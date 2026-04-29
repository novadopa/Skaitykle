package com.example.skaitykle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.UserBook;
import com.google.android.material.imageview.ShapeableImageView;

public class BookDetails extends AppCompatActivity {

    private int bookId;
    private int totalPages;
    String title, author, description, path, coverUri;
    private static final int currentUserId = 1;

    // Flip state
    private boolean showingFront = true;
    private FrameLayout cardFlipContainer;
    private ShapeableImageView coverImage;
    private LinearLayout descriptionBackLayout;
    private TextView textViewDescriptionBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_details);

        bookId        = getIntent().getIntExtra("BookId", -1);
        title         = getIntent().getStringExtra("BookTitle");
        author        = getIntent().getStringExtra("BookAuthor");
        description   = getIntent().getStringExtra("BookDescription");
        totalPages    = getIntent().getIntExtra("BookTotalPages", 0);
        path          = getIntent().getStringExtra("BookPath");
        coverUri      = getIntent().getStringExtra("BookCover");

        TextView textViewTitle       = findViewById(R.id.textViewTitle);
        TextView textViewAuthor      = findViewById(R.id.textViewBookAuthor);
        TextView textViewDescription = findViewById(R.id.textViewDescriptionBack);
        TextView textViewPages       = findViewById(R.id.textView_Pages);

        textViewTitle.setText(title);
        textViewAuthor.setText(author);
        textViewDescription.setText(description);
        textViewPages.setText(totalPages + " pages");

        cardFlipContainer     = findViewById(R.id.cardFlipContainer);
        coverImage            = findViewById(R.id.shapeableImageView);
        descriptionBackLayout = findViewById(R.id.descriptionBackLayout);
        textViewDescriptionBack = findViewById(R.id.textViewDescriptionBack);
        textViewDescriptionBack.setText(description);

        loadCoverAndExtractColor();

        cardFlipContainer.setOnClickListener(v -> flipCard());

        Button readButton = findViewById(R.id.button_ReadBook);
        readButton.setOnClickListener(v -> addBookToLibrary());

        ImageButton imageButton = findViewById(R.id.button_Back);
        imageButton.setOnClickListener(view -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadCoverAndExtractColor() {
        if (coverUri == null || coverUri.isEmpty()) return;

        Glide.with(this)
                .asBitmap()
                .load(coverUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

                        coverImage.setImageBitmap(bitmap);


                        Palette.from(bitmap).generate(palette -> {
                            int dominantColor = palette.getDominantColor(Color.WHITE);
                            applyDominantColor(dominantColor);
                        });
                    }

                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                });
    }

    private void applyDominantColor(int color) {

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(40f);
        bg.setColor(color);
        descriptionBackLayout.setBackground(bg);


        textViewDescriptionBack.setTextColor(isDarkColor(color) ? Color.WHITE : Color.BLACK);
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color)
                + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }



    private void flipCard() {

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardFlipContainer, "rotationY", 0f, 90f);
        flipOut.setDuration(200);
        flipOut.setInterpolator(new AccelerateInterpolator());


        ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardFlipContainer, "rotationY", -90f, 0f);
        flipIn.setDuration(200);
        flipIn.setInterpolator(new DecelerateInterpolator());

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (showingFront) {
                    coverImage.setVisibility(View.GONE);
                    descriptionBackLayout.setVisibility(View.VISIBLE);
                } else {
                    coverImage.setVisibility(View.VISIBLE);
                    descriptionBackLayout.setVisibility(View.GONE);
                }
                showingFront = !showingFront;
                flipIn.start();
            }
        });

        flipOut.start();
    }



    private void addBookToLibrary() {
        AppDatabase db = AppDatabase.getInstance(this);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserBook existingBook = db.userBookDao().getBookByUserAndBook(currentUserId, bookId);
            UserBook userBook;
            if (existingBook != null) {
                userBook = existingBook;
            } else {
                UserBook newUserBook = new UserBook(currentUserId, bookId, 0, 0);
                long newId = db.userBookDao().insert(newUserBook);
                newUserBook.ubId = (int) newId;
                userBook = newUserBook;
            }
            int userBookId = userBook.ubId;
            int readPages  = userBook.readPages;
            runOnUiThread(() -> {
                Intent intent = new Intent(BookDetails.this, BookReader.class);
                intent.putExtra("BookTitle",      title);
                intent.putExtra("BookAuthor",     author);
                intent.putExtra("BookDescription",description);
                intent.putExtra("BookPath",       path);
                intent.putExtra("BookTotalPages", totalPages);
                intent.putExtra("BookCover",      coverUri);
                intent.putExtra("BookPagesRead",  readPages);
                intent.putExtra("BookId",         bookId);
                intent.putExtra("UserId",         currentUserId);
                intent.putExtra("UserBookId",     userBookId);
                startActivity(intent);
            });
        });
    }
}