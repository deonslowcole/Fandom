package com.example.deoncole.fandom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.deoncole.fandom.AudioMessageAdapter.AudioMessageSelectionListener;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.AudioData;
import com.example.deoncole.fandom.model.AudioMessage;
import com.example.deoncole.fandom.model.Connection;
import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcAudioPlayer;
import com.example.jean.jcplayer.JcPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AudioMessageActivity extends AppCompatActivity {

    private AudioMessageAdapter mAdapter;
    private JcPlayerView jcPlayerView;
    private Set<String> displayedAudioKeys = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_message);

        Toolbar audioToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(audioToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Audio Messages");
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        jcPlayerView = (JcPlayerView) findViewById(R.id.jcPlayer);

        RecyclerView audioMessagesRv = (RecyclerView) findViewById(R.id.rv_audio_messages);
        audioMessagesRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AudioMessageAdapter();
        audioMessagesRv.setAdapter(mAdapter);

        mAdapter.setAudioMessageSelectionListener(new AudioMessageSelectionListener() {
            @Override
            public void onAudioMessageSelected(final JcAudio jcAudio) {
                jcPlayerView.playAudio(jcAudio);
            }
        });

        String currentUserUid = currentUser.getUid();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference(Connection.CONNECTIONS_REF)
                .orderByChild(Connection.USER_UID_COL)
                .startAt(currentUserUid).endAt(currentUserUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot shot : dataSnapshot.getChildren()) {
                            final Connection connection = shot.getValue(Connection.class);
                            fetchAudioMessage(connection, db);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TEST", "onCancelled() called with: databaseError = [" + databaseError + "]");
                    }
                });
    }

    private void fetchAudioMessage(final Connection connection, final FirebaseDatabase db) {
        FirebaseDatabase.getInstance().getReference("artists")
                .orderByKey()
                .startAt(connection.getArtistId())
                .endAt(connection.getArtistId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        for (DataSnapshot shot : ds.getChildren()) {
                            final Artist a = shot.getValue(Artist.class);
                            if (a != null) {
                                a.setId(shot.getKey());
                                fetchArtistAudioMessages(db, a, connection);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TEST", "onCancelled() called with: databaseError = [" + databaseError + "]");
                    }
                });
    }

    private void fetchArtistAudioMessages(final FirebaseDatabase db, final Artist a, final Connection connection) {
        final String artistName = a.getArtistName();
        db.getReference(AudioMessage.AUDIO_MESSAGES_REF)
                .orderByChild(AudioMessage.ARTIST_ID_COL)
                .startAt(a.getId())
                .endAt(a.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds2) {
                        List<AudioData> audios = new ArrayList<>();
                        for (DataSnapshot snapshot : ds2.getChildren()) {
                            final AudioMessage audioMessage = snapshot.getValue(AudioMessage.class);
                            if(audioMessage == null) continue;

                            final AudioMessage am = new AudioMessage(connection.getArtistId(),
                                    audioMessage.getTitle(), audioMessage.getUrl());
                            am.setId(snapshot.getKey());

                            final AudioData audioData = new AudioData(am, artistName);
                            audios.add(audioData);
                        }
                        onNewAudioMessages(audios);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TEST", "onCancelled() called with: databaseError = [" + databaseError + "]");
                    }
                });
    }

    private void onNewAudioMessages(final List<AudioData> audios) {
        List<JcAudio> jcAudios = new ArrayList<>();
        for (final AudioData ad : audios) {
            if(!displayedAudioKeys.contains(ad.getAudioMessage().getId())) {
                displayedAudioKeys.add(ad.getAudioMessage().getId());
                final JcAudio audio = JcAudio.createFromURL(
                        String.format("%s by %s", ad.getAudioMessage().getTitle(), ad.getArtistName()),
                        ad.getAudioMessage().getUrl());
                jcAudios.add(audio);
            }
        }

        if (!jcAudios.isEmpty()) {
            mAdapter.addItems(jcAudios);
            jcPlayerView.initPlaylist(mAdapter.getItems());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                jcPlayerView.kill();
        }
        return true;
    }
}

