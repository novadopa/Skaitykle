package com.example.skaitykle;

import android.net.Uri;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

    private androidx.lifecycle.Observer<List<Book>> searchObserver;
    private androidx.lifecycle.LiveData<List<Book>> currentSearchLiveData;
    private BottomNavigationView bottomNavigationView;
    private BooksViewModel booksViewModel;
    private List<Book> allBooks = new ArrayList<>();
    private List<GenreRow> allRows = new ArrayList<>();
    private GenreRowAdapter genreRowAdapter;

    private SearchDropdownAdapter searchDropdownAdapter;
    private androidx.cardview.widget.CardView searchDropdownCard;
    private static final List<String> GENRES =
            Arrays.asList("Dystopian", "Drama", "Fantasy", "Comedy", "Science Fiction", "Romance", "Mystery", "Horror");

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

        animateCategoryButtons(dystopian, dramas, fantasy, comedy);
        animateSearchView();

        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        booksViewModel.getBooks().observe(this, books -> {
            allBooks = books;
            allRows = buildGenreRows(books, GENRES);
            genreRowAdapter.setGenreRows(allRows);

            for (Book book : books) {
                String uri = book.getCoverUri();
                if (uri != null && !uri.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(Uri.parse(uri))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload();
                }
            }
        });

        searchDropdownCard = findViewById(R.id.searchDropdownCard);
        RecyclerView dropdownRecycler = findViewById(R.id.searchDropdownRecycler);
        dropdownRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchDropdownAdapter = new SearchDropdownAdapter();
        dropdownRecycler.setAdapter(searchDropdownAdapter);

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



    private void animateCategoryButtons(Button... buttons) {
        long delay = 100L;

        for (int i = 0; i < buttons.length; i++) {
            Button btn = buttons[i];


            btn.setAlpha(0f);
            btn.setTranslationX(-200f);

            ObjectAnimator slideIn = ObjectAnimator.ofFloat(btn, "translationX", -200f, 0f);
            ObjectAnimator fadeIn  = ObjectAnimator.ofFloat(btn, "alpha", 0f, 1f);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(slideIn, fadeIn);
            set.setDuration(400);
            set.setStartDelay(i * delay);
            set.setInterpolator(new OvershootInterpolator(1.5f));
            set.start();
        }
    }

    private void animateSearchView() {
        android.widget.SearchView searchView = findViewById(R.id.searchView);


        searchView.setAlpha(0f);
        searchView.setTranslationY(-30f);
        searchView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();


        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            float targetScale = hasFocus ? 1.03f : 1.0f;
            view.animate().scaleX(targetScale).setDuration(200).start();


            if (!hasFocus && searchView.getQuery().toString().isEmpty()) {
                showGenreRows();
            }
        });

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showGenreRows();
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });
    }

    private void performSearch(String query) {
        if (currentSearchLiveData != null && searchObserver != null) {
            currentSearchLiveData.removeObserver(searchObserver);
        }

        searchDropdownCard.setVisibility(View.VISIBLE);

        currentSearchLiveData = booksViewModel.searchBooks(query);
        searchObserver = books -> searchDropdownAdapter.setBooks(books);
        currentSearchLiveData.observe(this, searchObserver);
    }

    private void showGenreRows() {
        if (currentSearchLiveData != null && searchObserver != null) {
            currentSearchLiveData.removeObserver(searchObserver);
            currentSearchLiveData = null;
            searchObserver = null;
        }
        searchDropdownCard.setVisibility(View.GONE);
        genreRowAdapter.setGenreRows(allRows);
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