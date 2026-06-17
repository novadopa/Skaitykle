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
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private static final String AUTHORITY = "com.example.skaitykle.fileprovider";

    BottomNavigationView bottomNavigationView;

    private Uri cameraImageUri;

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {
                        if (success && cameraImageUri != null) {
                            ImageView profilePhoto = findViewById(R.id.profilePhoto);
                            Glide.with(this).load(cameraImageUri).circleCrop().into(profilePhoto);

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
                        if (isGranted) openProfileCamera();
                        else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    });


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int shakeCount = 0;
    private float lastX, lastY, lastZ;
    private boolean firstReading = true;
    private long lastShakeTime = 0;
    private static final float SHAKE_THRESHOLD   = 8.0f;
    private static final int   SHAKE_COOLDOWN_MS = 500;

    private final SensorEventListener shakeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            if (firstReading) { lastX = x; lastY = y; lastZ = z; firstReading = false; return; }
            float delta = Math.abs(x - lastX) + Math.abs(y - lastY) + Math.abs(z - lastZ);
            lastX = x; lastY = y; lastZ = z;
            long now = System.currentTimeMillis();
            if (delta > SHAKE_THRESHOLD && (now - lastShakeTime) > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now;
                shakeCount++;
                TextView t = findViewById(R.id.shakeStatusText);
                if (t != null) { t.setVisibility(View.VISIBLE); t.setText("Shake detected! 📳"); }
            }
        }
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null)
            sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) sensorManager.unregisterListener(shakeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        TextView shakeStatusText = findViewById(R.id.shakeStatusText);
        if (accelerometer == null && shakeStatusText != null)
            shakeStatusText.setText("Accelerometer not available");

        bottomNavigationView = findViewById(R.id.bottom_nav_profile);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        TextView nameText    = findViewById(R.id.nameText);
        TextView surnameText = findViewById(R.id.surnameText);
        TextView emailText   = findViewById(R.id.emailText);

        TextView deviceModelText        = findViewById(R.id.deviceModelText);
        TextView deviceIdText           = findViewById(R.id.deviceIdText);
        TextView deviceManufacturerText = findViewById(R.id.deviceManufacturerText);
        deviceModelText.setText(android.os.Build.MODEL);
        deviceIdText.setText(android.os.Build.ID);
        deviceManufacturerText.setText(android.os.Build.MANUFACTURER);

        ImageView profilePhoto = findViewById(R.id.profilePhoto);
        profilePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) openProfileCamera();
            else requestCameraPermission.launch(Manifest.permission.CAMERA);
        });

        Button btnShowSensors    = findViewById(R.id.btnShowSensors);
        Button btnVerification   = findViewById(R.id.btnBookVerification);

        btnShowSensors.setOnClickListener(v -> {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            StringBuilder sb = new StringBuilder();
            for (Sensor s : sensors)
                sb.append("• ").append(s.getName())
                        .append(" (Type: ").append(s.getType()).append(")\n\n");
            new AlertDialog.Builder(this)
                    .setTitle("Available Sensors").setMessage(sb.toString())
                    .setPositiveButton("Close", null).show();
        });

        int userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) { startActivity(new Intent(this, Login.class)); finish(); return; }

        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog(userId));

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
                        Glide.with(this).load(Uri.parse(user.profilePhotoUri))
                                .circleCrop().into(profilePhoto);
                    }

                    if (user.isAdmin()) {
                        btnVerification.setVisibility(View.VISIBLE);
                        btnVerification.setOnClickListener(v ->
                                startActivity(new Intent(this, BookVerification.class)));
                    } else {
                        btnVerification.setVisibility(View.GONE);
                    }
                }
            });
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(getBaseContext(), Title.class)); return true;
                } else if (id == R.id.menu_add_book) {
                    startActivity(new Intent(getBaseContext(), AddBook.class)); return true;
                } else if (id == R.id.menu_library) {
                    startActivity(new Intent(getBaseContext(), Library.class)); return true;
                } else if (id == R.id.menu_map) {
                    startActivity(new Intent(getBaseContext(), Map.class)); return true;
                } else if (id == R.id.menu_profile) {
                    Intent i = new Intent(getBaseContext(), Profile.class);
                    i.putExtra("userId", userId);
                    startActivity(i); return true;
                }
                return false;
            }
        });
    }

    private void showChangePasswordDialog(int userId) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, 0);

        EditText currentInput = new EditText(this);
        currentInput.setHint("Current password");
        currentInput.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText newInput = new EditText(this);
        newInput.setHint("New password");
        newInput.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText confirmInput = new EditText(this);
        confirmInput.setHint("Confirm new password");
        confirmInput.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(currentInput);
        layout.addView(newInput);
        layout.addView(confirmInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change password")
                .setView(layout)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String current = currentInput.getText().toString();
                    String newPass = newInput.getText().toString();
                    String confirm = confirmInput.getText().toString();

                    if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(this, "New password must be at least 6 characters",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirm)) {
                        Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserRep userRep = new UserRep(getApplication());
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        User user = userRep.getUserByIdDirect(userId);
                        if (user == null) {
                            runOnUiThread(() -> Toast.makeText(this, "User not found",
                                    Toast.LENGTH_SHORT).show());
                            return;
                        }
                        if (!user.password.equals(current)) {
                            runOnUiThread(() -> Toast.makeText(this, "Current password is incorrect",
                                    Toast.LENGTH_SHORT).show());
                            return;
                        }
                        if (newPass.equals(current)) {
                            runOnUiThread(() -> Toast.makeText(this,
                                    "New password must be different from the current one",
                                    Toast.LENGTH_SHORT).show());
                            return;
                        }
                        user.password = newPass;
                        userRep.update(user);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    });
                }));

        dialog.show();
    }

    private void openProfileCamera() {
        File photoDir = new File(getFilesDir(), "profiles");
        if (!photoDir.exists()) photoDir.mkdirs();
        File photoFile = new File(photoDir, "profile_" + System.currentTimeMillis() + ".jpg");
        cameraImageUri = FileProvider.getUriForFile(this, AUTHORITY, photoFile);
        cameraLauncher.launch(cameraImageUri);
    }
}