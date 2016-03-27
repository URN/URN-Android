package com.jamesfrturner.urn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RadioStreamService streamService;
    private boolean streamServiceIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, RadioStreamService.class);
        startService(intent);
        bindService(intent, streamServiceConnection, BIND_AUTO_CREATE);
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
