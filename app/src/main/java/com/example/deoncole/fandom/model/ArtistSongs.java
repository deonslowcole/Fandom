package com.example.deoncole.fandom.model;

import java.io.Serializable;

public class ArtistSongs implements Serializable{

    private final String songTitle;
    private final String songLink;

    public ArtistSongs(String songTitle, String songLink) {
        this.songTitle = songTitle;
        this.songLink = songLink;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongLink() {
        return songLink;
    }
}
