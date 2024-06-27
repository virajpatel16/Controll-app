package com.example.controllapp;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicFragment extends Fragment {
    private TextView songTitle;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private TextView tvElapsedTime, tvRemainingTime;

    private List<Integer> trackList; // List to hold track resource IDs
    private int currentTrackIndex = 0; // Index of the currently playing track

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_AUDIO_REQUEST = 101;
    private CircleImageView imageView;
    private List<Integer> imageList;
    ImageView back_music;
    private FloatingActionButton rotateButton,nextmusic,previousMusicButton;
    private boolean isRotating = false;
    private ObjectAnimator rotationAnimator;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_music, container, false);
        trackList = new ArrayList<>();
        trackList.add(R.raw.track1); // Replace with your actual raw resource IDs
        trackList.add(R.raw.track2);
        trackList.add(R.raw.track3);
        trackList.add(R.raw.track4);
        trackList.add(R.raw.track5);
        trackList.add(R.raw.track6);
        trackList.add(R.raw.track7);
        trackList.add(R.raw.track8);
        trackList.add(R.raw.track9);
        trackList.add(R.raw.track10);
        imageList = new ArrayList<>();
        imageList.add(R.drawable.image); // Replace with your actual drawable resource IDs
        imageList.add(R.drawable.image1);
        imageList.add(R.drawable.image3);
        imageList.add(R.drawable.image4);
        imageList.add(R.drawable.image5);
        imageList.add(R.drawable.image6);
        imageList.add(R.drawable.image7);
        imageList.add(R.drawable.image8);
        imageList.add(R.drawable.image9);
        imageList.add(R.drawable.image2);
        imageView = view.findViewById(R.id.imageview);
        rotateButton = view.findViewById(R.id.music);
        songTitle = view.findViewById(R.id.songtitle);
        nextmusic=view.findViewById(R.id.skip_next);
        previousMusicButton=view.findViewById(R.id.skip_previous);
        seekBar = view.findViewById(R.id.seekBar);
        tvElapsedTime = view.findViewById(R.id.tvElapsedTime);
        tvRemainingTime = view.findViewById(R.id.tvRemainingTime);
        back_music=view.findViewById(R.id.back_music);

        back_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        List<String> songTitles = new ArrayList<>();
        songTitles.add("Track 1");
        songTitles.add("Track 2");
        songTitles.add("Track 3");
        songTitles.add("Track 4");
        songTitles.add("Track 5");
        songTitles.add("Track 6");
        songTitles.add("Track 7");
        songTitles.add("Track 8");
        songTitles.add("Track 9");
        songTitles.add("Track 10");

        List<Integer> songResourceIds = new ArrayList<>();
        songResourceIds.add(R.raw.track1);
        songResourceIds.add(R.raw.track2);
        songResourceIds.add(R.raw.track3);
        songResourceIds.add(R.raw.track4);
        songResourceIds.add(R.raw.track5);
        songResourceIds.add(R.raw.track6);
        songResourceIds.add(R.raw.track7);
        songResourceIds.add(R.raw.track8);
        songResourceIds.add(R.raw.track9);
        songResourceIds.add(R.raw.track10);
        List<Integer> songImage = new ArrayList<>();
        songImage.add(R.drawable.image);
        songImage.add(R.drawable.image1);
        songImage.add(R.drawable.image2);
        songImage.add(R.drawable.image3);
        songImage.add(R.drawable.image4);
        songImage.add(R.drawable.image5);
        songImage.add(R.drawable.image6);
        songImage.add(R.drawable.image7);
        songImage.add(R.drawable.image8);
        songImage.add(R.drawable.image9);

        SongAdapter songAdapter = new SongAdapter(requireActivity(), songTitles, songResourceIds, songImage);
        recyclerView.setAdapter(songAdapter);

        nextmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToNextTrack(mediaPlayer);
            }
        });
        previousMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToPreviousTrack();
            }
        });
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRotating) {
                    if (isPlaying) {
                        // Stop music and change button image to play
                        mediaPlayer.pause();
                        isPlaying = false;
                        stopRotation();
                    }

                } else {
                    playCurrentTrack();

                }

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
  return view;
    }
    private void startRotation() {
        // Cancel any existing animation to start fresh
        if (rotationAnimator != null && rotationAnimator.isRunning()) {
            rotationAnimator.cancel();
        }

        // Create a new ObjectAnimator to rotate the imageView
        rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0.9f, 360f);
        rotationAnimator.setDuration(1000); // 1 second for one rotation
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Infinite rotation
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART); // Restart from the beginning when it ends
        rotationAnimator.start();
        isRotating = true;


    }
    private void stopRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
        isRotating = false;

        rotateButton.setImageResource(R.drawable.play_arrow_24dp_fill0_wght400_grad0_opsz24);
    }
    public void onDestroy() {
        super.onDestroy();
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    private void skipToNextTrack(MediaPlayer mediaPlayer) {
        if (trackList == null || trackList.isEmpty() || imageList == null || imageList.isEmpty()) {
            Toast.makeText(requireActivity(), "No tracks or images available", Toast.LENGTH_SHORT).show();
            return;
        }

        releaseMediaPlayer();

        currentTrackIndex++;
        if (currentTrackIndex >= trackList.size()) {
            currentTrackIndex = 0; // Loop back to the first track if end of list is reached
        }

        playCurrentTrack();
    }

    private void playCurrentTrack() {
        if (trackList == null || trackList.isEmpty() || imageList == null || imageList.isEmpty()) {
            Toast.makeText(requireActivity(), "No tracks or images available", Toast.LENGTH_SHORT).show();
            return;
        }

        releaseMediaPlayer();

        mediaPlayer = MediaPlayer.create(requireContext(), trackList.get(currentTrackIndex));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                skipToNextTrack(mediaPlayer);
            }
        });

        mediaPlayer.start();
        isPlaying = true;
        startRotation();
        rotateButton.setImageResource(R.drawable.pause_24dp_fill0_wght400_grad0_opsz24);

        songTitle.setText("Track " + (currentTrackIndex + 1));
        imageView.setImageResource(imageList.get(currentTrackIndex));
        Toast.makeText(requireActivity(), "" + (currentTrackIndex + 1), Toast.LENGTH_SHORT).show();

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar();
    }   private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void skipToPreviousTrack() {
        if (trackList == null || trackList.isEmpty() || imageList == null || imageList.isEmpty()) {
            Toast.makeText(requireActivity(), "No tracks or images available", Toast.LENGTH_SHORT).show();
            return;
        }

        releaseMediaPlayer();

        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = trackList.size() - 1; // Loop back to the last track if beginning of list is reached
        }

        playCurrentTrack();
    }
    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());

            int elapsedTime = mediaPlayer.getCurrentPosition() / 1000;
            int remainingTime = (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()) / 1000;

            tvElapsedTime.setText(String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60));
            tvRemainingTime.setText(String.format("%02d:%02d", remainingTime / 60, remainingTime % 60));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            }, 1000);
        }
    }
    }