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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import androidx.appcompat.widget.SearchView;

public class Library extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

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

        toolbar = findViewById(R.id.library_toolbar);

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