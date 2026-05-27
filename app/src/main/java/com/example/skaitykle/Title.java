package com.example.skaitykle;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import java.util.List;

public class Title extends AppCompatActivity {

    private Observer<List<Book>> searchObserver;
    private LiveData<List<Book>> currentSearchLiveData;

    private BottomNavigationView bottomNavigationView;
    private BooksViewModel booksViewModel;

    private List<Book> allBooks = new ArrayList<>();
    private List<GenreRow> allRows = new ArrayList<>();

    private GenreRowAdapter genreRowAdapter;

    private View activeGenreButton = null;
    private LinearLayout genreButtonContainer;

    private SearchDropdownAdapter searchDropdownAdapter;
    private CardView searchDropdownCard;

    // ── Gyroscope parallax ────────────────────────────────────────────────────
    private SensorManager sensorManager;
    private Sensor rotationSensor;

    private float parallaxX = 0f;

    // Max pixel offset at full tilt — keep subtle
    private static final float PARALLAX_MAX    = 45f;  // max degrees of rotation
    private static final float PARALLAX_SMOOTH = 0.12f;
    private static final float TILT_RANGE_DEG  = 25f;

    private final SensorEventListener gyroListener = new SensorEventListener() {

        private final float[] rotMatrix   = new float[9];
        private final float[] orientation = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event == null || event.values == null) return;

            // Convert rotation vector → rotation matrix → euler angles
            SensorManager.getRotationMatrixFromVector(rotMatrix, event.values);
            SensorManager.getOrientation(rotMatrix, orientation);

            float roll = (float) Math.toDegrees(orientation[2]);
            float normX = Math.max(-1f, Math.min(1f, roll / TILT_RANGE_DEG));
            float targetX = normX * PARALLAX_MAX;
            parallaxX += (targetX - parallaxX) * PARALLAX_SMOOTH;

