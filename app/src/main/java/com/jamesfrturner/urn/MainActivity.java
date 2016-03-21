package com.jamesfrturner.urn;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        final RadioStream stream = new RadioStream(context);
        final PlayButtonAnimator pba = new PlayButtonAnimator(MainActivity.this, this);
        final Button playButton = (Button) findViewById(R.id.play_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stream.isPlaying()) {
                    try {
                        pba.changeState(PlayButtonAnimator.STATE_STOPPED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stream.stop();
                }
                else {
                    try {
                        pba.changeState(PlayButtonAnimator.STATE_LOADING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    playButton.setEnabled(false);

                    stream.play(new Handler.Callback() {
                        public boolean handleMessage(Message msg) {
                            try {
                                pba.changeState(PlayButtonAnimator.STATE_PLAYING);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            playButton.setEnabled(true);
                            return false;
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
