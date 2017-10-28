package com.example.deoncole.fandom.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.ui.view.broadcast.BroadcastsView;

public class ArtistFeedFragment extends Fragment{

    public static ArtistFeedFragment newInstance() {
        Bundle args = new Bundle();
        ArtistFeedFragment fragment = new ArtistFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist_feed, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final BroadcastsView broadcastsView = (BroadcastsView) view.findViewById(R.id
                .artist_feed_list);
        broadcastsView.displayedBroadcasts(null);
    }
}
