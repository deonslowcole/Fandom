package com.example.deoncole.fandom.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.deoncole.fandom.ArtistProfileActivity;
import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.data.ListViewAdapter;
import com.example.deoncole.fandom.model.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends ListFragment implements SearchView.OnQueryTextListener{

    private ListViewAdapter lvAdapter;
    private ArrayList<Artist> artists = new ArrayList<>();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = mDatabase.getReference("artists");
    private ValueEventListener valueEventListener;

    public static SearchFragment newInstance(){
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        SearchView mArtistSv = (SearchView) v.findViewById(R.id.artistSearchView);
        mArtistSv.setOnQueryTextListener(this);

        ListView searchArtistLv = (ListView) v.findViewById(android.R.id.list);

        lvAdapter = new ListViewAdapter(getContext(), artists);
        searchArtistLv.setAdapter(lvAdapter);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()){
                    Artist artist = objSnapShot.getValue(Artist.class);
                    artist.setId(objSnapShot.getKey());
                    lvAdapter.addItem(artist);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);

        final String artistName = lvAdapter.getItem(position).getArtistName();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ("artists".equals(snapshot.getKey())) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            Artist artist = shot.getValue(Artist.class);
                            if (artist.getArtistName().equals(artistName)) {
                                artist.setId(shot.getKey());
                                Intent profileIntent = new Intent(SearchFragment.this.getActivity(),
                                        ArtistProfileActivity
                                                .class);
                                profileIntent.putExtra(ArtistProfileActivity.ARTIST_EXTRA, artist);
                                dbRef.removeEventListener(valueEventListener);
                                startActivity(profileIntent);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        lvAdapter.filter(newText);
        return false;
    }
}
