package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.Review;

public class CommentWriting extends ScreenBrightnessManager {
    RatingBar ratingBar;
    EditText editText;
    Button cancelButton;
    Button saveButton;

    private static final int currentUserId = 1;

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
        String authorCountry = getIntent().getStringExtra("BookAuthorCountry");
        String path = getIntent().getStringExtra("BookPath");
        String coverUri = getIntent().getStringExtra("BookCover");
        int totalPages = getIntent().getIntExtra("BookTotalPages", 0);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ratingStars = ratingBar.getRating();

                if(ratingStars == 0){
                    Toast.makeText(getApplicationContext(), "Stars cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }else{
                    String commentText = editText.getText().toString().trim();

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

                        Review existing = db.reviewDao()
                                .getReviewByUserAndBook(bookId, currentUserId);

                        Review review = new Review(bookId, currentUserId,
                                ratingStars, commentText);

                        if (existing != null) {
                            review.reviewId = existing.reviewId;
                        }

                        db.reviewDao().insert(review);
                    });

                    runOnUiThread(() -> finish());
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