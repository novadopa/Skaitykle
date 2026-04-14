package com.example.skaitykle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.UserBook;
import com.example.skaitykle.DataBase.UserBookViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class BookReader extends AppCompatActivity {
    GestureDetector gestureDetector;
    Toolbar toolbar;
    TextView pageCountView;
    SeekBar seekBar;
    TextView progressLabel;
    ImageView pageView;

    UserBookViewModel userBookViewModel;

    String title, author, description, path;
    int totalPages, pagesRead, readingProgress, userBookId, userId, bookId;
    boolean commentSuggestionShown;

    PdfRenderer pdfRenderer;
    PdfRenderer.Page currentPage;
    int pdfPageCount;

    boolean isImmersive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_reader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title      = getIntent().getStringExtra("BookTitle");
        totalPages = getIntent().getIntExtra("BookTotalPages", 0);
        pagesRead  = getIntent().getIntExtra("BookPagesRead",  0);
        userBookId = getIntent().getIntExtra("userBookId", -1);
        userId     = getIntent().getIntExtra("UserId",      1);
        bookId     = getIntent().getIntExtra("BookId",      0);
        author = getIntent().getStringExtra("BookAuthor");
        description = getIntent().getStringExtra("BookDescription");
        path = getIntent().getStringExtra("BookPath");

        pagesRead = getIntent().getIntExtra("BookPagesRead", 0);

        toolbar = findViewById(R.id.reader_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //toolbar.setVisibility(View.GONE);

        pageView = findViewById(R.id.reader_page_view);
        pageCountView = findViewById(R.id.reader_page_count);
        seekBar = findViewById(R.id.reader_seek_bar);
        progressLabel = findViewById(R.id.reader_progress_label);

        userBookViewModel = new ViewModelProvider(this).get(UserBookViewModel.class);

        openPdf();

        if(pdfRenderer != null){
            /*pdfPageCount = pdfRenderer.getPageCount();
            totalPages = pdfPageCount;*/
            totalPages = pdfRenderer.getPageCount();

            AppDatabase.databaseWriteExecutor.execute(() -> {
                AppDatabase.getInstance(BookReader.this)
                        .bookDao().updateTotalPages(bookId, totalPages);
            });
        }

        if(pagesRead >= totalPages && totalPages > 0){
            pagesRead = totalPages - 1;
        }

        showPages(pagesRead);

        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){
            private static final int swipeThreshold = 60;
            private static  final int swipeVelocityThreshold = 60;

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

        View readerArea = findViewById(R.id.reader_page_view);
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
                    int targetPage = (progress * totalPages) / 100;
                    showPages(targetPage);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {saveProgress();}
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


    private void openPdf(){
        if(path == null || path.isEmpty()) return;

        try{
            InputStream inputStream = getAssets().open("books/"+path);
            File tempFile = File.createTempFile("book", ".pdf", getCacheDir());
            tempFile.deleteOnExit();
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int len;
            while((len = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, len);
            outputStream.close();
            inputStream.close();
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(tempFile,
                    ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showPages(int pageIndex){
        if(pdfRenderer == null) return;
        if(pageIndex >= pdfRenderer. getPageCount())
            pageIndex = pdfRenderer.getPageCount() - 1;

        pagesRead = pageIndex;

        if(currentPage != null) currentPage.close();
        currentPage = pdfRenderer.openPage(pageIndex);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (int) ((float) currentPage.getHeight() / currentPage.getWidth() * width);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pageView.setImageBitmap(bitmap);

        updateProgress();
    }


    private void saveProgress() {
        int pageToSave = pagesRead + 1;
        if (userBookId != -1) {
            UserBook updated = new UserBook(userId, bookId, pageToSave, pagesRead);
            updated.setUserBookId(userBookId);
            userBookViewModel.update(updated);
        } else {
            UserBook newUserBook = new UserBook(userId, bookId, pageToSave, pagesRead);
            userBookViewModel.insert(newUserBook);
        }
    }


    private void updateProgress(){
        pageCountView.setText("Page " + (pagesRead+1) + " of " + totalPages);

        if (totalPages > 0) {
            readingProgress = (pagesRead * 100) / (totalPages - 1);
        }else{
            readingProgress = 0;
        }

        seekBar.setProgress(readingProgress);
        progressLabel.setText(readingProgress + "% read");
    }


    private void nextPage(){
        if(pagesRead + 1 < totalPages){
            showPages(pagesRead + 1);
            //updateProgress();
            saveProgress();
        }
    }


    private void previousPage(){
        if(pagesRead > 0){
            showPages(pagesRead - 1);
            //updateProgress();
            saveProgress();
        }
    }


    private void showCommentSuggestion(){
        commentSuggestionShown = true;

        new AlertDialog.Builder(this, R.style.AnimatedDialog).setMessage("You have finished this book. " +
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
                goToLibraryScreen();
            }
        }).setCancelable(false).show();
    }


    private void handleExit(){
        saveProgress();

        if((pagesRead+1) > 0 && !commentSuggestionShown && (pagesRead+1) >= totalPages){
            showCommentSuggestion();
        }else{
            goToLibraryScreen();
        }
    }

    private void goToLibraryScreen(){
        Intent libraryIntent = new Intent(getBaseContext(), Library.class);
        libraryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(libraryIntent);
        finish();
    }
}