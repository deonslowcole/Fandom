package com.example.deoncole.fandom.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.ui.view.broadcast.BroadcastsView;

public class PostFragment extends Fragment{

    public static final String ARTIST_BUNDLE_KEY = "artist";

    public static PostFragment newInstance(Artist artist) {
        Bundle args = new Bundle();
        PostFragment fragment = new PostFragment();
        args.putParcelable(ARTIST_BUNDLE_KEY, artist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist_post, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final BroadcastsView broadcastsView = (BroadcastsView) view.findViewById(R.id
                .post_broadcasts_view);
        final Artist artist = getArguments().getParcelable(ARTIST_BUNDLE_KEY);
        broadcastsView.displayedBroadcasts(artist);
    }
}