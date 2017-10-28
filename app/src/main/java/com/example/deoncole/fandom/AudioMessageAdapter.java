package com.example.deoncole.fandom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jean.jcplayer.JcAudio;

import java.util.ArrayList;
import java.util.List;


class AudioMessageAdapter extends RecyclerView.Adapter<AudioMessageAdapter.AudioMessageViewHolder> {

    private ArrayList<JcAudio> items = new ArrayList<>();
    private AudioMessageSelectionListener mAudioMessageSelectionListener;
    private int selectedPosition;

    @Override
    public AudioMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_message_item,
                parent, false);
        return new AudioMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AudioMessageViewHolder holder, final int position) {
        final JcAudio audioData = items.get(position);
        holder.bind(audioData);
        holder.itemView.setSelected(selectedPosition == position);
    }

    void setAudioMessageSelectionListener(final AudioMessageSelectionListener audioMessageSelectionListener) {
        mAudioMessageSelectionListener = audioMessageSelectionListener;
    }

    void setItems(ArrayList<JcAudio> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void addItems(final List<JcAudio> jcAudios) {
        this.items.addAll(jcAudios);
        notifyDataSetChanged();
    }

    List<JcAudio> getItems() {
        return items;
    }

    class AudioMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AudioMessageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

        }

        void bind(JcAudio audioData) {
            TextView amArtistTv = (TextView) itemView.findViewById(R.id.audio_message_artist);
            amArtistTv.setText(audioData.getTitle());
        }

        @Override
        public void onClick(final View v) {
            selectedPosition = getAdapterPosition();
            if (mAudioMessageSelectionListener != null) {
                mAudioMessageSelectionListener.onAudioMessageSelected(items.get(getAdapterPosition()));
            }
            notifyDataSetChanged();
        }
    }

    public interface AudioMessageSelectionListener {
        void onAudioMessageSelected(final JcAudio audioData);
    }

}
