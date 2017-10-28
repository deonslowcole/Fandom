package com.example.deoncole.fandom.model;


public class AudioMessage {

    public static final String AUDIO_MESSAGES_REF = "audio_messages";
    public static final String ARTIST_ID_COL = "artistId";

    private String id;
    private String artistId;
    private String title;
    private String url;

    public AudioMessage() {
    }

    public AudioMessage(String artistId, final String title, String url) {
        this.artistId = artistId;
        this.title = title;
        this.url = url;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AudioMessage{" +
                "id='" + id + '\'' +
                ", artistId='" + artistId + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

