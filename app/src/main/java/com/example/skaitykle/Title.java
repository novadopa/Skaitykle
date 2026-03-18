package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.UserViewModel;
import com.example.skaitykle.DataBase.BookViewAdapter;
import com.example.skaitykle.DataBase.BooksViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class Title extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private UserViewModel userViewModel;
    private BooksViewModel booksViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_title);

        RecyclerView recyclerView = findViewById(R.id.RecyclerViewTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BookViewAdapter bAdapater = new BookViewAdapter();
        //UserViewAdapter adapter = new UserViewAdapter();
        recyclerView.setAdapter(bAdapater);
        booksViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(BooksViewModel.class);
       // userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        /*userViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {

                adapter.setUsers(users);
            }
        });*/
        booksViewModel.getBooks().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {

                bAdapater.setBooks(books);
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