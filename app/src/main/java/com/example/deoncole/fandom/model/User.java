package com.example.deoncole.fandom.model;

public class User {

    public static final String USER_UID_COL = "userUid";

    private String userUid;

    public User(String userUid) {
        this.userUid = userUid;
    }

    public User() {
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(final String userUid) {
        this.userUid = userUid;
    }
}
