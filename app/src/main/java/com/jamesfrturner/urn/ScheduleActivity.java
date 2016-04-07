package com.jamesfrturner.urn;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.schedule_view_pager);
        viewPager.setAdapter(new DayFragmentPagerAdapter(getSupportFragmentManager(),
                ScheduleActivity.this));
        viewPager.setCurrentItem(DayFragmentPagerAdapter.dayNumberToPosition(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.schedule_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
