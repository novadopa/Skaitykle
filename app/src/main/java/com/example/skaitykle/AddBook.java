package com.example.skaitykle;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.BooksViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddBook extends AppCompatActivity {

    private static final String AUTHORITY = "com.example.skaitykle.fileprovider";

    private BottomNavigationView bottomNavigationView;
    private Uri pdfUri;
    private Uri cameraImageUri;
    private BooksViewModel booksViewModel;

    // PDF picker
    private final ActivityResultLauncher<String> pdfPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> pdfUri = uri
            );

    // Camera launcher
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {
                        if (success && cameraImageUri != null) {
                            showCoverPreview(cameraImageUri);
                            ((EditText) findViewById(R.id.editCover)).setText("");
                        }
                    });

    // Camera permission request
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            openCamera();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Camera permission denied",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        EditText title = findViewById(R.id.editTitle);
        EditText desc = findViewById(R.id.editDescription);
        EditText author = findViewById(R.id.editAuthor);
        EditText country = findViewById(R.id.editCountry);
        EditText cover = findViewById(R.id.editCover);
        EditText pages = findViewById(R.id.editPages);

        AutoCompleteTextView genre = findViewById(R.id.editGenre);

        Button upload = findViewById(R.id.buttonPickPdf);
        Button save = findViewById(R.id.buttonSaveBook);

        ImageButton camera = findViewById(R.id.buttonCamera);

        // Cover preview from URL
        cover.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {

                String url = cover.getText().toString().trim();

                if (!url.isEmpty()) {

                    cameraImageUri = null;

                    showCoverPreview(Uri.parse(url));
                }
            }
        });

        // Camera button
        camera.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                requestCameraPermission.launch(Manifest.permission.CAMERA);
            }
        });

        // Genre suggestions
        booksViewModel.getBooks().observe(this, books -> {

            List<String> usedGenres = new ArrayList<>();

            for (Book book : books) {

                for (String g : book.getGenres()) {

                    if (!usedGenres.contains(g)) {
                        usedGenres.add(g);
                    }
                }
            }

            genre.setAdapter(
                    new ArrayAdapter<>(
                            AddBook.this,
                            android.R.layout.simple_dropdown_item_1line,
                            usedGenres
                    )
            );
        });

        // PDF upload
        upload.setOnClickListener(v ->
                pdfPicker.launch("application/pdf")
        );

        String titleText = title.getText().toString().trim();
        String authorText = author.getText().toString().trim();
        String pagesText = pages.getText().toString().trim();

        // Save book
        save.setOnClickListener(v -> {

            if (pdfUri == null) {
                Toast.makeText(this, "No file is uploaded", Toast.LENGTH_SHORT).show();
                vibrateError();
                animateBounce(save);
                return;
            }

            if (titleText.isEmpty()) {
                Toast.makeText(this, "No title is written", Toast.LENGTH_SHORT).show();
                vibrateError();
                animateBounce(title);
                return;
            }

            if (authorText.isEmpty()) {
                Toast.makeText(this, "No author is written", Toast.LENGTH_SHORT).show();
                vibrateError();
                animateBounce(author);
                return;
            }

            if (pagesText.isEmpty()) {
                Toast.makeText(this, "No pages are written", Toast.LENGTH_SHORT).show();
                vibrateError();
                animateBounce(pages);
                return;
            }

            // Use camera image if available
            String coverString = (cameraImageUri != null)
                    ? cameraImageUri.toString()
                    : cover.getText().toString().trim();

            Book book = new Book(
                    titleText,
                    desc.getText().toString(),
                    authorText,
                    country.getText().toString(),
                    pdfUri.toString(),
                    coverString,
                    Integer.parseInt(pagesText),
                    Arrays.asList(genre.getText().toString())
            );

            booksViewModel.insert(book);

            Toast.makeText(
                    this,
                    "Book saved",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });



        // Bottom navigation
        int userId = getIntent().getIntExtra("userId", -1);

        bottomNavigationView = findViewById(R.id.bottom_nav_add);

        bottomNavigationView.setSelectedItemId(R.id.menu_add_book);

        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(
                            @NonNull MenuItem menuItem
                    ) {

                        int id = menuItem.getItemId();

                        if (id == R.id.menu_home) {

                            startActivity(
                                    new Intent(
                                            getBaseContext(),
                                            Title.class
                                    )
                            );

                            return true;

                        } else if (id == R.id.menu_add_book) {

                            startActivity(
                                    new Intent(
                                            getBaseContext(),
                                            AddBook.class
                                    )
                            );

                            return true;

                        } else if (id == R.id.menu_library) {

                            startActivity(
                                    new Intent(
                                            getBaseContext(),
                                            Library.class
                                    )
                            );

                            return true;

                        } else if (id == R.id.menu_profile) {

                            Intent i = new Intent(
                                    getBaseContext(),
                                    Profile.class
                            );

                            i.putExtra("userId", userId);

                            startActivity(i);

                            return true;
                        }

                        return false;
                    }
                });
    }

    private void vibrateError() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
            );
        }
    }

    // Open camera
    private void openCamera() {

        cameraImageUri = createCoverImageUri();

        cameraLauncher.launch(cameraImageUri);
    }

    // Create image file URI
    private Uri createCoverImageUri() {

        File coversDir = new File(getFilesDir(), "covers");

        if (!coversDir.exists()) {
            coversDir.mkdirs();
        }

        File imageFile = new File(
                coversDir,
                "cover_" + System.currentTimeMillis() + ".jpg"
        );

        return FileProvider.getUriForFile(
                this,
                AUTHORITY,
                imageFile
        );
    }

    // Show preview
    private void showCoverPreview(Uri uri) {

        ImageView preview = findViewById(R.id.coverPreview);

        preview.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.cover)
                .error(R.drawable.cover)
                .centerCrop()
                .into(preview);
    }



    // Bounce animation
    private void animateBounce(View view) {

        ObjectAnimator bounce = ObjectAnimator.ofFloat(
                view,
                "translationY",
                0f,
                -40f,
                0f
        );

        bounce.setDuration(600);

        bounce.setInterpolator(new BounceInterpolator());

        bounce.start();
    }
}