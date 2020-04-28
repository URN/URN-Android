package com.jamesfrturner.urn;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RadioStreamService extends Service {
    public static final int STATE_PLAYING = 1;
    public static final int STATE_BUFFERING = 2;
    public static final int STATE_DUCKING = 3;
    public static final int STATE_STOPPED = 4;
    private static final int NOTIFICATION_ID = 1;
    private static final String HIGH_QUALITY_STREAM_URL = "https://corona.urn1350.net/radio/8000/urn_high.mp3";
    private static final String LOW_QUALITY_STREAM_URL = "https://corona.urn1350.net/radio/8000/urn_high.mp3";
    //I know i know but there's only one stream right now! And this is better than zero

    private final IBinder binder = new MyLocalBinder();
    private Context context;
    private static MediaPlayer mediaPlayer;
    private static boolean isLoading = false;
    private static boolean isPaused = false;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private Handler.Callback playCallback = null;

    public RadioStreamService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                final Message message = new Message();

                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        play(playCallback);
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        message.arg1 = STATE_STOPPED;
                        playCallback.handleMessage(message);
                        stop();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // Lost focus for a short time, but we have to stop
                        // playback. We don't release the media player because playback
                        // is likely to resume
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            isPaused = true;
                            message.arg1 = STATE_STOPPED;
                            playCallback.handleMessage(message);
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // Lost focus for a short time, but it's ok to keep playing
                        // at an attenuated level
                        if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                        break;
                }

            }
        };

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

    public boolean isLoading() {
        return isLoading;
    }

    public boolean play(final Handler.Callback callback) {
        if (!isNetworkConnected()) {
            return false;
        }

        playCallback = callback;

        MediaPlayer player = getPlayer();

        if (player.isPlaying()) {
            return true;
        }

        final Message message = new Message();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);

                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    message.arg1 = STATE_STOPPED;
                } else {
                    startMyOwnForeground();
                    message.arg1 = STATE_PLAYING;
                    player.start();
                }

                callback.handleMessage(message);
                isLoading = false;
            }
        });

        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        isLoading = true;
                        message.arg1 = STATE_BUFFERING;
                        callback.handleMessage(message);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        isLoading = false;
                        message.arg1 = STATE_PLAYING;
                        callback.handleMessage(message);
                        break;
                }
                return false;
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
                play(callback);
                message.arg1 = STATE_BUFFERING;
                callback.handleMessage(message);
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stop();
                message.arg1 = STATE_STOPPED;
                callback.handleMessage(message);
                return false;
            }
        });

        if (isPaused) {
            player.start();
            isLoading = true;
            isPaused = false;
        } else {
            try {
                isLoading = true;
                player.prepareAsync();
            } catch (IllegalStateException e) {
                isLoading = false;
                message.arg1 = STATE_STOPPED;
                callback.handleMessage(message);
                stop();
            }
        }

        return true;
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isLoading = false;
        isPaused = false;
        stopSelf();
        stopForeground(true);
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

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "com.jamesfrturner.urn";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_wings)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_urn))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.app_name_long))
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }
}
