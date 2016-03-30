package com.jamesfrturner.urn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class MainActivity extends AppCompatActivity {

    private RadioStreamService streamService;
    private boolean streamServiceIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getResources().getString(R.string.app_name_long));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Intent intent = new Intent(this, RadioStreamService.class);
        startService(intent);
        bindService(intent, streamServiceConnection, BIND_AUTO_CREATE);

        final RestClient restClient = new RestClient(this);
        startCurrentSongPolling(restClient);
    }

    private void startCurrentSongPolling(final RestClient restClient) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Runnable that = this;
                restClient.getCurrentSong(
                        new Response.Listener<Song>() {
                            @Override
                            public void onResponse(Song currentSong) {
                                int delay = 10000;

                                if (currentSong.getProgress() != 100) {
                                    delay = currentSong.getRemainingDurationMillis();
                                }
                                else {
                                    currentSong = null;
                                }

                                onCurrentSongChange(currentSong);

                                handler.postDelayed(that, delay);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onCurrentSongChange(null);
                                handler.postDelayed(that, 10000);
                                // TODO Hide current song view / show error
                            }
                        }
                );
            }
        };

        handler.postDelayed(runnable, 100);
    }

    private void setPlayButtonListeners() {
        final Context context = getApplicationContext();
        final PlayButtonAnimator pba = new PlayButtonAnimator(MainActivity.this, this);
        final Button playButton = (Button) findViewById(R.id.play_button);

        streamService.setContext(context);

        if (streamService.isPlaying()) {
            try {
                pba.changeState(PlayButtonAnimator.STATE_PLAYING);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (streamService.isPlaying()) {
                    try {
                        pba.changeState(PlayButtonAnimator.STATE_STOPPED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    streamService.stop();
                }
                else {
                    try {
                        pba.changeState(PlayButtonAnimator.STATE_LOADING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    playButton.setEnabled(false);

                    boolean success = streamService.play(new Handler.Callback() {
                        public boolean handleMessage(Message msg) {
                            try {
                                switch (msg.arg1) {
                                    case RadioStreamService.STATE_PLAYING:
                                        pba.changeState(PlayButtonAnimator.STATE_PLAYING);
                                        break;
                                    case RadioStreamService.STATE_BUFFERING:
                                        pba.changeState(PlayButtonAnimator.STATE_LOADING);
                                        break;
                                    default:
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            playButton.setEnabled(true);
                            return false;
                        }
                    });

                    if (!success) {
                        try {
                            pba.changeState(PlayButtonAnimator.STATE_STOPPED);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        playButton.setEnabled(true);

                        String errorMessage = getResources().getString(R.string.stream_error);

                        Toast.makeText(
                                MainActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        });
    }

    private void onCurrentSongChange(Song currentSong) {
        TextView artistTextView = (TextView) findViewById(R.id.current_song_artist);
        TextView titleTextView = (TextView) findViewById(R.id.current_song_title);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.current_song_progress);

        if (currentSong == null) {
            artistTextView.setText(getResources().getString(R.string.current_song_artist_placeholder));
            titleTextView.setText(getResources().getString(R.string.current_song_title_placeholder));
            progressBar.setProgress(0);
            return;
        }

        artistTextView.setText(currentSong.getArtist());
        titleTextView.setText(currentSong.getTitle());

        final int progress = currentSong.getProgress();
        final int remainingMilliseconds = currentSong.getRemainingDurationMillis();
        final int animationInDuration = 1000;

        CurrentSongProgressBarAnimation animIn = new CurrentSongProgressBarAnimation(progressBar, 0, progress);
        animIn.setDuration(animationInDuration);
        progressBar.startAnimation(animIn);

        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CurrentSongProgressBarAnimation animOut = new CurrentSongProgressBarAnimation(progressBar, progress, 100);
                animOut.setDuration(remainingMilliseconds - animationInDuration);
                progressBar.startAnimation(animOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // TODO update ongoing notification
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (streamServiceIsBound) {
            unbindService(streamServiceConnection);
        }
        super.onDestroy();
    }

    private ServiceConnection streamServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RadioStreamService.MyLocalBinder binder = (RadioStreamService.MyLocalBinder) service;
            streamService = binder.getService();
            streamServiceIsBound = true;
            setPlayButtonListeners();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            streamServiceIsBound = false;
        }
    };
}
