package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.User;
import com.google.android.material.button.MaterialButton;

public class Register extends AppCompatActivity {

    private EditText nameField, surnameField, passwordField, emailField;
    private MaterialButton registerButton;
    private FrameLayout loadingOverlay;
    private ProgressBar loadingSpinner;
    private LinearLayout successCheck;
    private AppDatabase db;

    private static final long SPINNER_DURATION_MS  = 800;  // spinner shows for at least this long
    private static final long CHECKMARK_HOLD_MS    = 900;  // how long checkmark stays before navigating
    private static final long RED_BUTTON_DURATION  = 3000;

    private final Handler resetButtonHandler = new Handler(Looper.getMainLooper());
    private Runnable resetButtonRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        nameField      = findViewById(R.id.nameLogin);
        surnameField   = findViewById(R.id.surnameLogin);
        passwordField  = findViewById(R.id.passwordLogin);
        emailField     = findViewById(R.id.emailLogin);
        registerButton = findViewById(R.id.registerLogin);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        successCheck   = findViewById(R.id.successCheck);

        MaterialButton alreadyHaveAccount = findViewById(R.id.paskyraLogin);
        alreadyHaveAccount.setOnClickListener(view -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        db = AppDatabase.getInstance(this);

        registerButton.setOnClickListener(view -> {
            String name     = nameField.getText().toString().trim();
            String surname  = surnameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String email    = emailField.getText().toString().trim();

            if (name.isEmpty() || surname.isEmpty() || password.isEmpty() || email.isEmpty()) {
                shakeScreen();
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                shakeScreen();
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                shakeScreen();
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            attemptRegister(name, surname, email, password);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void attemptRegister(String name, String surname, String email, String password) {
        // Show overlay with spinner
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.VISIBLE);
        successCheck.setVisibility(View.GONE);

        // Track whether spinner minimum time and DB query are both done
        final boolean[] spinnerTimerDone = {false};
        final boolean[] dbDone          = {false};
        final boolean[] registrationOk  = {false};
        final String[]  savedName       = {name};

        Runnable tryShowResult = () -> {
            if (!spinnerTimerDone[0] || !dbDone[0]) return;

            if (registrationOk[0]) {
                showCheckmarkThenNavigate(savedName[0]);
            } else {
                loadingOverlay.setVisibility(View.GONE);
                shakeScreen();
                flashButtonRed();
                Toast.makeText(this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
            }
        };

        // Minimum spinner time
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            spinnerTimerDone[0] = true;
            tryShowResult.run();
        }, SPINNER_DURATION_MS);

        // DB query
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User existing = db.userDao().getUserByEmail(email);

            if (existing == null) {
                User newUser = new User(name, surname, email, password);
                db.userDao().insert(newUser);
                runOnUiThread(() -> {
                    registrationOk[0] = true;
                    dbDone[0] = true;
                    tryShowResult.run();
                });
            } else {
                runOnUiThread(() -> {
                    registrationOk[0] = false;
                    dbDone[0] = true;
                    tryShowResult.run();
                });
            }
        });
    }

    private void showCheckmarkThenNavigate(String name) {
        // Swap spinner for checkmark with a pop animation
        loadingSpinner.setVisibility(View.GONE);
        successCheck.setVisibility(View.VISIBLE);

        Animation popIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        successCheck.startAnimation(popIn);

        // Hold the checkmark briefly, then go to Login
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingOverlay.setVisibility(View.GONE);
            Toast.makeText(this, "Account created! Welcome, " + name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        }, CHECKMARK_HOLD_MS);
    }

    private void flashButtonRed() {
        if (resetButtonRunnable != null) {
            resetButtonHandler.removeCallbacks(resetButtonRunnable);
        }

        registerButton.setBackgroundTintList(
                ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));

        resetButtonRunnable = () -> registerButton.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.purple_primary));

        resetButtonHandler.postDelayed(resetButtonRunnable, RED_BUTTON_DURATION);
    }

    private void shakeScreen() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.bookReaderMain).startAnimation(shake);
    }
}