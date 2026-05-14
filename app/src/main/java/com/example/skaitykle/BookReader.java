package com.example.skaitykle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.example.skaitykle.DataBase.UserBook;
import com.example.skaitykle.DataBase.UserBookViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BookReader extends AppCompatActivity {
    GestureDetector gestureDetector;
    Toolbar toolbar;
    TextView pageCountView;
    SeekBar seekBar;
    TextView progressLabel;
    ImageView currentPageView;
    ImageView nextPageView;

    UserBookViewModel userBookViewModel;

    String title, author, description, path;
    int totalPages, pagesRead, readingProgress, userBookId, userId, bookId;
    boolean commentSuggestionShown;

    PdfRenderer pdfRenderer;
    PdfRenderer.Page currentPage;

    boolean isImmersiveMode = true;

    boolean isAnimating = false;

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


        currentPageView = findViewById(R.id.reader_page_current);
        nextPageView = findViewById(R.id.reader_page_next);
        pageCountView = findViewById(R.id.reader_page_count);
        seekBar = findViewById(R.id.reader_seek_bar);
        progressLabel = findViewById(R.id.reader_progress_label);

        userBookViewModel = new ViewModelProvider(this).get(UserBookViewModel.class);

        openPdf();

        if(pdfRenderer != null){
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


           @Override
           public boolean onSingleTapUp(MotionEvent e) {
                if (isImmersiveMode) {
                    exitImmersiveMode();
                } else {
                    enterImmersiveMode();
                }
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

        View readerArea = findViewById(R.id.reader_page_current);
        readerArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                v.performClick();
                return true;
            }
        });


        readerArea.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return true;
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

        enterImmersiveMode();
    }


    private void enterImmersiveMode() {
        isImmersiveMode = true;

        toolbar.animate().alpha(0f).setDuration(200).withEndAction(() ->
                toolbar.setVisibility(View.GONE)).start();
        seekBar.animate().alpha(0f).setDuration(200).withEndAction(() ->
                seekBar.setVisibility(View.GONE)).start();

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams)
                        currentPageView.getLayoutParams();
        params.topToBottom = -1;
        params.topToTop = R.id.bookReaderMain;
        currentPageView.setLayoutParams(params);
    }


    private void exitImmersiveMode() {
        isImmersiveMode = false;

        toolbar.setVisibility(View.VISIBLE);
        toolbar.setAlpha(0f);
        toolbar.animate().alpha(1f).setDuration(200).start();

        seekBar.setVisibility(View.VISIBLE);
        seekBar.setAlpha(0f);
        seekBar.animate().alpha(1f).setDuration(200).start();

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams)
                        currentPageView.getLayoutParams();
        params.topToBottom = R.id.reader_toolbar;
        params.topToTop = -1;
        currentPageView.setLayoutParams(params);
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

        if(pageIndex >= pdfRenderer.getPageCount())
            pageIndex = pdfRenderer.getPageCount() - 1;

        pagesRead = pageIndex;

        Bitmap bitmap = renderPage(pageIndex);
        currentPageView.setImageBitmap(bitmap);

        updateProgress();
    }


    private Bitmap renderPage(int pageIndex) {
        if (pdfRenderer == null) return null;

        if (pageIndex >= pdfRenderer.getPageCount()) {
            pageIndex = pdfRenderer.getPageCount() - 1;
        }

        PdfRenderer.Page page = pdfRenderer.openPage(pageIndex);

        int screenWidth  = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        float pageWidth  = page.getWidth();
        float pageHeight = page.getHeight();

        int renderHeight = screenHeight;
        int renderWidth  = (int) (pageWidth / pageHeight * renderHeight);

        if (renderWidth > screenWidth * 2) {
            renderWidth  = screenWidth * 2;
            renderHeight = (int) (pageHeight / pageWidth * renderWidth);
        }

        Bitmap bitmap = Bitmap.createBitmap(renderWidth, renderHeight, Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        page.close();

        return bitmap;
    }


    private void animatePageSlide(Bitmap nextBitmap, boolean forward) {
        if (isAnimating) return;
        isAnimating = true;

        nextPageView.setImageBitmap(nextBitmap);
        nextPageView.setVisibility(View.VISIBLE);

        float width = currentPageView.getWidth();

        float startNext = forward ? width : -width;
        float endCurrent = forward ? -width : width;

        nextPageView.setTranslationX(startNext);

        nextPageView.setAlpha(0.7f);
        currentPageView.setAlpha(1f);

        nextPageView.animate()
                .translationX(0)
                .alpha(1f)
                .setDuration(300)
                .start();

        currentPageView.animate()
                .translationX(endCurrent)
                .alpha(0.3f)
                .setDuration(300)
                .withEndAction(() -> {
                    currentPageView.setImageBitmap(nextBitmap);
                    currentPageView.setTranslationX(0);
                    currentPageView.setAlpha(1f);

                    nextPageView.setVisibility(View.GONE);

                    pagesRead += forward ? 1 : -1;
                    saveProgress();
                    updateProgress();

                    isAnimating = false;
                })
                .start();
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
            Bitmap next = renderPage(pagesRead + 1);
            animatePageSlide(next, true);
            saveProgress();
        }
    }


    private void previousPage(){
        if(pagesRead > 0){
            Bitmap prev = renderPage(pagesRead - 1);
            animatePageSlide(prev, false);
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