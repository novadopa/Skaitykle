package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.UserViewModel;
import com.example.skaitykle.DataBase.BookViewAdapter;
import com.example.skaitykle.DataBase.BooksViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class Title extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private BooksViewModel booksViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_title);

        Button genres = findViewById(R.id.button_Genres);
        genres.setOnClickListener(view -> {
            Intent intent = new Intent(Title.this, Genres.class);
            startActivity(intent);
        });

        //Book Layout
        LinearLayout booksContainer = findViewById(R.id.LineOfBooks);

        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        booksViewModel.getBooks().observe(this, new Observer<List<Book>>() {

            @Override
            public void onChanged(List<Book> books) {
                booksContainer.removeAllViews();

                for (Book book : books) {
                    View bookView = LayoutInflater.from(Title.this)
                            .inflate(R.layout.books_for_title, booksContainer, false);

                    TextView title = bookView.findViewById(R.id.textViewBookTitle);
                    TextView author = bookView.findViewById(R.id.textViewAuthor);

                    title.setText(book.getTitle());
                    author.setText(book.getAuthor());

                    bookView.setOnClickListener(view -> {
                        Intent intent = new Intent(Title.this, BookDetails.class);
                        intent.putExtra("BookId", book.getBid());
                        intent.putExtra("BookTitle", book.getTitle());
                        intent.putExtra("BookAuthor", book.getAuthor());
                        intent.putExtra("BookDescription", book.getDescription());
                        startActivity(intent);
                    });


                    booksContainer.addView(bookView);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_nav_title);

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
    }
}