package com.example.deoncole.fandom.model;

public class LiveBroadcast {

    private String artistId;
    private long timestamp;

    public LiveBroadcast() {
    }

    public LiveBroadcast(String artistId, Long timestamp) {
        this.artistId = artistId;
        this.timestamp = timestamp;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "LiveBroadcast{" +
                "artistId='" + artistId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
