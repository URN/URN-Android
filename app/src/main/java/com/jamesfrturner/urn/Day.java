package com.jamesfrturner.urn;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Day {
    private List<Show> shows;

    public Day() {
        shows = new ArrayList<>();
    }

    public void addShow(Show show) {
        shows.add(show);
    }

    public Show getShowAtTime(LocalTime time) {
        // TODO return show that's live at that time;
        return shows.get(5);
    }

    public static int getDayNumberByName(String name) throws Exception {
        switch (name.toLowerCase()) {
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
            case "sunday":
                return Calendar.SUNDAY;
            default:
                throw new Exception("Invalid day name");
        }
    }
}
