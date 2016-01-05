package com.jamesfrturner.urn;

import java.util.ArrayList;

public class Day {
    private String name;
    private ArrayList<Show> shows;

    public Day(String name) {
        this.name = name;
        this.shows = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addShow(Show show) {
        this.shows.add(show);
    }
}
