package com.example.controllapp;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MoreActionFragment extends Fragment {


    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_more_action, container, false);


        FloatingActionButton screenshotButton = view.findViewById(R.id.Screenshot);
        screenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                playClickSound();
            }
        });
        return view;
    }
    private void takeScreenshot() {
        // Get the root view
        View rootView = getActivity().getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Save the screenshot to the gallery
        saveScreenshotToGallery(screenshotBitmap);
    }
    private void saveScreenshotToGallery(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Screenshots");
        if (!imagePath.exists()) {
            if (!imagePath.mkdirs()) {
                Toast.makeText(getActivity(), "Failed to create directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileName = "Screenshot_" + timeStamp + ".jpg";
        File file = new File(imagePath, fileName);

        try {
            OutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Refresh gallery to show new screenshot
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{file.toString()},
                    new String[]{"image/jpeg"},
                    null);

            Toast.makeText(getActivity(), "Screenshot saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to save screenshot", Toast.LENGTH_SHORT).show();
        }
    }

    private void playClickSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.screenshot); // Make sure R.raw.screenshot exists
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
        }
        mediaPlayer.start();
    }
}