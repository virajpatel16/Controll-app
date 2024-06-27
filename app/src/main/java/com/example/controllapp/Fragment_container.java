package com.example.controllapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class Fragment_container extends AppCompatActivity {
    int checkin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MusicFragment musicFragment = new MusicFragment();
        fragmentTransaction.add(R.id.frame, musicFragment);
        fragmentTransaction.commit();

        if (getIntent().getExtras() != null) {
            checkin = getIntent().getIntExtra("dailycheck", 1);
            switch (checkin) {
                case 101:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MusicFragment()).commit();
                    break;
                case 102:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MoreActionFragment()).commit();
                    break;

            }
        }
    }


}