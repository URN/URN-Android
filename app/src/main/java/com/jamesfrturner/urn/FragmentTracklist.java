package com.jamesfrturner.urn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentTracklist extends Fragment {
    private static ArrayList<Song> songHistory;
    private static SongAdaptor adaptor;
    private static ListView tracklistView;
    private static TextView tracklistEmptyMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songHistory = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);

        adaptor = new SongAdaptor(getActivity(), songHistory);
        tracklistView = (ListView) view.findViewById(R.id.tracklistView);
        tracklistEmptyMessage = (TextView) view.findViewById(R.id.tracklistEmptyMessage);
        tracklistView.setAdapter(adaptor);

        return view;
    }

    public static void addSong(Song song) {
        if (songHistory.size() == 0 || !songHistory.get(songHistory.size() - 1).toString().equals(song.toString())) {
            songHistory.add(song);
            adaptor.notifyDataSetChanged();
            if (tracklistView.getVisibility() != View.VISIBLE) {
                tracklistView.setVisibility(View.VISIBLE);
                tracklistEmptyMessage.setVisibility(View.GONE);
            }
        }
    }
}