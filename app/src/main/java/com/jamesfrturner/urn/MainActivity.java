package com.jamesfrturner.urn;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

        AppController controller = ((AppController) getApplicationContext());

        startCurrentSongPolling(controller.getRestClient());
        setSendMessageListeners(controller.getRestClient());
        loadCurrentShow(controller);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                            }
                        }
                );
            }
        };

        handler.postDelayed(runnable, 100);
    }

    private void loadCurrentShow(AppController controller) {
        final CurrentShowBar bar = new CurrentShowBar(this);

        controller.getSchedule(
                new Response.Listener<Schedule>() {
                    @Override
                    public void onResponse(Schedule schedule) {
                        Show currentShow = schedule.getCurrentShow();

                        if (currentShow == null) {
                            bar.hide();
                            return;
                        }

                        bar.show(currentShow.getTitle());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bar.hide();
                    }
                }
        );
    }

    private void setSendMessageListeners(final RestClient restClient) {
        final ImageButton sendButton = (ImageButton) findViewById(R.id.message_studio_send);
        final EditText messageEditText = (EditText) findViewById(R.id.message_studio_text);
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        if (sendButton == null || messageEditText == null || mainLayout == null) {
            throw new IllegalStateException();
        }

        final Activity activity = this;

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();

                if (message.equals("")) {
                    return;
                }

                messageEditText.setText("");

                restClient.sendMessage(
                        message,
                        new Response.Listener<Boolean>() {
                            @Override
                            public void onResponse(Boolean success) {
                                String responseMessage = "";
                                if (success) {
                                    responseMessage = getResources().getString(R.string.message_sent);
                                }
                                else {
                                    getResources().getString(R.string.message_not_sent);
                                }

                                Toast.makeText(activity, responseMessage, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(activity, getResources().getString(R.string.message_not_sent), Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        });

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        messageEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void setPlayButtonListeners() {
        final Context context = getApplicationContext();
        final PlayButtonAnimator pba = new PlayButtonAnimator(MainActivity.this, this);
        final ImageButton playButton = (ImageButton) findViewById(R.id.play_button);

        if (playButton == null) {
            throw new IllegalStateException();
        }

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

        if (artistTextView == null || titleTextView == null || progressBar == null) {
            throw new IllegalStateException();
        }

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

                int duration = remainingMilliseconds - animationInDuration;
                duration = duration < 0 ? 0 : duration;
                animOut.setDuration(duration);
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

        if (id == R.id.action_schedule) {
            startActivity(new Intent(this, ScheduleActivity.class));
            return true;
        }

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
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
