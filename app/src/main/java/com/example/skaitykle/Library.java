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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Library extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    BookAdapter bookAdapter;
    Chip authorChip;
    Chip genresChip;
    BookEntity bookInProgress;

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
                200, 100, genres1));
        books.add(new BookEntity("Book tuah", "Author b", R.drawable.profile,
                400, 50, genres2));

        bookAdapter = new BookAdapter(books, new BookAdapter.OnBookClickListener(){
            @Override
            public void onBookClick(BookEntity book){
                bookInProgress = book;

                Intent bookReaderIntent = new Intent(Library.this, BookReader.class);
                bookReaderIntent.putExtra("title", book.getBookTitle());
                bookReaderIntent.putExtra("author", book.getBookAuthor());
                bookReaderIntent.putExtra("totalPages", book.getTotalBookPages());
                bookReaderIntent.putExtra("pagesRead", book.getPagesRead());

                startActivityForResult(bookReaderIntent, 1);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);


        authorChip = findViewById(R.id.authorFilterChip);
        authorChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Library.this, v);

                List<String> authors = getAuthors(books);
                for(String author : authors){
                    popup.getMenu().add(author);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selected = item.getTitle().toString();

                        bookAdapter.FilterAuthor(selected);

                        if(selected.equals("All"))
                        {
                            authorChip.setText("Authors");
                        }else{
                            authorChip.setText(selected);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        genresChip = findViewById(R.id.genreFilterChip);
        genresChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Library.this, v);
                popup.getMenu().add("All");
                popup.getMenu().add("Horror");
                popup.getMenu().add("Fantasy");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selected = item.getTitle().toString();

                        bookAdapter.FilterGenre(selected);

                        if (selected.equals("All")) {
                            genresChip.setText("Genres");
                        } else {
                            genresChip.setText(selected);
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
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


    private List<String> getAuthors(List<BookEntity> books){
        Set<String> authors = new HashSet<>();

        for(BookEntity book : books){
            authors.add(book.getBookAuthor());
        }

        List<String> authorList = new ArrayList<>(authors);
        Collections.sort(authorList);

        authorList.add(0, "All");
        return authorList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            int updatePagesRead = data.getIntExtra("updatePagesRead", 0);

            if(bookAdapter != null){
                bookInProgress.setPagesRead(updatePagesRead);
            }
            bookAdapter.notifyDataSetChanged();
        }
    }
}