package com.example.skaitykle.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface BookDao {
    @Query("SELECT * FROM book ORDER BY bid ASC")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM book WHERE bid IN (:bookIds)")
    List<Book> loadAllIds(int[] bookIds);

    @Query("SELECT * FROM book WHERE title LIKE :title OR author LIKE :author LIMIT 1")
    Book findByTitleOrAuthor(String title, String author);

    @Insert
    void insertAll(Book... books);

    @Insert
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);


    @Query("SELECT b.*, ub.ubId, ub.user_id, ub.book_id, ub.read_pages, ub.last_read_page " +
            "FROM Book b INNER JOIN UserBook ub ON b.bid = ub.book_id AND ub.user_id = :userId")
    LiveData<List<BookWithReadingProgress>> getBooksWithReadingProgress(int userId);

    @Query("UPDATE Book SET total_pages = :totalPages WHERE bid = :bookId")
    void updateTotalPages(int bookId, int totalPages);

    @Query("SELECT * FROM book WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY title ASC")
    LiveData<List<Book>> searchBooks(String query);
}
