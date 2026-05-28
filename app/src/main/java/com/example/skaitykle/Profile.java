package com.example.skaitykle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.skaitykle.DataBase.User;
import com.example.skaitykle.DataBase.UserRep;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {

    // ── Navigation ────────────────────────────────────────────────────────────
    BottomNavigationView bottomNavigationView;

    // ── Camera ────────────────────────────────────────────────────────────────
    private static final String AUTHORITY = "com.example.skaitykle.fileprovider";
    private Uri cameraImageUri;

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {
                        if (success && cameraImageUri != null) {
                            ImageView profilePhoto = findViewById(R.id.profilePhoto);
                            Glide.with(this)
                                    .load(cameraImageUri)
                                    .circleCrop()
                                    .into(profilePhoto);

                            int userId = getIntent().getIntExtra("userId", -1);
                            UserRep userRep = new UserRep(getApplication());
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.execute(() -> {
                                User user = userRep.getUserByIdDirect(userId);
                                if (user != null) {
                                    user.profilePhotoUri = cameraImageUri.toString();
                                    userRep.update(user);
                                }
                            });
                        }
                    });

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            openProfileCamera();
                        } else {
                            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    // ── Shake detection ───────────────────────────────────────────────────────
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int shakeCount = 0;
    private float lastX, lastY, lastZ;
    private boolean firstReading = true;
    private long lastShakeTime = 0;
    private static final float SHAKE_THRESHOLD = 8.0f;
    private static final int SHAKE_COOLDOWN_MS = 500;

    private final SensorEventListener shakeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Skip first reading to avoid false spike from 0,0,0
            if (firstReading) {
                lastX = x;
                lastY = y;
                lastZ = z;
                firstReading = false;
                return;
            }

            float deltaX = Math.abs(x - lastX);
            float deltaY = Math.abs(y - lastY);
            float deltaZ = Math.abs(z - lastZ);

            lastX = x;
            lastY = y;
            lastZ = z;

            float movement = deltaX + deltaY + deltaZ;
            long now = System.currentTimeMillis();

            if (movement > SHAKE_THRESHOLD && (now - lastShakeTime) > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now;
                shakeCount++;

                TextView shakeStatusText = findViewById(R.id.shakeStatusText);
                TextView shakeCountText  = findViewById(R.id.shakeCountText);

                if (shakeStatusText != null) {
                    shakeStatusText.setVisibility(View.VISIBLE);
                    shakeStatusText.setText("Shake detected! 📳");
                }
                if (shakeCountText != null) {
                    //shakeCountText.setVisibility(View.VISIBLE);
                    //shakeCountText.setText("Shake count: " + shakeCount);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(
                    shakeListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        TextView shakeStatusText = findViewById(R.id.shakeStatusText);
        if (accelerometer == null && shakeStatusText != null) {
            shakeStatusText.setText("Accelerometer not available");
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_profile);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        TextView nameText    = findViewById(R.id.nameText);
        TextView surnameText = findViewById(R.id.surnameText);
        TextView emailText   = findViewById(R.id.emailText);

        // ── Device info ───────────────────────────────────────────────────────
        TextView deviceModelText        = findViewById(R.id.deviceModelText);
        TextView deviceIdText           = findViewById(R.id.deviceIdText);
        TextView deviceManufacturerText = findViewById(R.id.deviceManufacturerText);

        deviceModelText.setText(android.os.Build.MODEL);
        deviceIdText.setText(android.os.Build.ID);
        deviceManufacturerText.setText(android.os.Build.MANUFACTURER);

        // ── Profile photo ─────────────────────────────────────────────────────
        ImageView profilePhoto = findViewById(R.id.profilePhoto);
        profilePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openProfileCamera();
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA);
            }
        });

        // ── Show sensors button ───────────────────────────────────────────────
        Button btnShowSensors = findViewById(R.id.btnShowSensors);
        btnShowSensors.setOnClickListener(v -> {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

            StringBuilder sb = new StringBuilder();
            for (Sensor sensor : sensors) {
                sb.append("• ").append(sensor.getName())
                        .append(" (Type: ").append(sensor.getType()).append(")\n\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("Available Sensors")
                    .setMessage(sb.toString())
                    .setPositiveButton("Close", null)
                    .show();
        });

        // ── Load user from database ───────────────────────────────────────────
        int userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        UserRep userRep = new UserRep(getApplication());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User user = userRep.getUserByIdDirect(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    nameText.setText(user.firstName);
                    surnameText.setText(user.lastName);
                    emailText.setText(user.email);

                    if (user.profilePhotoUri != null && !user.profilePhotoUri.isEmpty()) {
                        Glide.with(this)
                                .load(Uri.parse(user.profilePhotoUri))
                                .circleCrop()
                                .into(profilePhoto);
                    }
                }
            });
        });

        // ── Navigation listener ───────────────────────────────────────────────
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(getBaseContext(), Title.class));
                    return true;
                } else if (id == R.id.menu_add_book) {
                    startActivity(new Intent(getBaseContext(), AddBook.class));
                    return true;
                } else if (id == R.id.menu_library) {
                    startActivity(new Intent(getBaseContext(), Library.class));
                    return true;
                } else if (id == R.id.menu_map) {
                    startActivity(new Intent(getBaseContext(), Map.class));
                    return true;
                } else if (id == R.id.menu_profile) {
                    Intent profileIntent = new Intent(getBaseContext(), Profile.class);
                    profileIntent.putExtra("userId", userId);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            }
        });
    }

    // ── Camera helper ─────────────────────────────────────────────────────────
    private void openProfileCamera() {
        File photoDir = new File(getFilesDir(), "profiles");
        if (!photoDir.exists()) photoDir.mkdirs();
        File photoFile = new File(photoDir, "profile_" + System.currentTimeMillis() + ".jpg");
        cameraImageUri = FileProvider.getUriForFile(this, AUTHORITY, photoFile);
        cameraLauncher.launch(cameraImageUri);
    }
}