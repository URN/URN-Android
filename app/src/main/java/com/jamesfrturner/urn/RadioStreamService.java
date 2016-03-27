package com.jamesfrturner.urn;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RadioStreamService extends Service {
    private final IBinder binder = new MyLocalBinder();
    private static final String streamUrl = "http://128.243.106.145:8080/urn_high.mp3";
    private Context context;
    private static MediaPlayer mediaPlayer;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_BUFFERING = 2;

    public RadioStreamService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setContext(Context c) {
        context = c;
    }

    private MediaPlayer getPlayer() {
        if (mediaPlayer != null) {
            return mediaPlayer;
        }

        MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "URN Android App");

        try {
            player.setDataSource(this.context, Uri.parse(streamUrl), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer = player;
        return player;
    }

    public boolean isPlaying() {
        return getPlayer().isPlaying();
    }

    public boolean play(final Handler.Callback callback) {
        if (!isNetworkConnected()) {
            return false;
        }

        MediaPlayer player = getPlayer();

        final Message message = new Message();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer player) {
                message.arg1 = STATE_PLAYING;
                callback.handleMessage(message);
                player.start();
            }
        });

        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        message.arg1 = STATE_BUFFERING;
                        callback.handleMessage(message);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        message.arg1 = STATE_PLAYING;
                        callback.handleMessage(message);
                        break;
                }
                return false;
            }
        });

        player.prepareAsync();

        return true;
    }

    public void stop() {
        getPlayer().reset();
        mediaPlayer = null;
        stopSelf();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public class MyLocalBinder extends Binder {
        RadioStreamService getService() {
            return RadioStreamService.this;
        }
    }
}
