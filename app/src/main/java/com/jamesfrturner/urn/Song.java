package com.jamesfrturner.urn;

import android.text.format.DateUtils;
import java.util.Date;

public class Song {
    private String title;
    private String artist;
    private String show;
    private Date played;
    private int length;

    public Song(String title, String artist, String show, Date played, int length) {
        this.title = title;
        this.artist = artist;
        this.show = show;
        this.played = played;
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getShow() {
        return show;
    }

    public String getPlayed() {
        String ago = DateUtils.getRelativeTimeSpanString(this.played.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return ago;
    }

    public int getLength() {
        return length;
    }
}
