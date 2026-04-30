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

public class Login extends AppCompatActivity {

    private EditText emailField, passwordField;
    private FrameLayout loadingOverlay;
    private MaterialButton loginButton;
    private AppDatabase db;

    private static final long MIN_LOADING_MS      = 1500;
    private static final long RED_BUTTON_DURATION = 500;

    private User    loggedInUser = null;
    private boolean timerDone   = false;
    private boolean dbDone      = false;
    private boolean loginFailed = false;

    private final Handler resetButtonHandler = new Handler(Looper.getMainLooper());
    private Runnable resetButtonRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        emailField     = findViewById(R.id.emailLogin);
        passwordField  = findViewById(R.id.passwordLogin);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loginButton    = findViewById(R.id.loginLogin);
        MaterialButton registerButton = findViewById(R.id.registerInLogin);

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        db = AppDatabase.getInstance(this);

        loginButton.setOnClickListener(view -> {
            String email    = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                shakeScreen();
                flashButtonRed();
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            startLogin(email, password);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startLogin(String email, String password) {
        loggedInUser = null;
        timerDone    = false;
        dbDone       = false;
        loginFailed  = false;

        loadingOverlay.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            timerDone = true;
            maybeNavigate();
        }, MIN_LOADING_MS);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = db.userDao().login(email, password);

            runOnUiThread(() -> {
                dbDone = true;
                if (user != null) {
                    loggedInUser = user;
                } else {
                    loginFailed = true;
                }
                maybeNavigate();
            });
        });
    }

    private void maybeNavigate() {
        if (!timerDone || !dbDone) return;

        loadingOverlay.setVisibility(View.GONE);

        if (loginFailed) {
            shakeScreen();
            flashButtonRed();
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome back, " + loggedInUser.getFirstName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, Title.class);
            intent.putExtra("userId", loggedInUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    private void flashButtonRed() {
        if (resetButtonRunnable != null) {
            resetButtonHandler.removeCallbacks(resetButtonRunnable);
        }

        loginButton.setBackgroundTintList(
                ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));

        resetButtonRunnable = () -> loginButton.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.purple_primary));

        resetButtonHandler.postDelayed(resetButtonRunnable, RED_BUTTON_DURATION);
    }

    private void shakeScreen() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.bookReaderMain).startAnimation(shake);
    }
}