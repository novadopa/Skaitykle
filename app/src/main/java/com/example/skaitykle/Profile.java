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

import com.example.skaitykle.DataBase.User;
import com.example.skaitykle.DataBase.UserRep;
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
import android.widget.TextView;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView userInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookReaderMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        bottomNavigationView = findViewById(R.id.bottom_nav_title);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        TextView nameText = findViewById(R.id.nameText);
        TextView surnameText = findViewById(R.id.surnameText);
        TextView emailText = findViewById(R.id.emailText);

        UserRep userRep = new UserRep(getApplication());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User user = userRep.getUserByIdDirect(1);

            runOnUiThread(() -> {
                if (user != null) {
                    nameText.setText(user.firstName);
                    surnameText.setText(user.lastName);
                    emailText.setText(user.email);
                }
            });
        });


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