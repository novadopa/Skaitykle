package com.example.skaitykle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.User;
import com.example.skaitykle.DataBase.UserBook;

public class BookDetails extends AppCompatActivity {
    private int bookId;
    private int totalPages;
    String title;
    String author;
    String description;
    private static final int currentUserId = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_details);

        bookId = getIntent().getIntExtra("BookId", -1);
        title = getIntent().getStringExtra("BookTitle");
        author = getIntent().getStringExtra("BookAuthor");
        description = getIntent().getStringExtra("BookDescription");
        totalPages = getIntent().getIntExtra("BookTotalPages", 0);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewAuthor = findViewById(R.id.textViewBookAuthor);
        TextView textViewDescription = findViewById(R.id.textViewDescription);

        textViewTitle.setText(title);
        textViewAuthor.setText(author);
        textViewDescription.setText(description);

        Button readButton = (Button) findViewById(R.id.button_ReadBook);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToLibrary();
            }
        });

        ImageButton imageButton = findViewById(R.id.button_Back);
        imageButton.setOnClickListener(view -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void addBookToLibrary()
    {
        AppDatabase db = AppDatabase.getInstance(this);
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                UserBook existingBook = db.userBookDao().
                        getBookByUserAndBook(currentUserId, bookId);

                UserBook userBook;
                if(existingBook != null){
                    userBook = existingBook;
                }else{
                    UserBook newUserBook = new UserBook(currentUserId, bookId, 0, 0);
                    long newId = db.userBookDao().insert(newUserBook);
                    newUserBook.ubId = (int) newId;
                    userBook = newUserBook;
                }

                int userBookId = userBook.ubId;
                int readPages = userBook.readPages;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent addBookIntent = new Intent(getBaseContext(), Library.class);
                        addBookIntent.putExtra("BookTitle", title);
                        addBookIntent.putExtra("BookAuthor", author);
                        addBookIntent.putExtra("BookDescription", description);
                        addBookIntent.putExtra("BookTotalPages", totalPages);
                        addBookIntent.putExtra("BookCover", getIntent().getStringExtra("BookCover"));
                        addBookIntent.putExtra("BookPagesRead", readPages);
                        addBookIntent.putExtra("BookId", bookId);
                        addBookIntent.putExtra("UserId", currentUserId);
                        addBookIntent.putExtra("UserBookId", userBookId);
                        startActivity(addBookIntent);
                    }
                });
            }
        });
    }
}