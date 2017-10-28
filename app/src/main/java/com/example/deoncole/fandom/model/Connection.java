package com.example.deoncole.fandom.model;


public class Connection {

    public static final String CONNECTIONS_REF = "connections";
    public static final String USER_UID_COL = "userUid";

    private String artistId;
    private String userUid;

    public Connection() {
    }

    public Connection(String artistId, String userUid) {
        this.artistId = artistId;
        this.userUid = userUid;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "artistId='" + artistId + '\'' +
                ", userUid='" + userUid + '\'' +
                '}';
    }
}
