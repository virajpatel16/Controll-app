package com.example.controllapp;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton airoplanmodeButton;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_AUDIO_REQUEST = 2;


    private FloatingActionButton more;
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    FloatingActionButton music,flashlight;
    private CameraManager cameraManager;
    private String cameraId;

    private boolean isFlashlightOn = false;
    private ImageButton btnSkipPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnSkipNext;
    private static final int CAMERA_PERMISSION_REQUEST = 1001;



    private boolean isPlaying = false;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;
    private static final int REQUEST_WIFI_PERMISSIONS = 3;
    private static final int REQUEST_INTERNET_PERMISSIONS = 4;

    private BluetoothAdapter mBluetoothAdapter;
    private CircleImageView btnWiFi;
    private CircleImageView btnSend;
    private CircleImageView btnInternet;

    private ImageButton btnLow;
    private ImageButton btnMedium;
    private ImageButton btnHigh;
    private SeekBar seekBar;
    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnSend = findViewById(R.id.blutooth); // Ensure this ID is correct and points to a CircleImageView
        btnWiFi = findViewById(R.id.btnWiFi);  // Ensure this ID is correct and points to a CircleImageView
        btnInternet = findViewById(R.id.btnintenet);// Ensure this ID is correct and points to a CircleImageView
        btnLow = findViewById(R.id.btn_low);
        btnMedium = findViewById(R.id.btn_medium);
        flashlight=findViewById(R.id.flashlighton);
        airoplanmodeButton = findViewById(R.id.airoplanmode);
        btnHigh = findViewById(R.id.btn_high);
        more=findViewById(R.id.more);
        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        music = findViewById(R.id.music);

more.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, Fragment_container.class);
        intent.putExtra("dailycheck", 102);
        startActivity(intent);
    }
});







        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Fragment_container.class);
                intent.putExtra("dailycheck", 101);
                startActivity(intent);
            }
        });
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0]; // Use the first camera by default
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Check if device has flashlight feature
        if (!hasFlashlight()) {
            Toast.makeText(this, "No flashlight available on this device", Toast.LENGTH_SHORT).show();
            // Disable flashlight toggle button or handle accordingly
        }
flashlight.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (isFlashlightOn) {
            turnOffFlashlight();
            flashlight.setImageResource(R.drawable.flashlight_off_24dp_fill0_wght400_grad0_opsz24); // Set icon to off state
        } else {
            if (hasCameraPermission()) {
                turnOnFlashlight();
                flashlight.setImageResource(R.drawable.flashlight_on_24dp_fill0_wght400_grad0_opsz24); // Set icon to on state
            } else {
                requestCameraPermission();
            }
        }

    }
});
        btnLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustBrightness(50);
                updateTextView(50);
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustBrightness(128);
                updateTextView(128);
            }
        });
        airoplanmodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the app has permission to modify system settings
                if (hasWriteSettingsPermission()) {
                    // Open airplane mode settings screen
                    openAirplaneModeSettings();
                } else {
                    // Request permission to modify system settings
                    requestWriteSettingsPermission();
                }
            }
        });

        btnHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustBrightness(255);
                updateTextView(255);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBluetoothPermissions();
            }
        });

        btnWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWiFiPermissions();
            }
        });

        btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInternetPermissions();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjustBrightness(progress);
                updateTextView(progress);
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.screenBrightness = progress / 255.0f;
                getWindow().setAttributes(layoutParams);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void requestBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                showBluetoothPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
            }
        } else {
            toggleBluetooth();
        }
    }

    private void showBluetoothPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Bluetooth Permission Needed")
                .setMessage("This app needs Bluetooth permissions to function properly. Please grant the permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void toggleBluetooth() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth request denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestWiFiPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)) {
                showWiFiPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                        REQUEST_WIFI_PERMISSIONS);
            }
        } else {
            toggleWiFi();
        }
    }

    private void showWiFiPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("WiFi Permission Needed")
                .setMessage("This app needs WiFi permissions to function properly. Please grant the permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                                REQUEST_WIFI_PERMISSIONS);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void toggleWiFi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(this, "WiFi Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(this, "WiFi Disabled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "WiFi permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestInternetPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                showInternetPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_INTERNET_PERMISSIONS);
            }
        } else {
            toggleInternet();
        }
    }

    private void showInternetPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Internet Permission Needed")
                .setMessage("This app needs Internet permissions to function properly. Please grant the permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_INTERNET_PERMISSIONS);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void toggleInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(this, "Internet is already enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_WIFI_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleWiFi();
            } else {
                Toast.makeText(this, "WiFi permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_INTERNET_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleInternet();
            } else {
                Toast.makeText(this, "Internet permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open airplane mode settings screen
                openAirplaneModeSettings();
            } else {
                openAirplaneModeSettings();
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to modify system settings", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, turn on the flashlight
                turnOnFlashlight();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to access the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void adjustBrightness(int brightness) {
        if (Settings.System.canWrite(this)) {
            // Brightness value is in the range [0, 255]
            if (brightness < 0) {
                brightness = 0;
            } else if (brightness > 255) {
                brightness = 255;
            }
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        } else {
        }
    }

    private void updateTextView(int progress) {
        int brightnessPercentage = (int) ((progress / 255.0) * 100);
        textView.setText("Brightness: " + brightnessPercentage + "%");
    }

    private boolean hasWriteSettingsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }
    private boolean hasFlashlight() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
    private void requestWriteSettingsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_SETTINGS},
                PERMISSION_REQUEST_CODE);
    }

    private void openAirplaneModeSettings() {
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        startActivity(intent);
    }
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST);
    }
    private void turnOnFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void turnOffFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



}