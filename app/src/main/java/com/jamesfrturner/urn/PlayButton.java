package com.jamesfrturner.urn;

public class PlayButton {
    private static String STATE_STOPPED = "stopped";
    private static String STATE_PLAYING = "playing";
    private static String STATE_LOADING = "loading";

    private static String currentState = STATE_STOPPED;

//    public void beginLoad() {
//        // change to 'loading' state
//    }

//    public void cancelLoad() {
//        // change to 'stopped' state
//    }

    public static void play() {
        // change to 'playing' state
        currentState = STATE_LOADING;
        // start loading animaton
    }

//    public void stop() {
//        // change to 'stopped' state
//    }
}
