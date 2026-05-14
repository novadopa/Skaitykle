package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CommentWriting extends AppCompatActivity {
    RatingBar ratingBar;
    EditText editText;
    Button cancelButton;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment_writing);

        ratingBar = findViewById(R.id.bookRatingBar);
        editText = findViewById(R.id.commentEditText);
        cancelButton = findViewById(R.id.cancelCommentButton);
        saveButton = findViewById(R.id.saveCommentButton);

        int bookId = getIntent().getIntExtra("BookId", -1);
        String title = getIntent().getStringExtra("BookTitle");
        String author = getIntent().getStringExtra("BookAuthor");
        String description = getIntent().getStringExtra("BookDescription");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ratingStarts = ratingBar.getRating();

                if(ratingStarts == 0){
                    Toast.makeText(getApplicationContext(), "Stars cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Intent saveComment = new Intent(getBaseContext(), BookDetails.class);
                    saveComment.putExtra("BookId", bookId);
                    saveComment.putExtra("BookTitle", title);
                    saveComment.putExtra("BookAuthor", author);
                    saveComment.putExtra("BookDescription", description);
                    startActivity(saveComment);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelComment = new Intent(getBaseContext(), Title.class);
                startActivity(cancelComment);
                finish();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}