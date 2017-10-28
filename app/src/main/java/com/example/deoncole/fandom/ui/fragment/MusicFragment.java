package com.example.deoncole.fandom.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.deoncole.fandom.ArtistFeedActivity;
import com.example.deoncole.fandom.ArtistSongPreviewActivity;
import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.model.ArtistSongs;

import java.util.ArrayList;

public class MusicFragment extends ListFragment {

    public static ListView musicLv;
    public static ArrayList<ArtistSongs> previewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.artist_music, container, false);

        musicLv = (ListView)v.findViewById(android.R.id.list);

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String titleOfSong = previewList.get(position).getSongTitle();
        String linkToSong = previewList.get(position).getSongLink();

        Intent songIntent = new Intent(getContext(), ArtistSongPreviewActivity.class);
        songIntent.putExtra(ArtistSongPreviewActivity.SONG_NAME, titleOfSong);
        songIntent.putExtra(ArtistSongPreviewActivity.SONG_LINK, linkToSong);
        startActivity(songIntent);

        System.out.println(linkToSong);

    }
}