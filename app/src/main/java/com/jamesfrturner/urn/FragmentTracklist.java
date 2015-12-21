package com.jamesfrturner.urn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentTracklist extends Fragment {
    private ArrayList<Song> songHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songHistory = new ArrayList<Song>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);

        SongAdaptor adaptor = new SongAdaptor(getActivity(), songHistory);
        ListView tracklistView = (ListView) view.findViewById(R.id.tracklistView);
        tracklistView.setAdapter(adaptor);

        return view;
    }
}