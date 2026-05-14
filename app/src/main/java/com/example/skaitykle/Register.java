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
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.User;

public class Register extends AppCompatActivity {

    private EditText nameField, surnameField, passwordField, emailField;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        nameField     = findViewById(R.id.nameLogin);
        surnameField  = findViewById(R.id.surnameLogin);
        passwordField = findViewById(R.id.passwordLogin);
        emailField    = findViewById(R.id.emailLogin);
        Button registerButton = findViewById(R.id.registerLogin);
        Button alreadyHaveAccount = findViewById(R.id.paskyraLogin);
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

            // Basic validation
            if (name.isEmpty() || surname.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                User existing = db.userDao().getUserByEmail(email);

                runOnUiThread(() -> {
                    if (existing != null) {
                        Toast.makeText(this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            User newUser = new User(name, surname, email, password);
                            db.userDao().insert(newUser);

                            runOnUiThread(() -> {
                                Toast.makeText(this, "Account created! Welcome, " + name, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, Login.class); // ← changed from Title.class
                                startActivity(intent);
                                finish();
                            });
                        });
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