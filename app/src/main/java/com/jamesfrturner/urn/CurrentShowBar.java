package com.jamesfrturner.urn;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CurrentShowBar {
    private LinearLayout bar;
    private TextView titleTextView;
    private Activity activity;

    public CurrentShowBar(Activity activity) {
        this.activity = activity;
        bar = (LinearLayout) activity.findViewById(R.id.current_show_bar);
        titleTextView = (TextView) activity.findViewById(R.id.on_air_show_name);
    }

    public void hide() {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_top);
        bar.startAnimation(animation);
        bar.setVisibility(View.INVISIBLE);
    }

    public void show(String title) {
        titleTextView.setText(title);

        if (bar.getVisibility() == View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_top);
            bar.startAnimation(animation);
            bar.setVisibility(View.VISIBLE);
        }
    }
}
