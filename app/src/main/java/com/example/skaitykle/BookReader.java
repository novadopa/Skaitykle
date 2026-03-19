package com.example.skaitykle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.UserBook;
import com.example.skaitykle.DataBase.UserBookViewModel;

import java.util.Arrays;

public class BookReader extends AppCompatActivity {
    GestureDetector gestureDetector;
    String title;
    String author;
    String description;
    Toolbar toolbar;
    TextView pageCountView;
    SeekBar seekBar;
    TextView progressLabel;

    UserBookViewModel userBookViewModel;

    int totalPages;
    int pagesRead;
    int readingProgress;
    int userBookId;
    int userId;
    int bookId;


    boolean commentSuggestionShown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_reader);

        title      = getIntent().getStringExtra("BookTitle");
        totalPages = getIntent().getIntExtra("BookTotalPages", 0);
        pagesRead  = getIntent().getIntExtra("BookPagesRead",  0);
        userBookId = getIntent().getIntExtra("userBookId", -1);
        userId     = getIntent().getIntExtra("UserId",      1);
        bookId     = getIntent().getIntExtra("BookId",      0);
        author = getIntent().getStringExtra("BookAuthor");
        description = getIntent().getStringExtra("BookDescription");

        toolbar = findViewById(R.id.reader_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pageCountView = findViewById(R.id.reader_page_count);
        seekBar = findViewById(R.id.reader_seek_bar);
        progressLabel = findViewById(R.id.reader_progress_label);

        userBookViewModel = new ViewModelProvider(this).get(UserBookViewModel.class);

        updateProgress();

        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){
            private static final int swipeThreshold = 100;
            private static  final int swipeVelocityThreshold = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onFling(MotionEvent ev1, MotionEvent ev2, float velocityX,
                                   float velocityY){
                float diffX = ev2.getX() - ev1.getX();

                if(Math.abs(diffX)>swipeThreshold && Math.abs(velocityX)>swipeVelocityThreshold){
                    if(diffX>0){
                        previousPage();
                    }else{
                        nextPage();
                    }
                    return true;
                }

                return false;

            }

        });

        View readerArea = findViewById(R.id.reader_placeholder_text);
        readerArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                v.performClick();
                return true;
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && totalPages > 0){
                    pagesRead = (progress * totalPages) / 100;
                    updateProgress();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {saveProgress();}
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveProgress();
    }


    private void saveProgress() {
        if (userBookId != -1) {
            UserBook updated = new UserBook(userId, bookId, pagesRead, pagesRead);
            updated.setUserBookId(userBookId);
            userBookViewModel.update(updated);
        } else {
            UserBook newUserBook = new UserBook(userId, bookId, pagesRead, pagesRead);
            userBookViewModel.insert(newUserBook);
        }
    }


    private void updateProgress(){
        pageCountView.setText("Page " + pagesRead + " of " + totalPages);

        if (totalPages > 0) {
            readingProgress = (pagesRead * 100)/ totalPages;
        }else{
            readingProgress = 0;
        }

        seekBar.setProgress(readingProgress);
        progressLabel.setText(readingProgress + "% read");
    }


    private void nextPage(){
        if(pagesRead < totalPages){
            pagesRead++;
            updateProgress();
            saveProgress();
        }
    }


    private void previousPage(){
        if(pagesRead > 0){
            pagesRead--;
            updateProgress();
            saveProgress();
        }
    }


    private void showCommentSuggestion(){
        commentSuggestionShown = true;

        new AlertDialog.Builder(this).setMessage("You have finished this book. " +
                "Would you like to rate it").setPositiveButton("Yes, I would", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent commentIntent = new Intent(BookReader.this, CommentWriting.class);

                commentIntent.putExtra("BookTitle", title);
                commentIntent.putExtra("BookId", bookId);
                commentIntent.putExtra("UserId", userId);
                commentIntent.putExtra("BookAuthor", author);
                commentIntent.putExtra("BookDescription", description);
                startActivity(commentIntent);
                finish();
            }
        }).setNegativeButton("No thank you", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }


    private void handleExit(){
        saveProgress();

        if(pagesRead > 0 && !commentSuggestionShown && pagesRead >= totalPages){
            showCommentSuggestion();
        }else{
            finish();
        }
    }
}