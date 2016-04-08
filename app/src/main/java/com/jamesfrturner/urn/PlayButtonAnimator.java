package com.jamesfrturner.urn;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public class PlayButtonAnimator {
    public final static int STATE_STOPPED = 0;
    public final static int STATE_PLAYING = 1;
    public final static int STATE_LOADING = 2;

    private int oldState;

    private static int currentState = STATE_STOPPED;
    private Context context;
    private Activity activity;

    private ImageButton playButton;
    private ImageView topWings;
    private ImageView bottomWings;

    private Animation spinAnimation;
    private AnimatedVectorDrawable avdPlayToStop;
    private AnimatedVectorDrawable avdStopToPlay;

    public PlayButtonAnimator(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;

        playButton = (ImageButton) activity.findViewById(R.id.play_button);
        topWings = (ImageView) activity.findViewById(R.id.play_wings_top);
        bottomWings = (ImageView) activity.findViewById(R.id.play_wings_bottom);
        spinAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_play_button_wings);

        if (Build.VERSION.SDK_INT >= 21) {
            avdPlayToStop = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.avd_play_stop);
            avdStopToPlay = (AnimatedVectorDrawable) activity.getDrawable(R.drawable.avd_stop_play);
            Drawable circleBackground = activity.getDrawable(R.drawable.circle_button);

            if (playButton == null || avdPlayToStop == null) {
                throw new IllegalStateException();
            }

            playButton.setBackground(circleBackground);
            playButton.setImageDrawable(avdPlayToStop);
        }
    }

    public void changeState(int newState) throws Exception {
        oldState = currentState;

        switch (newState) {
            case STATE_STOPPED:
                currentState = STATE_STOPPED;

                if (oldState == STATE_LOADING) {
                    stopLoadAnimation();
                }

                if (oldState == STATE_PLAYING) {
                    stopPlayAnimation();
                }

                startStopAnimation();
                break;

            case STATE_LOADING:
                currentState = STATE_LOADING;

                if (oldState == STATE_PLAYING) {
                    stopPlayAnimation();
                }

                if (oldState == STATE_STOPPED) {
                    stopStopAnimation();
                }

                startLoadAnimation();
                break;

            case STATE_PLAYING:
                currentState = STATE_PLAYING;

                if (oldState == STATE_LOADING) {
                    stopLoadAnimation();
                }

                if (oldState == STATE_STOPPED) {
                    stopStopAnimation();
                }

                startPlayAnimation();
                break;

            default:
                throw new Exception("Invalid state");
        }
    }


    private void startLoadAnimation() {
        activity.findViewById(R.id.play_button_load_spinner).setVisibility(View.VISIBLE);
    }

    private void stopLoadAnimation() {
        activity.findViewById(R.id.play_button_load_spinner).setVisibility(View.GONE);
    }

    private void startPlayAnimation() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (oldState == STATE_PLAYING) {
                playButton.setImageDrawable(avdStopToPlay);
            }
            else {
                playButton.setImageDrawable(avdPlayToStop);
                avdPlayToStop.start();
            }
        }
        else {
            playButton.setBackgroundResource(R.drawable.circle_stop_button);
        }

        topWings.startAnimation(spinAnimation);
        bottomWings.startAnimation(spinAnimation);
        topWings.setVisibility(View.VISIBLE);
        bottomWings.setVisibility(View.VISIBLE);
    }

    private void stopPlayAnimation() {
        topWings.clearAnimation();
        bottomWings.clearAnimation();
        topWings.setVisibility(View.INVISIBLE);
        bottomWings.setVisibility(View.INVISIBLE);
    }

    private void startStopAnimation() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (oldState == STATE_STOPPED) {
                playButton.setImageDrawable(avdPlayToStop);
            }
            else {
                playButton.setImageDrawable(avdStopToPlay);
                avdStopToPlay.start();
            }
        }
        else {
            playButton.setBackgroundResource(R.drawable.circle_play_button);
        }
    }

    private void stopStopAnimation() {

    }
}
