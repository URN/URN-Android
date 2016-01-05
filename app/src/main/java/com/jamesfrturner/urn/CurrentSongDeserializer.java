package com.jamesfrturner.urn;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

public class CurrentSongDeserializer implements JsonDeserializer<Song> {
    @Override
    public Song deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Song song = new Song();
        JsonObject jsonObject = (JsonObject) json;

        String title = jsonObject.get("title").getAsString();
        String artist = jsonObject.get("artist").getAsString();
        int startTimeUnixTimestamp = jsonObject.get("start_time").getAsInt();
        int duration = jsonObject.get("duration").getAsInt();

        Date startTime = new java.util.Date((long) startTimeUnixTimestamp * 1000);

        // Convert duration from mAirList times units to seconds
        // https://www.mairlist.com/forum/index.php?topic=4182.0
        duration /= 10000000;

        song.setTitle(title);
        song.setArtist(artist);
        song.setStartTime(startTime);
        song.setDurationSeconds(duration);

        return song;
    }
}
