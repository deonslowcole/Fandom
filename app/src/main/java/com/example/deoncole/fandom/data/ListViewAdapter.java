package com.example.deoncole.fandom.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.model.Artist;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private List<Artist> artistsObj = new ArrayList<>();;
    private List<Artist> displayedArtists = new ArrayList<>();

    public ListViewAdapter(Context context, List<Artist> artists){
        this.artistsObj.addAll(artists);
        this.displayedArtists.addAll(artists);
        inflater = LayoutInflater.from(context);
    }

    public void clear() {
        this.artistsObj.clear();
        this.displayedArtists.clear();
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextView artistNameTv;
    }

    public void addItem(Artist artist) {
        this.artistsObj.add(artist);
        this.displayedArtists.add(artist);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return displayedArtists.size();
    }

    @Override
    public Artist getItem(int position) {
        return displayedArtists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 010101 + position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_text, null);
            holder.artistNameTv = (TextView) view.findViewById(R.id.artistNameTv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.artistNameTv.setText(displayedArtists.get(position).getArtistName());
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        displayedArtists.clear();
        if(charText.length() == 0){
            displayedArtists.addAll(artistsObj);
        } else {
            for (Artist artist : artistsObj){
                if(artist.getArtistName().toLowerCase(Locale.getDefault()).contains(charText)){
                    displayedArtists.add(artist);
                }
            }
        }
        notifyDataSetChanged();
    }
}
