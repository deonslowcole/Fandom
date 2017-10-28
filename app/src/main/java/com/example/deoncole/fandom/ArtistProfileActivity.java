package com.example.deoncole.fandom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.example.deoncole.fandom.actions.MusicTask;
import com.example.deoncole.fandom.data.ArtistPager;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Connection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

public class ArtistProfileActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    public static String ARTIST_EXTRA = "ARTIST_EXTRA";

    private ImageView connectImg;
    private int icons[] = {R.drawable.ic_action_posts, R.drawable.ic_action_music};
    private ViewPager viewPager;
    private Artist artist;
    private boolean mIsConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        connectImg = (ImageView) findViewById(R.id.connectImg);
        final ImageView artistImg = (ImageView) findViewById(R.id.artistImg);
        final TextView artistProfileNameTv = (TextView) findViewById(R.id.artProfileNameTv);
        viewPager = (ViewPager) findViewById(R.id.pager);

        Toolbar apToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(apToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Artist Profile");
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_action_posts));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_action_music));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Intent intent = getIntent();
        if (!intent.hasExtra(ARTIST_EXTRA)) return;

        artist = intent.getParcelableExtra(ARTIST_EXTRA);
        Picasso.with(this).load(artist.getImageUrl()).into(artistImg);
        artistProfileNameTv.setText(artist.getArtistName());

        ArtistPager pager = new ArtistPager(getSupportFragmentManager(), tabLayout.getTabCount(), artist);
        viewPager.setAdapter(pager);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            final TabLayout.Tab tabAt = tabLayout.getTabAt(i);
            if (tabAt != null) {
                tabAt.setIcon(icons[i]);
            }
        }

        String url = "https://api.deezer.com/search?q=" + artist.getArtistName();
        MusicTask task = new MusicTask(getApplicationContext());
        task.execute(url);
        final FireBaseProvider fireBaseProvider = ((FandomApp) getApplicationContext()).getFireBaseProvider();
        fetchConnectedState(fireBaseProvider);
    }

    private void fetchConnectedState(final FireBaseProvider fireBaseProvider) {
        FirebaseDatabase.getInstance().getReference("connections")
                .orderByChild("artistId")
                .startAt(artist.getId())
                .endAt(artist.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        for (DataSnapshot shot : ds.getChildren()) {
                            final Connection connection = shot.getValue(Connection.class);
                            if (connection != null &&
                                    fireBaseProvider.getConnectedUserUid().equals(connection.getUserUid())) {
                                displayConnectedState(fireBaseProvider, true);
                                return;
                            }
                        }
                        displayConnectedState(fireBaseProvider, false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TEST", "onCancelled() called with: databaseError = [" + databaseError + "]");
                    }
                });
    }

    private void displayConnectedState(
            final FireBaseProvider fireBaseProvider,
            final boolean isConnected) {
        mIsConnected = isConnected;
        showConnectedIconState(isConnected);
        connectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsConnected) {
                    Toast.makeText(getApplicationContext(),
                            "Your're Disconnected with the artist", Toast.LENGTH_SHORT).show();
                    fireBaseProvider.removeConnection(artist.getId(), FirebaseAuth.getInstance()
                            .getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(artist.getId());
                    showConnectedIconState(false);
                    mIsConnected = false;
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Your're Connected with the artist", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().subscribeToTopic(artist.getId());
                    fireBaseProvider.createConnection(artist.getId(), FirebaseAuth.getInstance()
                            .getCurrentUser().getUid());
                    showConnectedIconState(true);
                    mIsConnected = true;
                }
            }
        });
    }

    private void showConnectedIconState(boolean isConnected) {
        if (isConnected) {
            connectImg.setImageResource(R.drawable.minus_icon);
        }
        else {
            connectImg.setImageResource(R.drawable.add_icon);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Switch statement to check which
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_voice_notify:
                Intent intent = new Intent(getApplicationContext(), AudioMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.action_profile:
                ProfileActivity.open(this);
                break;
        }
        return true;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
