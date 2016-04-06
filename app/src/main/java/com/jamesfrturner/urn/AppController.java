package com.jamesfrturner.urn;

import android.app.Application;

import com.android.volley.Response;

public class AppController extends Application {
    private Schedule schedule;
    private RestClient restClient;

    @Override
    public void onCreate() {
        restClient = new RestClient(getApplicationContext());
        super.onCreate();
    }

    public void getSchedule(final Response.Listener<Schedule> listener, Response.ErrorListener errorListener) {
        if (schedule != null) {
            listener.onResponse(schedule);
            return;
        }

        restClient.getSchedule(
                new Response.Listener<Schedule>() {
                    @Override
                    public void onResponse(Schedule s) {
                        schedule = s;
                        listener.onResponse(schedule);
                    }
                },
                errorListener
        );
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
