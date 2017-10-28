package com.example.deoncole.fandom.ui.view.broadcast;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Broadcast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BroadcastsView extends LinearLayout {

    private BroadcastAdapter broadcastAdapter;

    public BroadcastsView(Context context) {
        this(context, null);
    }

    public BroadcastsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BroadcastsView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.broadcasts_layout, this, true);
        final RecyclerView broadcastsRv = (RecyclerView) findViewById(R.id.broadcasts_rv);
        broadcastsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        broadcastAdapter = new BroadcastAdapter();
        broadcastsRv.setAdapter(broadcastAdapter);
    }

    public void displayedBroadcasts(@Nullable final Artist displayedArtist) {
        loadBroadcasts(displayedArtist);
    }

    private void loadBroadcasts(final Artist artist) {
        DatabaseReference broadcastsRef = FirebaseDatabase.getInstance().getReference("broadcasts");
        Query query = broadcastsRef;
        if (artist != null) {
            query = broadcastsRef.orderByChild("artistId")
                    .startAt(artist.getId())
                    .endAt(artist.getId());
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                broadcastAdapter.clearData();
                onBroadcastData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TEST", "onCancelled() called with: databaseError = [" + databaseError + "]");
            }
        });
    }

    private void onBroadcastData(DataSnapshot dataSnapshot) {
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Broadcast value = snapshot.getValue(Broadcast.class);
            if (value != null && value.getArtistId() != null) {
                fetchItemData(value);
            }
        }
    }

    private void fetchItemData(final Broadcast broadcast) {
        FirebaseDatabase.getInstance().getReference("artists")
                .orderByKey()
                .startAt(broadcast.getArtistId())
                .endAt(broadcast.getArtistId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("TEST", "onDataChange: ");

                        final List<BroadcastViewItem> items = new ArrayList<>();
                        for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                            Artist artist = artistSnapshot.getValue(Artist.class);
                            items.add(new BroadcastViewItem(broadcast, artist));
                        }

                        Log.d("TEST", "onDataChange: items = " + items);
                        broadcastAdapter.addItems(items);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TEST", "onCancelled: ");
                    }
                });
    }
}
