package com.example.skaitykle;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_details);

        String title = getIntent().getStringExtra("BookTitle");
        String author = getIntent().getStringExtra("BookAuthor");
        String description = getIntent().getStringExtra("BookDescription");

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewAuthor = findViewById(R.id.textViewBookAuthor);
        TextView textViewDescription = findViewById(R.id.textViewDescription);

        textViewTitle.setText(title);
        textViewAuthor.setText(author);
        textViewDescription.setText(description);

        ImageButton imageButton = findViewById(R.id.button_Back);
        imageButton.setOnClickListener(view -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}