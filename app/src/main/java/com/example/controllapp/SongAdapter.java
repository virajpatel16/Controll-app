package com.example.controllapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<String> songList;
    private final List<Integer> songResourceIds;
    private final List<Integer> songimagelist;


    private final Context context;
    private MediaPlayer mediaPlayer;

    public SongAdapter(Context context, List<String> songList, List<Integer> songResourceIds, List<Integer> songImage) {
        this.context = context;
        this.songList = songList;
        this.songResourceIds = songResourceIds;
        songimagelist = songImage;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        String songTitle = songList.get(position);
        int song_image=songimagelist.get(position);
        holder.songTitle.setText(songTitle);

        int songImageId = songimagelist.get(position);

        holder.songTitle.setText(songTitle);
        holder.image_song.setImageResource(songImageId);


        holder.itemView.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, songResourceIds.get(position));
            mediaPlayer.start();

        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        CircleImageView image_song;



        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            image_song = itemView.findViewById(R.id.image_song);
        }
    }
}
