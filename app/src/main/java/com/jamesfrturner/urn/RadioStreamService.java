package com.jamesfrturner.urn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RadioStreamService extends Service {
    public static final int STATE_PLAYING = 1;
    public static final int STATE_BUFFERING = 2;
    private static final int NOTIFICATION_ID = 1;
    private static final String HIGH_QUALITY_STREAM_URL = "http://posurnl.nottingham.ac.uk:8080/urn_high.mp3";
    private static final String LOW_QUALITY_STREAM_URL = "http://posurnl.nottingham.ac.uk:8080/urn_mobile.mp3";

    private final IBinder binder = new MyLocalBinder();
    private Context context;
    private static MediaPlayer mediaPlayer;

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

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
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
            player.setDataSource(this.context, Uri.parse(getStreamUrl()), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer = player;
        return player;
    }

    private String getStreamUrl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean highQualityPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_HIGH_QUALITY_STREAM, false);
        return highQualityPref ? HIGH_QUALITY_STREAM_URL : LOW_QUALITY_STREAM_URL;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && getPlayer().isPlaying();
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
                startForeground(NOTIFICATION_ID, getNotification());
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
        stopForeground(true);
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name_full))
                        .setContentText("Prisoner - The Weeknd");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(pi);
        return builder.build();
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
