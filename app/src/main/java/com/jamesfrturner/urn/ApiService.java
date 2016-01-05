package com.jamesfrturner.urn;

import retrofit.Call;
import retrofit.http.GET;

public interface ApiService {
    @GET("/api/current_song")
    Call<Song> getCurrentSong();
}