package com.example.deoncole.fandom.model;


public class AudioData {

    private AudioMessage audioMessage;
    private String artistName;

    public AudioData(AudioMessage audioMessage, String artistName) {
        this.audioMessage = audioMessage;
        this.artistName = artistName;
    }

    public AudioMessage getAudioMessage() {
        return audioMessage;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public String toString() {
        return "AudioData{" +
                "audioMessage=" + audioMessage +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}