            applyParallax(parallaxX, 0f);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        setupRecyclerView();
        setupSearchDropdown();
        setupSensors();
        setupViewModel();
        setupBottomNavigation();
        animateSearchView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.RecyclerViewTitleBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        genreRowAdapter = new GenreRowAdapter();
        recyclerView.setAdapter(genreRowAdapter);
        genreButtonContainer = findViewById(R.id.genreButtonContainer);
    }

    private void setupSearchDropdown() {
        searchDropdownCard = findViewById(R.id.searchDropdownCard);
        RecyclerView dropdownRecycler = findViewById(R.id.searchDropdownRecycler);
        dropdownRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchDropdownAdapter = new SearchDropdownAdapter();
        dropdownRecycler.setAdapter(searchDropdownAdapter);
    }

    private void setupSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(
                    Sensor.TYPE_ROTATION_VECTOR
            );
        }
    }

    private void setupViewModel() {
        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        booksViewModel.getBooks().observe(this, books -> {
            if (books == null) return;
            allBooks = books;
            allRows = buildGenreRows(books);
            genreRowAdapter.setGenreRows(allRows);
            buildGenreButtons(books);
            preloadImages(books);
        });
    }

    private void preloadImages(List<Book> books) {
        for (Book book : books) {
            if (book == null) continue;
            String uri = book.getCoverUri();
            if (uri != null && !uri.isEmpty()) {
                Glide.with(getApplicationContext())
                        .load(Uri.parse(uri))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload();
            }
        }
    }

    private void setupBottomNavigation() {
        int userId = getIntent().getIntExtra("userId", -1);
        bottomNavigationView = findViewById(R.id.bottom_nav_title);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.menu_home) return true;
                        if (id == R.id.menu_add_book) {
                            startActivity(new Intent(Title.this, AddBook.class));
                            return true;
                        }
                        if (id == R.id.menu_library) {
                            startActivity(new Intent(Title.this, Library.class));
                            return true;
                        }
                        if (id == R.id.menu_map) {
                            startActivity(new Intent(Title.this, Map.class));
                            return true;
                        }
                        if (id == R.id.menu_profile) {
                            Intent profileIntent = new Intent(Title.this, Profile.class);
                            profileIntent.putExtra("userId", userId);
                            startActivity(profileIntent);
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && rotationSensor != null) {
            sensorManager.registerListener(
                    gyroListener,
                    rotationSensor,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(gyroListener);
        }
        // Reset covers to centre so they don't freeze mid-tilt
        applyParallax(0f, 0f);
    }

    // ── Parallax helpers ──────────────────────────────────────────────────────

    /**
     * Walks the outer RecyclerView → inner RecyclerViews → cover ImageViews
     * and applies the tilt-based translation directly to each visible cover.
     * No adapter changes needed.
     */
    private void applyParallax(float tx, float ty) {
        RecyclerView outerRv = findViewById(R.id.RecyclerViewTitleBooks);
        if (outerRv == null) return;

        for (int i = 0; i < outerRv.getChildCount(); i++) {
            View rowView = outerRv.getChildAt(i);
            if (!(rowView instanceof ViewGroup)) continue;

            RecyclerView innerRv = findInnerRecyclerView((ViewGroup) rowView);
            if (innerRv == null) continue;

            for (int j = 0; j < innerRv.getChildCount(); j++) {
                View bookView = innerRv.getChildAt(j);
                if (bookView == null) continue;

                ImageView cover = bookView.findViewById(R.id.imageViewCover);
                if (cover != null) {
                    cover.setRotationY(tx);
                }
            }
        }
    }

    /** Recursively finds the first RecyclerView inside a ViewGroup. */
    private RecyclerView findInnerRecyclerView(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof RecyclerView) return (RecyclerView) child;
            if (child instanceof ViewGroup) {
                RecyclerView found = findInnerRecyclerView((ViewGroup) child);
                if (found != null) return found;
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────

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
            view.animate().scaleX(targetScale).scaleY(targetScale).setDuration(200).start();
            if (!hasFocus && searchView.getQuery().toString().isEmpty()) showGenreRows();
        });

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                performSearch(query); return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) showGenreRows(); else performSearch(newText);
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

    private List<String> extractGenresFromBooks(List<Book> books) {
        List<String> genres = new ArrayList<>();
        for (Book book : books) {
            if (book == null || book.getGenres() == null) continue;
            for (String genre : book.getGenres()) {
                if (genre == null) continue;
                String trimmed = genre.trim();
                if (!trimmed.isEmpty() && !genres.contains(trimmed)) genres.add(trimmed);
            }
        }
        return genres;
    }

    private List<GenreRow> buildGenreRows(List<Book> books) {
        List<String> genres = extractGenresFromBooks(books);
        List<GenreRow> rows = new ArrayList<>();
        for (String genre : genres) {
            List<Book> filtered = new ArrayList<>();
            for (Book book : books) {
                if (book == null || book.getGenres() == null) continue;
                for (String bookGenre : book.getGenres()) {
                    if (bookGenre != null && bookGenre.trim().equals(genre)) {
                        filtered.add(book); break;
                    }
                }
            }
            if (!filtered.isEmpty()) rows.add(new GenreRow(genre, filtered));
        }
        return rows;
    }

    private void filterByGenre(String genre) {
        List<GenreRow> filtered = new ArrayList<>();
        for (GenreRow row : allRows) {
            if (row.genreName.equals(genre)) filtered.add(row);
        }
        genreRowAdapter.setGenreRows(filtered);
    }

    private Button createGenreButton(String label) {
        Button btn = new Button(this);
        btn.setText(label);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMarginEnd(20);
        btn.setBackgroundResource(R.drawable.genre_button_icon);
        btn.setTextColor(Color.WHITE);
        btn.setLayoutParams(params);
        return btn;
    }

    private void buildGenreButtons(List<Book> books) {
        List<String> usedGenres = extractGenresFromBooks(books);
        genreButtonContainer.removeAllViews();
        List<View> createdButtons = new ArrayList<>();

        View allBtn = createGenreButton("All");
        allBtn.setOnClickListener(v -> { setActiveButton(allBtn); genreRowAdapter.setGenreRows(allRows); });
        genreButtonContainer.addView(allBtn);
        createdButtons.add(allBtn);

        for (String genre : usedGenres) {
            View btn = createGenreButton(genre);
            btn.setOnClickListener(v -> { setActiveButton(btn); filterByGenre(genre); });
            genreButtonContainer.addView(btn);
            createdButtons.add(btn);
        }

        setActiveButton(allBtn);
        animateCategoryButtons(createdButtons);
    }

    private void setActiveButton(View selected) {
        if (activeGenreButton != null) activeGenreButton.setAlpha(0.6f);
        selected.setAlpha(1.0f);
        activeGenreButton = selected;
    }

    private void animateCategoryButtons(List<View> buttons) {
        long delay = 200L;
        for (int i = 0; i < buttons.size(); i++) {
            View btn = buttons.get(i);
            btn.setAlpha(0f);
            btn.setTranslationX(-200f);
            ObjectAnimator slideIn = ObjectAnimator.ofFloat(btn, "translationX", -200f, 0f);
            ObjectAnimator fadeIn  = ObjectAnimator.ofFloat(btn, "alpha", 0f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(slideIn, fadeIn);
            set.setDuration(1000);
            set.setStartDelay(i * delay);
            set.setInterpolator(new OvershootInterpolator(1.5f));
            set.start();
        }
    }
}