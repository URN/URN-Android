package com.jamesfrturner.urn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private RadioStream radio;
    private RestClient restClient;
    private FloatingActionButton playPauseBtn;
    private ProgressBar fabProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0);

        radio = new RadioStream(getApplicationContext());

        fabProgress = (ProgressBar) findViewById(R.id.fabProgress);
        playPauseBtn = (FloatingActionButton) findViewById(R.id.fab);

        if (radio.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.stop);
        }

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                class PlayCallback implements CallbackInterface {
                    @Override
                    public void execute() {
                        playPauseBtn.setImageResource(R.drawable.stop);
                        fabProgress.setVisibility(View.GONE);
                        playPauseBtn.setEnabled(true);
                    }
                }

                if (!radio.isPlaying()) {
                    fabProgress.setVisibility(View.VISIBLE);
                    playPauseBtn.setImageResource(0);
                    playPauseBtn.setEnabled(false);
                    radio.play(new PlayCallback());
                }
                else {
                    radio.stop();
                    playPauseBtn.setImageResource(R.drawable.play);
                }
            }
        });

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentAdaptor(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        restClient = RestClient.getInstance();
        restClient.setCurrentSongCallback(new RestClient.CurrentSongReadyCallback() {
            @Override
            public void resultReady(Song currentSong) {
                refreshCurrentSong(currentSong);
            }
        });

        restClient.setScheduleCallback(new RestClient.ScheduleReadyCallback() {
            @Override
            public void resultReady(Schedule schedule) {
                refreshCurrentShow(schedule.getCurrentShow());
            }
        });

        restClient.requestCurrentSong();
        restClient.requestSchedule();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshCurrentSong(final Song currentSong) {
        final int progress = currentSong.getPercentagePlayed();
        int timeout = currentSong == null ? 500 : 5000;

        TextView textView = (TextView) findViewById(R.id.currentSong);
        LinearLayout container = (LinearLayout) findViewById(R.id.nowPlayingContainer);
        if (currentSong != null && progress < 100) {
            textView.setText(currentSong.toString());
            container.setVisibility(View.VISIBLE);

            FragmentTracklist.addSong(currentSong);
        }
        else {
            textView.setText("URN Live");
            container.setVisibility(View.GONE);
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (progress >= 100) {
                            restClient.requestCurrentSong();
                        }
                        else {
                            refreshCurrentSong(currentSong);
                        }
                    }
                },
                timeout);
    }

    public void refreshCurrentShow(final Show currentShow) {
        TextView textView = (TextView) findViewById(R.id.currentShowName);
        LinearLayout container = (LinearLayout) findViewById(R.id.onAirContainer);

        if (currentShow != null) {
            textView.setText(currentShow.getName());
            container.setVisibility(View.VISIBLE);
        }
        else {
            container.setVisibility(View.GONE);
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        restClient.requestSchedule();
                    }
                },
                180000);
    }
}
