package com.jamesfrturner.urn;

import java.util.ArrayList;

public class Schedule {
    private ArrayList<Day> days;
    private Show currentShow;

    public Schedule() {
        days = new ArrayList<>();
        days.add(new Day("monday"));
        days.add(new Day("tuesday"));
        days.add(new Day("wednesday"));
        days.add(new Day("thursday"));
        days.add(new Day("friday"));
        days.add(new Day("saturday"));
        days.add(new Day("sunday"));
    }

    public Day getDay(String name) {
        for (Day day : days) {
            if (day.getName().equals(name)) {
                return day;
            }
        }
        return null;
    }

    public void setCurrentShow(Show show) {
        this.currentShow = show;
    }

    public Show getCurrentShow() {
        return currentShow;
    }
}
