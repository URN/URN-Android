package com.jamesfrturner.urn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SongAdaptor extends ArrayAdapter<Song> {
    private static class ViewHolder {
        TextView title;
        TextView artist;
        TextView played;
    }

    public SongAdaptor(Context context, List<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tracklist_item, parent, false);

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tracklist_item, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.songTitle);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.songArtist);
            viewHolder.played = (TextView) convertView.findViewById(R.id.timePlayed);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.title.setText(song.getTitle());
        viewHolder.artist.setText(song.getArtist());
        viewHolder.played.setText(Integer.toString(song.getPercentagePlayed()));

        return convertView;
    }

}
