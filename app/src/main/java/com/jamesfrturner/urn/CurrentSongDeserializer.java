package com.jamesfrturner.urn;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.math.BigInteger;

public class CurrentSongDeserializer implements JsonDeserializer {

    @Override
    public Song deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Song song = new Song();
        JsonObject jsonObject = (JsonObject) json;

        String title = jsonObject.get("title").getAsString();
        String artist = jsonObject.get("artist").getAsString();
        long startTimeMilliseconds = jsonObject.get("start_time").getAsLong() * 1000;

        BigInteger duration;

        try {
            duration = jsonObject.get("duration").getAsBigInteger();
        } catch (Exception e) {
            duration = new BigInteger("0");
        }

        // Convert duration from mAirList times units to milliseconds
        // https://www.mairlist.com/forum/index.php?topic=4182.0
        BigInteger durationMilliseconds = duration.divide(new BigInteger("10000"));

        song.setTitle(title);
        song.setArtist(artist);
        song.setStartTimeMillis(startTimeMilliseconds);
        song.setDurationMillis(durationMilliseconds.intValue());

        return song;
    }
}
