package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Genres extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genres);

        ImageButton imageButton = findViewById(R.id.imageButton_Back2);
        imageButton.setOnClickListener(view -> finish());

        String[] genres = {"Fantasy", "Science Fiction", "Romance", "Mystery", "Horror"};

        LinearLayout genresContainer = findViewById(R.id.genresContainer);

        for (String genre : genres) {
            Button button = new Button(this);
            button.setText(genre);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(this, Genres.class);
                intent.putExtra("GENRE", genre);
                startActivity(intent);
            });
            genresContainer.addView(button);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}