package com.jamesfrturner.urn;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class PlayButtonAnimator {
    public final static int STATE_STOPPED = 0;
    public final static int STATE_PLAYING = 1;
    public final static int STATE_LOADING = 2;

    private static int currentState = STATE_STOPPED;
    private Context context;
    private Activity activity;

    private Button playButton;

    public PlayButtonAnimator(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;

        playButton = (Button) activity.findViewById(R.id.play_button);
    }

    public void changeState(int newState) throws Exception {
        int oldState = currentState;

        if (oldState == newState) {
            return;
        }

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
        playButton.setBackgroundResource(R.drawable.circle_stop_button);
    }

    private void stopPlayAnimation() {

    }

    private void startStopAnimation() {
        playButton.setBackgroundResource(R.drawable.circle_play_button);
    }

    private void stopStopAnimation() {

    }
}
