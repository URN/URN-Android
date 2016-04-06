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
        for (Show show : shows) {
            LocalTime startTime = show.getStartTime();
            LocalTime endTime = show.getEndTime();

            if (startTime.isAfter(endTime)) {
                if (time.compareTo(startTime) >= 0 || time.compareTo(endTime) < 0) {
                    return show;
                }
            } else {
                if (startTime.compareTo(time) <= 0 && time.compareTo(endTime) < 0) {
                    return show;
                }
            }
        }
        return null;
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
