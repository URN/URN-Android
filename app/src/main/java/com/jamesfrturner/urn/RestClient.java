package com.jamesfrturner.urn;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class RestClient {
    private static final String CURRENT_SONG_URL = "http://urn1350.net/api/current_song";
    private static final String SCHEDULE_URL = "http://urn1350.net/api/schedule/week";
    private RequestQueue requestQueue;

    public RestClient(Activity activity) {
        this.requestQueue = Volley.newRequestQueue(activity);
    }

    public void getCurrentSong(Response.Listener<Song> listener, Response.ErrorListener errorListener) {
        CurrentSongRequest<Song> request = new CurrentSongRequest<>(
                Request.Method.GET,
                CURRENT_SONG_URL,
                Song.class,
                null,
                listener,
                errorListener);

        requestQueue.add(request);
    }

    public void getSchedule(Response.Listener<Schedule> listener, Response.ErrorListener errorListener) {
        ScheduleRequest<Schedule> request = new ScheduleRequest<>(
                Request.Method.GET,
                SCHEDULE_URL,
                Schedule.class,
                null,
                listener,
                errorListener);

        requestQueue.add(request);
    }
}
