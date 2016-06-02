package com.jamesfrturner.urn;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PlayButtonAnimator {
    public final static int STATE_STOPPED = 0;
    public final static int STATE_PLAYING = 1;
    public final static int STATE_LOADING = 2;

    private static int currentState = STATE_STOPPED;
    private Context context;
    private Activity activity;

    private ImageButton playButton;
    private ImageView topWings;
    private ImageView bottomWings;
    private ProgressBar spinner;

    private Animation spinAnimation;
    private AnimatedVectorDrawableCompat avdPlayToStop;
    private AnimatedVectorDrawableCompat avdStopToPlay;

    public PlayButtonAnimator(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;

        playButton = (ImageButton) activity.findViewById(R.id.play_button);
        topWings = (ImageView) activity.findViewById(R.id.play_wings_top);
        bottomWings = (ImageView) activity.findViewById(R.id.play_wings_bottom);
        spinner = (ProgressBar) activity.findViewById(R.id.play_button_load_spinner);
        spinAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_play_button_wings);

        if (Build.VERSION.SDK_INT >= 21) {
            avdPlayToStop = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_play_stop);
            avdStopToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_stop_play);

            if (playButton == null || avdPlayToStop == null) {
                throw new IllegalStateException();
            }

            playButton.setImageDrawable(avdPlayToStop);
        }
    }

    public void changeState(int newState, boolean animate) throws Exception {
        int oldState = currentState;

        switch (newState) {
            case STATE_STOPPED:
                spinner.setVisibility(View.GONE);

                topWings.clearAnimation();
                bottomWings.clearAnimation();
                topWings.setVisibility(View.INVISIBLE);
                bottomWings.setVisibility(View.INVISIBLE);

                if (oldState == STATE_PLAYING && animate && Build.VERSION.SDK_INT >= 21) {
                    playButton.setImageDrawable(avdStopToPlay);
                    avdStopToPlay.start();
                }
                else {
                    playButton.setImageResource(R.drawable.play);
                }

                currentState = STATE_STOPPED;
                break;

            case STATE_LOADING:
                spinner.setVisibility(View.VISIBLE);

                topWings.clearAnimation();
                bottomWings.clearAnimation();
                topWings.setVisibility(View.INVISIBLE);
                bottomWings.setVisibility(View.INVISIBLE);

                currentState = STATE_LOADING;
                break;

            case STATE_PLAYING:
                spinner.setVisibility(View.GONE);

                if (oldState == STATE_STOPPED && animate && Build.VERSION.SDK_INT >= 21) {
                    playButton.setImageDrawable(avdPlayToStop);
                    avdPlayToStop.start();
                }
                else {
                    playButton.setImageResource(R.drawable.stop);
                }

                topWings.startAnimation(spinAnimation);
                bottomWings.startAnimation(spinAnimation);
                topWings.setVisibility(View.VISIBLE);
                bottomWings.setVisibility(View.VISIBLE);

                currentState = STATE_PLAYING;
                break;

            default:
                throw new Exception("Invalid state");
        }
    }
}
