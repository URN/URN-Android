package com.jamesfrturner.urn;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

public class RadioStream {
    private static final String TAG = "RADIO";
    private static final String URL = "http://128.243.106.145:8080/urn_high.mp3";
    private Context context;

    private MediaPlayer mediaPlayer;


    public RadioStream(Context context) {
        this.context = context;

        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);




    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        try {
            this.mediaPlayer.setDataSource(this.URL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mediaPlayer.start();
        Toast.makeText(this.context, "Stream started", Toast.LENGTH_LONG).show();
    }

    public void stop() {
        this.mediaPlayer.reset();
    }
}
