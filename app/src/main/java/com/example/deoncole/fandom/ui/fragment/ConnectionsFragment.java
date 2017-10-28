package com.example.deoncole.fandom.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.deoncole.fandom.ArtistProfileActivity;
import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.data.ListViewAdapter;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Connection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConnectionsFragment extends Fragment {

    private ListViewAdapter connectionsAdapter;

    //Create a new instance of the fragment
    public static ConnectionsFragment newInstance() {
        return new ConnectionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the layout
        View v = inflater.inflate(R.layout.fragment_my_connections, container, false);

        //Set the view to the list view
        final ListView connectionLv = (ListView) v.findViewById(R.id.connectionsList);

        connectionsAdapter = new ListViewAdapter(getContext(), new ArrayList<Artist>());
        connectionLv.setAdapter(connectionsAdapter);

        connectionLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = connectionsAdapter.getItem(position);

                //Create an intent that will go to the artist profile activity. Put extras for the
                // selected artist name and if the artist is from the connected list. Start the
                // activity.
                Intent profileIntent = new Intent(getContext(), ArtistProfileActivity.class);
                profileIntent.putExtra(ArtistProfileActivity.ARTIST_EXTRA, artist);
                startActivity(profileIntent);
            }
        });
        //return the inflated view
        return v;
    }

    private void fetchContent() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("connections")
                .orderByChild("userUid")
                .startAt(currentUserUid).endAt(currentUserUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot objSnapShot : dataSnapshot.getChildren()) {
                            Connection connection = objSnapShot.getValue(Connection.class);

                            db.getReference("artists")
                                    .orderByKey()
                                    .startAt(connection.getArtistId())
                                    .endAt(connection.getArtistId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot ds) {
                                            for (DataSnapshot shot : ds.getChildren()) {
                                                Artist artist = shot.getValue(Artist.class);
                                                artist.setId(shot.getKey());
                                                connectionsAdapter.addItem(artist);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (connectionsAdapter != null) {
            connectionsAdapter.clear();
            fetchContent();
        }
    }
}
