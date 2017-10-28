package com.example.deoncole.fandom.model;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Broadcast {

    private String artistId;
    private String message;
    private long timestamp;
    @Nullable
    private String bitmapUrl;

    public Broadcast() {
    }

    public Broadcast(String artistId, String message, Long timestamp,
                     @Nullable String bitmapUrl) {
        this.artistId = artistId;
        this.message = message;
        this.timestamp = timestamp;
        this.bitmapUrl = bitmapUrl;
    }

    public String getBitmapUrl() {
        return bitmapUrl;
    }

    public void setBitmapUrl(@Nullable String bitmapUrl) {
        this.bitmapUrl = bitmapUrl;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        String datePattern = "EEE, MMM d, h:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date messageCreationDate = new Date(timestamp);
        return dateFormat.format(messageCreationDate);
    }

    @Override
    public String toString() {
        return "Broadcast{" +
                "artistId='" + artistId + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", bitmapUrl=" + bitmapUrl +
                '}';
    }
}
