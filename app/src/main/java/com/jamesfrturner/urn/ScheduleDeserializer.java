package com.jamesfrturner.urn;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.joda.time.LocalTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduleDeserializer implements JsonDeserializer {

    @Override
    public Schedule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Schedule schedule = new Schedule();
        JsonObject jsonObject = (JsonObject) json;

        List<String> dayNames = new ArrayList<>();
        dayNames.add("monday");
        dayNames.add("tuesday");
        dayNames.add("wednesday");
        dayNames.add("thursday");
        dayNames.add("friday");
        dayNames.add("saturday");
        dayNames.add("sunday");

        for (String dayName : dayNames) {
            JsonArray day = jsonObject.getAsJsonArray(dayName);

            for (Iterator<JsonElement> iterator = day.iterator(); iterator.hasNext(); ) {
                JsonObject showJson = (JsonObject) iterator.next();

                Show show = new Show();
                show.setTitle(showJson.get("name").getAsString());
                show.setDescription(showJson.get("description").getAsString());
                show.setCategory(Show.getCategoryByName(showJson.get("category").getAsString()));

                String startTimeString = showJson.get("from").getAsString();
                LocalTime startTime = new LocalTime(
                        Integer.parseInt(startTimeString.substring(0, 2)),
                        Integer.parseInt(startTimeString.substring(3, 4))
                );

                String endTimeString = showJson.get("to").getAsString();
                LocalTime endTime = new LocalTime(
                        Integer.parseInt(endTimeString.substring(0, 2)),
                        Integer.parseInt(endTimeString.substring(3, 4))
                );

                show.setStartTime(startTime);
                show.setEndTime(endTime);

                try {
                    schedule.addShow(Day.getDayNumberByName(dayName), show);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return schedule;
    }
}
