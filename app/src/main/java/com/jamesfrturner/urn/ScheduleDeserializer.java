package com.jamesfrturner.urn;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class ScheduleDeserializer implements JsonDeserializer<Schedule> {
    @Override
    public Schedule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Schedule schedule = new Schedule();
        JsonObject jsonObject = (JsonObject) json;

        for (Map.Entry<String,JsonElement> entry : jsonObject.entrySet()) {
            for (JsonElement showEl : entry.getValue().getAsJsonArray()) {
                JsonObject showObj = (JsonObject) showEl;

                Show show = new Show(showObj.get("name").getAsString());

                if (showObj.get("live").getAsBoolean()) {
                    schedule.setCurrentShow(show);
                }

                schedule.getDay(entry.getKey()).addShow(show);
            }
        }

        return schedule;
    }
}
