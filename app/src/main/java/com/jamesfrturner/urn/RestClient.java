package com.jamesfrturner.urn;

import android.app.Activity;
import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RestClient {
    private static final String CURRENT_SONG_URL = "http://urn1350.net/api/current_song";
    private static final String SCHEDULE_URL = "http://urn1350.net/api/schedule/week";
    private static final String SEND_MESSAGE_URL = "http://urn1350.net/api/send_message";
    private static final String USER_AGENT = "URN Android - " + System.getProperty("http.agent");
    private RequestQueue requestQueue;
    private HashMap<String, String> headers;

    public RestClient(Context content) {
        this.requestQueue = Volley.newRequestQueue(content);
        this.headers = new HashMap<>();
        this.headers.put("User-agent", USER_AGENT);
    }

    public void getCurrentSong(Response.Listener<Song> listener, Response.ErrorListener errorListener) {
        CurrentSongRequest<Song> request = new CurrentSongRequest<>(
                Request.Method.GET,
                CURRENT_SONG_URL,
                Song.class,
                headers,
                listener,
                errorListener);

        requestQueue.add(request);
    }

    public void getSchedule(Response.Listener<Schedule> listener, Response.ErrorListener errorListener) {
        ScheduleRequest<Schedule> request = new ScheduleRequest<>(
                Request.Method.GET,
                SCHEDULE_URL,
                Schedule.class,
                headers,
                listener,
                errorListener);

        requestQueue.add(request);
    }

    public void sendMessage(final String message, final Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        Request<Boolean> request = new Request<Boolean>(Request.Method.POST, SEND_MESSAGE_URL, errorListener) {
            @Override
            protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {
                String jsonString;
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    jsonString = new String(response.data);
                }

                JsonElement json = new JsonParser().parse(jsonString);
                String status = json.getAsJsonObject().get("status").getAsString();

                boolean success = status.equals("success");

                return Response.success(success, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(Boolean response) {
                listener.onResponse(response);
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("message", message.trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders(){
                return headers;
            }
        };
        requestQueue.add(request);
    }
}
