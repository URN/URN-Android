package com.jamesfrturner.urn;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RadioStream {
    private static final String URL = "http://128.243.106.145:8080/urn_high.mp3";
    private Context context;
    private static MediaPlayer mediaPlayer;


    public RadioStream(Context context) {
        this.context = context;
    }

    private MediaPlayer getPlayer() {
        if (this.mediaPlayer != null) {
            return this.mediaPlayer;
        }

        MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Map<String, String> headers = new HashMap<String, String>();;
        headers.put("User-Agent", "URN Android App");

        try {
            player.setDataSource(this.context, Uri.parse(this.URL), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mediaPlayer = player;
        return player;
    }

    public boolean isPlaying() {
        return getPlayer().isPlaying();
    }

    public void play(final CallbackInterface callback) {
        if (!MainActivity.isConnected()) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
            callback.execute();
            return;
        }

        MediaPlayer player = getPlayer();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer player) {
                callback.execute();
                player.start();
            }
        });

        player.prepareAsync();
    }

    public void stop() {
        getPlayer().reset();
        this.mediaPlayer = null;
    }
}
