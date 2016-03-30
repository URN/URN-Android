package com.jamesfrturner.urn;

import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Schedule {
    Map<Integer, Day> days = new HashMap<>();

    public Schedule() {
        days.put(Calendar.MONDAY, new Day());
        days.put(Calendar.TUESDAY, new Day());
        days.put(Calendar.WEDNESDAY, new Day());
        days.put(Calendar.THURSDAY, new Day());
        days.put(Calendar.FRIDAY, new Day());
        days.put(Calendar.SATURDAY, new Day());
        days.put(Calendar.SUNDAY, new Day());
    }

    public Day getDay(int day) {
        return days.get(day);
    }

    public Show getCurrentShow() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        LocalTime currentTime = new LocalTime();
        return getDay(day).getShowAtTime(currentTime);
    }

    public void addShow(int day, Show show) {
        getDay(day).addShow(show);
    }
}
