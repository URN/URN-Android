package com.jamesfrturner.urn;

import org.joda.time.LocalTime;

public class Show {
    public static final int CATEGORY_DAYTIME = 0;
    public static final int CATEGORY_SPORT = 1;
    public static final int CATEGORY_AFTER_DARK = 2;
    public static final int CATEGORY_NEWS = 3;
    public static final int CATEGORY_CULTURE = 4;
    public static final int CATEGORY_NONE = 4;

    private String title;
    private String description;
    private int category;
    private LocalTime startTime;
    private LocalTime endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        switch (category) {
            case CATEGORY_AFTER_DARK:
                return "After Dark";
            case CATEGORY_CULTURE:
                return "Culture";
            case CATEGORY_DAYTIME:
                return "Daytime";
            case CATEGORY_NEWS:
                return "News";
            case CATEGORY_SPORT:
                return "Sport";
            default:
                return "";
        }
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public static int getCategoryByName(String category) {
        switch (category.toLowerCase()) {
            case "daytime":
                return CATEGORY_DAYTIME;
            case "after dark":
                return CATEGORY_AFTER_DARK;
            case "news":
                return CATEGORY_NEWS;
            case "sport":
                return CATEGORY_SPORT;
            case "culture":
                return CATEGORY_CULTURE;
            default:
                return CATEGORY_NONE;
        }
    }

    public String getCategoryColor() {
        switch (category) {
            case CATEGORY_AFTER_DARK:
                return "#3B4251";
            case CATEGORY_CULTURE:
                return "#ED6346";
            case CATEGORY_DAYTIME:
                return "#E50052";
            case CATEGORY_NEWS:
                return "#009994";
            case CATEGORY_SPORT:
                return "#009FE3";
            default:
                return "#ffffff";
        }
    }
}
