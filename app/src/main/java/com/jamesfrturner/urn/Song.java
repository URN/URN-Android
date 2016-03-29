package com.jamesfrturner.urn;

public class Song {
    private String title;
    private String artist;
    private long startTimeMillis;
    private int durationMillis;

    public Song() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setDurationMillis(int duration) {
        this.durationMillis = duration;
    }

    public int getDurationMillis() {
        return durationMillis;
    }

    public int getProgress() {
        long currentTimeMilliseconds = System.currentTimeMillis();
        long startTimeMilliseconds = getStartTimeMillis();
        long durationMilliseconds = getDurationMillis();

        long progressMilliseconds = currentTimeMilliseconds - startTimeMilliseconds;

        double progressPercent = (double) ((float)progressMilliseconds / durationMilliseconds) * 100;

        if (progressPercent > 100) {
            return 100;
        }
        else if (progressPercent < 0) {
            return 0;
        }
        else {
            return (int) progressPercent;
        }
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
