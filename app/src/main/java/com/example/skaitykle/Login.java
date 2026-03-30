package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.User;

public class Login extends AppCompatActivity {

    private EditText emailField, passwordField;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        emailField    = findViewById(R.id.emailLogin);
        passwordField = findViewById(R.id.passwordLogin);
        Button loginButton = findViewById(R.id.loginLogin);
        Button registerButton = findViewById(R.id.registerInLogin); // ← add this

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        db = AppDatabase.getInstance(this);

        loginButton.setOnClickListener(view -> {
            String email    = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                User user = db.userDao().login(email, password);

                runOnUiThread(() -> {
                    if (user != null) {
                        Toast.makeText(this, "Welcome back, " + user.getFirstName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Title.class);
                        intent.putExtra("userId", user.getUid());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}