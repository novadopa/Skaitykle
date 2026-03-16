package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class BooksViewModel extends AndroidViewModel {

    private BookRep bookRep;
    private LiveData<List<Book>> books;
    public BooksViewModel(@NonNull Application application) {
        super(application);

        bookRep = new BookRep(application);
        books = bookRep.getBooks();
    }

    public void insert(Book book) { bookRep.insert(book);}

    public void update(Book book) { bookRep.update(book);}

    public void delete(Book book) { bookRep.delete(book);}

    public LiveData<List<Book>> getBooks() { return books;}
}
