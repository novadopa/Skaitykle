package com.example.skaitykle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.BookWithReadingProgress;
import com.example.skaitykle.DataBase.BooksViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Library extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    LibraryBookAdapter libraryBookAdapter;
    Chip authorChip;
    Chip genresChip;

    LinearLayout searchOptionsLayout;
    CheckBox checkTitle;
    CheckBox checkAuthor;

    BooksViewModel booksViewModel;

    List<BookWithReadingProgress> currentBooks = new ArrayList<>();

    private static final int currentUserId = 1;


    private final ActivityResultLauncher<Intent> readerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new androidx.activity.result.ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                        }
                    });

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

        bottomNavigationView = findViewById(R.id.libraryBottomNav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if(id == R.id.menu_home){
                    Intent homeIntent = new Intent(getBaseContext(), Title.class);
                    homeIntent.addFlags(homeIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    return true;
                }
                else if (id == R.id.menu_library){
                    Intent libraryIntent = new Intent(getBaseContext(), Library.class);
                    libraryIntent.addFlags(libraryIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(libraryIntent);
                    return true;
                }
                else if(id == R.id.menu_profile){
                    Intent profileIntent = new Intent(getBaseContext(), Profile.class);
                    profileIntent.addFlags(profileIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profileIntent);
                    return true;
                }

                return false;
            }
        });

        toolbar = findViewById(R.id.libraryToolbar);
        setSupportActionBar(toolbar);

        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        libraryBookAdapter = new LibraryBookAdapter(new ArrayList<>(), new LibraryBookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(BookWithReadingProgress bookItem) {
                Intent bookReaderIntent = new Intent(Library.this, BookReader.class);
                bookReaderIntent.putExtra("BookTitle", bookItem.book.getTitle());
                bookReaderIntent.putExtra("BookAuthor", bookItem.book.getAuthor());
                bookReaderIntent.putExtra("BookDescription", bookItem.book.getDescription());
                bookReaderIntent.putExtra("BookPath", bookItem.book.getBookPath());
                bookReaderIntent.putExtra("BookTotalPages", bookItem.book.getTotalPages());
                bookReaderIntent.putExtra("BookCover", bookItem.book.getCoverUri());
                bookReaderIntent.putExtra("BookPagesRead", bookItem.getReadPages());
                bookReaderIntent.putExtra("BookId",     bookItem.book.getBid());
                bookReaderIntent.putExtra("UserId", currentUserId);

                if (bookItem.userBook != null) {
                    bookReaderIntent.putExtra("UserBookId", bookItem.userBook.getUserBookId());
                } else {
                    bookReaderIntent.putExtra("UserBookId", -1);
                }
                readerLauncher.launch(bookReaderIntent);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.libraryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(libraryBookAdapter);

        booksViewModel.getBooksWithReadingProgress(currentUserId).observe(this,
                new Observer<List<BookWithReadingProgress>>() {
            @Override
            public void onChanged(List<BookWithReadingProgress> books) {
                if(books != null){
                    currentBooks.clear();
                    if(books != null){
                        currentBooks.addAll(books);
                    }
                }
                libraryBookAdapter.setBooks(currentBooks);
            }
        });


        searchOptionsLayout = findViewById(R.id.searchOptionsLayout);
        checkTitle = findViewById(R.id.checkTitle);
        checkAuthor = findViewById(R.id.checkAuthor);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean titleChecked = checkTitle.isChecked();
                boolean authorChecked = checkAuthor.isChecked();

                if(titleChecked && authorChecked){
                    libraryBookAdapter.setSearchMode("All");
                }
                else if(titleChecked){
                    libraryBookAdapter.setSearchMode("Title");
                }
                else if(authorChecked){
                    libraryBookAdapter.setSearchMode("Author");
                }
                else{
                    libraryBookAdapter.setSearchMode("All");
                }
            }
        };

        checkTitle.setOnClickListener(listener);
        checkAuthor.setOnClickListener(listener);

        authorChip = findViewById(R.id.authorFilterChip);
        authorChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Library.this, v);

                List<String> authors = getAuthors(currentBooks);
                for(String author : authors){
                    popup.getMenu().add(author);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selected = item.getTitle().toString();

                        libraryBookAdapter.FilterAuthor(selected);

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

                List<String> genres = getGenres(currentBooks);
                for(String genre : genres){
                    popup.getMenu().add(genre);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selected = item.getTitle().toString();

                        libraryBookAdapter.FilterGenre(selected);

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

        View searchIcon = toolbar.findViewById(R.id.search);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                toolbar.post(() -> {
                    for (int i = 0; i < toolbar.getChildCount(); i++) {
                        View child = toolbar.getChildAt(i);
                        if (child instanceof android.widget.ImageButton ||
                                child instanceof androidx.appcompat.widget.AppCompatImageButton) {
                            child.setScaleX(0f);
                            child.setScaleY(0f);
                            child.setAlpha(0f);
                            child.animate().scaleX(1f).scaleY(1f).alpha(1f)
                                    .setDuration(220).setStartDelay(80).start();
                            break;
                        }
                    }
                });

                if(searchIcon != null){
                    searchIcon.animate().scaleX(0f).scaleY(0f).alpha(0f).
                            setDuration(200).start();
                }

                if (searchView != null) {
                    searchView.setScaleX(0.8f);
                    searchView.setAlpha(0f);
                    searchView.animate().scaleX(1f).scaleY(1f).alpha(1f).
                            setDuration(250).start();
                }


                showSearchOptions();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                for (int i = 0; i < toolbar.getChildCount(); i++) {
                    View child = toolbar.getChildAt(i);
                    if (child instanceof android.widget.ImageButton ||
                            child instanceof androidx.appcompat.widget.AppCompatImageButton) {
                        child.animate()
                                .scaleX(0f).scaleY(0f)
                                .alpha(0f)
                                .setDuration(160)
                                .start();
                        break;
                    }
                }

                if(searchIcon != null){
                    searchIcon.setScaleX(0f);
                    searchIcon.setScaleY(0f);
                    searchIcon.setAlpha(0f);
                    searchIcon.animate().scaleX(1f).scaleY(1f).alpha(1f).
                            setDuration(200).start();
                }


                if (searchView != null) {
                    searchView.setScaleX(0f);
                    searchView.setAlpha(0f);
                    searchView.animate().scaleX(0f).scaleY(0f).alpha(0f).
                            setDuration(250).start();
                }


                hideSearchOptions();
                libraryBookAdapter.SearchBooks("");
                return true;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                libraryBookAdapter.SearchBooks(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                libraryBookAdapter.SearchBooks(newText.toLowerCase());
                return true;
            }
        });


        MenuItem deleteBooksItem = menu.findItem(R.id.deleteAllBooks);

        deleteBooksItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AlertDialog.Builder(Library.this, R.style.AnimatedDialog).setMessage("Are you sure you want to delete " +
                        "all your books?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppDatabase db = AppDatabase.getInstance(Library.this);
                        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.userBookDao().deleteAllBooks(currentUserId);
                            }
                        });
                        libraryBookAdapter.FilterAuthor("All");
                        libraryBookAdapter.FilterGenre("All");
                        authorChip.setText("Authors");
                        genresChip.setText("Genres");
                    }
                }).setNegativeButton("No", null).show();


                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void showSearchOptions(){
        searchOptionsLayout.setVisibility(View.VISIBLE);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.checkboxes_slide_down);
        searchOptionsLayout.startAnimation(slideDown);
    }


    private void hideSearchOptions(){
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.checkboxes_slide_up);

        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                searchOptionsLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });
        searchOptionsLayout.startAnimation(slideUp);
    }


    private List<String> getAuthors(List<BookWithReadingProgress> books){
        Set<String> authors = new HashSet<>();

        for(BookWithReadingProgress bookItem : books){
            authors.add(bookItem.book.getAuthor());
        }

        List<String> authorList = new ArrayList<>(authors);
        Collections.sort(authorList);

        authorList.add(0, "All");
        return authorList;
    }


    private List<String> getGenres(List<BookWithReadingProgress> books){
        Set<String> genres = new HashSet<>();

        for(BookWithReadingProgress bookItem : books){
            genres.addAll(bookItem.book.getGenres());
        }

        List<String> genreList = new ArrayList<>(genres);
        Collections.sort(genreList);

        genreList.add(0, "All");
        return genreList;
    }
}