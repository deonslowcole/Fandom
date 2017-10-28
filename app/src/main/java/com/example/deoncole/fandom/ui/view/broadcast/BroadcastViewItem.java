package com.example.deoncole.fandom.ui.view.broadcast;

import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Broadcast;

public final class BroadcastViewItem {

    private final Broadcast broadcast;
    private final Artist artist;

    BroadcastViewItem(Broadcast broadcast, Artist artist) {
        this.broadcast = broadcast;
        this.artist = artist;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    public Artist getArtist() {
        return artist;
    }
}
