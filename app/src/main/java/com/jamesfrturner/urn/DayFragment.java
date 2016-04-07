package com.jamesfrturner.urn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Calendar;

public class DayFragment extends Fragment {
    public static final String ARG_DAY_NUMBER = "ARG_DAY_NUMBER";
    private int day;

    public static Fragment newInstance(int dayNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_DAY_NUMBER, dayNumber);
        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = getArguments().getInt(ARG_DAY_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_day, container, false);

        AppController controller = ((AppController) getActivity().getApplicationContext());
        controller.getSchedule(
                new Response.Listener<Schedule>() {
                    @Override
                    public void onResponse(Schedule schedule) {
                        createScheduleView(schedule.getDay(day), view);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO schedule not loaded error
                    }
                }
        );

        return view;
    }

    private void createScheduleView(Day day, View fragmentView) {
        TextView textView = (TextView) fragmentView.findViewById(R.id.test_id);
        textView.setText(day.getShows().toString());
    }
}
