package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class BookRep {
    private BookDao bookDao;
    private LiveData<List<Book>> books;

    public BookRep(Application application) {
        AppDatabase Adb = AppDatabase.getInstance(application);
        bookDao = Adb.bookDao();
        books   = bookDao.getAllBooks();
    }

    public void insert(Book book) {
        AppDatabase.databaseWriteExecutor.execute(() -> bookDao.insert(book));
    }

    public void update(Book book) {
        AppDatabase.databaseWriteExecutor.execute(() -> bookDao.update(book));
    }

    public void delete(Book book) {
        AppDatabase.databaseWriteExecutor.execute(() -> bookDao.delete(book));
    }

    public void updateStatus(int bookId, String status) {
        AppDatabase.databaseWriteExecutor.execute(() -> bookDao.updateStatus(bookId, status));
    }

    public LiveData<List<Book>> getBooks()              { return books; }

    public LiveData<List<Book>> getPendingBooks()       { return bookDao.getPendingBooks(); }

    public LiveData<List<Book>> getPersonalBooks(int userId) {
        return bookDao.getPersonalBooks(userId);
    }

    public LiveData<List<Book>> searchBooks(String query) {
        return bookDao.searchBooks(query);
    }

    public LiveData<List<BookWithReadingProgress>> getBooksWithReadingProgress(int userId) {
        return bookDao.getBooksWithReadingProgress(userId);
    }

    public void updateTotalPages(int bookId, int totalPages) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                bookDao.updateTotalPages(bookId, totalPages));
    }
}