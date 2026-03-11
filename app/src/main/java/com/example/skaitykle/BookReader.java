package com.example.skaitykle;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_reader);

        String title      = getIntent().getStringExtra("title");
        String author     = getIntent().getStringExtra("author");
        int    totalPages = getIntent().getIntExtra("totalPages", 0);
        int    pagesRead  = getIntent().getIntExtra("pagesRead",  0);

        Toolbar toolbar = findViewById(R.id.reader_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView authorView = findViewById(R.id.reader_author);
        authorView.setText(author);

        TextView pageCountView = findViewById(R.id.reader_page_count);
        pageCountView.setText("Page " + pagesRead + " of " + totalPages);

        ProgressBar progressBar = findViewById(R.id.reader_progress_bar);
        int progress = totalPages > 0 ? (pagesRead * 100) / totalPages : 0;
        progressBar.setProgress(progress);

        TextView progressLabel = findViewById(R.id.reader_progress_label);
        progressLabel.setText(progress + "% read");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}