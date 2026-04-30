package com.example.skaitykle;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.BooksViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddBook extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Uri pdfUri;
    private BooksViewModel booksViewModel;

    private final ActivityResultLauncher<String> pdfPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> pdfUri = uri
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        EditText title  = findViewById(R.id.editTitle);
        EditText desc   = findViewById(R.id.editDescription);
        EditText author = findViewById(R.id.editAuthor);
        EditText cover  = findViewById(R.id.editCover);
        EditText pages  = findViewById(R.id.editPages);
        AutoCompleteTextView genre = findViewById(R.id.editGenre);

        Button upload = findViewById(R.id.buttonPickPdf);
        Button save   = findViewById(R.id.buttonSaveBook);

        // Build autocomplete list from all genres already in the database
        booksViewModel.getBooks().observe(this, books -> {
            List<String> usedGenres = new ArrayList<>();
            for (Book book : books) {
                for (String g : book.getGenres()) {
                    if (!usedGenres.contains(g)) {
                        usedGenres.add(g);
                    }
                }
            }
            genre.setAdapter(new ArrayAdapter<>(
                    AddBook.this,
                    android.R.layout.simple_dropdown_item_1line,
                    usedGenres
            ));
        });

        upload.setOnClickListener(v -> pdfPicker.launch("application/pdf"));

        save.setOnClickListener(v -> {

            if (pdfUri == null) {
                Toast.makeText(getBaseContext(), "No file is uploaded", Toast.LENGTH_SHORT).show();
                animateBounce(save);
                return;
            }

            String titleText  = title.getText().toString().trim();
            String authorText = author.getText().toString().trim();
            String pagesText  = pages.getText().toString().trim();

            if (titleText.isEmpty()) {
                Toast.makeText(getBaseContext(), "No title is written", Toast.LENGTH_SHORT).show();
                animateBounce(title);
                return;
            }
            if (authorText.isEmpty()) {
                Toast.makeText(getBaseContext(), "No author is written", Toast.LENGTH_SHORT).show();
                animateBounce(author);
                return;
            }
            if (pagesText.isEmpty()) {
                Toast.makeText(getBaseContext(), "No pages are written", Toast.LENGTH_SHORT).show();
                animateBounce(pages);
                return;
            }

            Book book = new Book(
                    titleText,
                    desc.getText().toString(),
                    authorText,
                    pdfUri.toString(),
                    cover.getText().toString(),
                    Integer.parseInt(pagesText),
                    Arrays.asList(genre.getText().toString())
            );

            booksViewModel.insert(book);
            finish();
        });

        int userId = getIntent().getIntExtra("userId", -1);

        bottomNavigationView = findViewById(R.id.bottom_nav_add);
        bottomNavigationView.setSelectedItemId(R.id.menu_add_book);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(getBaseContext(), Title.class));
                    return true;
                } else if (id == R.id.menu_add_book) {
                    startActivity(new Intent(getBaseContext(), AddBook.class));
                    return true;
                } else if (id == R.id.menu_library) {
                    startActivity(new Intent(getBaseContext(), Library.class));
                    return true;
                } else if (id == R.id.menu_profile) {
                    Intent profileIntent = new Intent(getBaseContext(), Profile.class);
                    profileIntent.putExtra("userId", userId);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            }
        });
    }

    private void animateBounce(View view) {
        ObjectAnimator bounce = ObjectAnimator.ofFloat(view, "translationY", 0f, -40f, 0f);
        bounce.setDuration(600);
        bounce.setInterpolator(new BounceInterpolator());
        bounce.start();
    }
}