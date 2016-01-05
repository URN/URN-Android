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
    private ResultReadyCallback callback;
    private static final String BASE_URL = "http://urn1350.net";
    private ApiService service;

    public RestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Song.class, new CurrentSongDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();

        service = retrofit.create(ApiService.class);
    }

    public void requestCurrentSong() {
        Call<Song> request = service.getCurrentSong();
        request.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Response<Song> response) {
                if (!response.isSuccess()) {
                    callback.resultReady(null);
                    return;
                }

                callback.resultReady(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                callback.resultReady(null);
            }
        });
    }

    public void setCallback(ResultReadyCallback callback) {
        this.callback = callback;
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public interface ResultReadyCallback {
        void resultReady(Song currentSong);
    }

}