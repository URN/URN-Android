package com.jamesfrturner.urn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class RestClient{

    private static RestClient instance = null;
    private CurrentSongReadyCallback currentSongCallback;
    private ScheduleReadyCallback scheduleCallback;
    private static final String BASE_URL = "http://urn1350.net";
    private ApiService service;

    public RestClient() {

    }

    public void requestCurrentSong() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Song.class, new CurrentSongDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();

        service = retrofit.create(ApiService.class);

        Call<Song> request = service.getCurrentSong();
        request.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Response<Song> response) {
                if (!response.isSuccess()) {
                    currentSongCallback.resultReady(null);
                    return;
                }

                currentSongCallback.resultReady(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                currentSongCallback.resultReady(null);
            }
        });
    }

    public void requestSchedule() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Schedule.class, new ScheduleDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();

        service = retrofit.create(ApiService.class);

        Call<Schedule> request = service.getSchedule();
        request.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Response<Schedule> response) {
                if (!response.isSuccess()) {
                    scheduleCallback.resultReady(null);
                    return;
                }

                scheduleCallback.resultReady(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                scheduleCallback.resultReady(null);
            }
        });
    }

    public void setCurrentSongCallback(CurrentSongReadyCallback callback) {
        this.currentSongCallback = callback;
    }

    public void setScheduleCallback(ScheduleReadyCallback callback) {
        this.scheduleCallback = callback;
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public interface CurrentSongReadyCallback {
        void resultReady(Song currentSong);
    }

    public interface ScheduleReadyCallback {
        void resultReady(Schedule schedule);
    }

}