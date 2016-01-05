package com.jamesfrturner.urn;

import java.util.Date;

public class Song {
    private String title;
    private String artist;
    private Date startTime;
    private int durationSeconds;
    private String show;

    public Song() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public int getPercentagePlayed() {
        // TODO return between 0 and 100
        return 0;
    }

    public String toString() {
        return getTitle() + " - " + getArtist();
    }
}
