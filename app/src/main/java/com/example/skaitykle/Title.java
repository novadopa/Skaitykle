package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.BooksViewModel;
import com.example.skaitykle.DataBase.GenreRow;
import com.example.skaitykle.DataBase.GenreRowAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Title extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private BooksViewModel booksViewModel;
    private List<Book> allBooks = new ArrayList<>();
    private List<GenreRow> allRows = new ArrayList<>();
    private GenreRowAdapter genreRowAdapter;
    private static final List<String> GENRES =
            Arrays.asList("Dystopian", "Drama", "Fantasy", "Comedy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_title);

        RecyclerView recyclerView = findViewById(R.id.RecyclerViewTitleBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        genreRowAdapter = new GenreRowAdapter();
        recyclerView.setAdapter(genreRowAdapter);

        Button dystopian = findViewById(R.id.button_Genres);
        Button dramas = findViewById(R.id.button_Dramas);
        Button fantasy = findViewById(R.id.button_Fantasy);
        Button comedy = findViewById(R.id.button_Comedy);

        dystopian.setOnClickListener(view -> filterByGenre("Dystopian"));
        dramas.setOnClickListener(view -> filterByGenre("Drama"));
        fantasy.setOnClickListener(view -> filterByGenre("Fantasy"));
        comedy.setOnClickListener(view -> filterByGenre("Comedy"));

        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        booksViewModel.getBooks().observe(this, books -> {
            allBooks = books;
            allRows = buildGenreRows(books, GENRES);
            genreRowAdapter.setGenreRows(allRows);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int userId = getIntent().getIntExtra("userId", -1);

        bottomNavigationView = findViewById(R.id.bottom_nav_title);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(getBaseContext(), Title.class));
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

    private List<GenreRow> buildGenreRows(List<Book> books, List<String> genres) {
        List<GenreRow> rows = new ArrayList<>();
        for (String genre : genres) {
            List<Book> filtered = new ArrayList<>();
            for (Book book : books) {
                if (book.getGenres().contains(genre)) {
                    filtered.add(book);
                }
            }
            if (!filtered.isEmpty()) {
                rows.add(new GenreRow(genre, filtered));
            }
        }
        return rows;
    }

    private void filterByGenre(String genre) {
        List<GenreRow> filtered = new ArrayList<>();
        for (GenreRow row : allRows) {
            if (row.genreName.equals(genre)) {
                filtered.add(row);
            }
        }
        genreRowAdapter.setGenreRows(filtered);
    }
}