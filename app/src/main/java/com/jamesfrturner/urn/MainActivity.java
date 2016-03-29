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

        RestClient restClient = new RestClient(this);
        restClient.getCurrentSong(
                new Response.Listener<Song>() {
                    @Override
                    public void onResponse(Song currentSong) {
                        TextView artist = (TextView) findViewById(R.id.current_song_artist);
                        artist.setText(currentSong.getArtist());

                        TextView title = (TextView) findViewById(R.id.current_song_title);
                        title.setText(currentSong.getTitle());

                        ProgressBar progress = (ProgressBar) findViewById(R.id.current_song_progress);
                        progress.setProgress(currentSong.getProgress());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Hide current song view / show error
                    }
                }
        );
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
