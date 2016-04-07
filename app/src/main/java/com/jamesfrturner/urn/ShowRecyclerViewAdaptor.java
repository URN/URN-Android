package com.jamesfrturner.urn;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.List;

public class ShowRecyclerViewAdaptor extends RecyclerView.Adapter<ShowRecyclerViewAdaptor.ShowViewHolder>{

    private List<Show> shows;
    private Context context;


    ShowRecyclerViewAdaptor(List<Show> shows, Context context){
        this.shows = shows;
        this.context = context;
    }

    public static class ShowViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView showTitle;
        TextView showDescription;
        TextView showTimes;

        ShowViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.show_card_view);
            showTitle = (TextView) itemView.findViewById(R.id.show_title);
            showTimes = (TextView) itemView.findViewById(R.id.show_times);
        }
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_card_view, viewGroup, false);
        ShowViewHolder pvh = new ShowViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ShowViewHolder showViewHolder, int i) {
        Show show = shows.get(i);

        showViewHolder.showTitle.setText(show.getTitle());

        DateTimeFormatter outputFormat = new DateTimeFormatterBuilder().appendPattern("H:mm").toFormatter();
        String startTime = show.getStartTime().toString(outputFormat);
        String endTime = show.getEndTime().toString(outputFormat);
        String times = startTime + " - " + endTime;
        showViewHolder.showTimes.setText(times);

        showViewHolder.showTitle.setBackgroundColor(Color.parseColor(show.getCategoryColor()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}