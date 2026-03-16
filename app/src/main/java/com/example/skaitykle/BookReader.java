package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookReader extends AppCompatActivity {
    GestureDetector gestureDetector;
    String title;

    int totalPages;
    int pagesRead;
    int readingProgress;

    Toolbar toolbar;
    TextView pageCountView;
    ProgressBar progressBar;
    TextView progressLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_reader);

        title      = getIntent().getStringExtra("title");
        totalPages = getIntent().getIntExtra("totalPages", 0);
        pagesRead  = getIntent().getIntExtra("pagesRead",  0);

        toolbar = findViewById(R.id.reader_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pageCountView = findViewById(R.id.reader_page_count);
        progressBar = findViewById(R.id.reader_progress_bar);
        progressLabel = findViewById(R.id.reader_progress_label);

        updateProgress();


        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){
            private static final int swipeThreshold = 60;
            private static  final int swipeVelocityThreshold = 60;

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

        View readerArea = findViewById(R.id.main);
        readerArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                v.performClick();
                return true;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("updatePagesRead", pagesRead);

            setResult(RESULT_OK, intent);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateProgress(){
        pageCountView.setText("Page " + pagesRead + " of " + totalPages);

        if (totalPages > 0) {
            readingProgress = (pagesRead * 100)/ totalPages;
        }else{
            readingProgress = 0;
        }

        progressBar.setProgress(readingProgress);
        progressLabel.setText(readingProgress + "% read");
    }


    private void nextPage(){
        if(pagesRead < totalPages){
            pagesRead++;
            updateProgress();
        }
    }


    private void previousPage(){
        if(pagesRead > 0){
            pagesRead--;
            updateProgress();
        }
    }
}