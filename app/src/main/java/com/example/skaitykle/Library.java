package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Library extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    BookAdapter bookAdapter;
    Chip authorChip;
    Chip genresChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.library_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.library_bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if(id == R.id.menu_home){
                    Intent homeIntent = new Intent(getBaseContext(), Title.class);
                    startActivity(homeIntent);
                    return true;
                }
                else if (id == R.id.menu_library){
                    Intent libraryIntent = new Intent(getBaseContext(), Library.class);
                    startActivity(libraryIntent);
                    return true;
                }
                else if(id == R.id.menu_profile){
                    Intent profileIntent = new Intent(getBaseContext(), Profile.class);
                    startActivity(profileIntent);
                    return true;
                }

                return false;
            }
        });

        toolbar = findViewById(R.id.library_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.library_recycler);
        List<BookEntity> books = new ArrayList<>();
        List<String> genres1 = Arrays.asList("Horror");
        List<String> genres2 = Arrays.asList("Fantasy");
        books.add(new BookEntity("Book One", "Author A", R.drawable.profile,
                200, genres1));
        books.add(new BookEntity("Book tuah", "Author b", R.drawable.profile,
                400, genres2));

        bookAdapter = new BookAdapter(books);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        authorChip = findViewById(R.id.authorFilterChip);
        authorChip.setOnClickListener(v->{
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("All");
            popup.getMenu().add("Author A");
            popup.getMenu().add("Author b");

            popup.setOnMenuItemClickListener(item->{
                //bookAdapter.FilterAuthor(item.getTitle().toString());
                String selected = item.getTitle().toString();

                bookAdapter.FilterAuthor(selected);
                //authorChip.setText(item.getTitle());

                if(selected.equals("All"))
                {
                    authorChip.setText("Authors");
                }else{
                    authorChip.setText(selected);
                }

                return true;
            });

            popup.show();
        });

        genresChip = findViewById(R.id.genreFilterChip);
        genresChip.setOnClickListener(v->{
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("All");
            popup.getMenu().add("Horror");
            popup.getMenu().add("Fantasy");

            popup.setOnMenuItemClickListener(item->{
                bookAdapter.FilterGenre(item.getTitle().toString());
                genresChip.setText(item.getTitle());
                return true;
            });
            popup.show();
        });


        /*Spinner authorSpinner = findViewById(R.id.author_spinner);
        List<String> authors = Arrays.asList("All", "Author A", "Author B");
        //Set<String> auth = new HashSet<>();

        ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(this, android.R.layout.
                simple_spinner_dropdown_item, authors);

        authorSpinner.setAdapter(authorAdapter);

        authorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAuthor = parent.getItemAtPosition(position).toString();
                adapter.FilterAuthor(selectedAuthor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        /*Spinner genreSpinner = findViewById(R.id.genre_spinner);
        List<String> genres = Arrays.asList("All", "Fantasy", "Science-fiction", "Horror");

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genres);

        genreSpinner.setAdapter(genreAdapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = parent.getItemAtPosition(position).toString();
                adapter.FilterGenre(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.actionbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookAdapter.SearchBooks(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookAdapter.SearchBooks(newText.toLowerCase());
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}