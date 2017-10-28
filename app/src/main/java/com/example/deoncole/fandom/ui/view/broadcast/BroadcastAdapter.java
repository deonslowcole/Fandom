package com.example.deoncole.fandom.ui.view.broadcast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deoncole.fandom.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BroadcastAdapter extends RecyclerView.Adapter<BroadcastViewHolder> {

    private ArrayList<BroadcastViewItem> items = new ArrayList<>();

    @Override
    public BroadcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View messageView = inflater.inflate(R.layout.broadcast_layout, parent, false);
        return new BroadcastViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(BroadcastViewHolder holder, int position) {
        BroadcastViewItem broadcast = items.get(position);
        holder.bind(broadcast);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void addItems(List<BroadcastViewItem> l) {
        items.addAll(l);
        Collections.sort(items, new Comparator<BroadcastViewItem>() {
            @Override
            public int compare(BroadcastViewItem b1, BroadcastViewItem b2) {
                long timestamp1 = b1.getBroadcast().getTimestamp();
                long timestamp2 = b2.getBroadcast().getTimestamp();
                if(timestamp1 < timestamp2) {
                    return 1;
                }
                else if (timestamp1 > timestamp2) {
                    return -1;
                }
                return 0;
            }
        });
        notifyDataSetChanged();
    }

    void clearData() {
        items.clear();
        notifyDataSetChanged();
    }
}
