package com.example.skaitykle.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserBookDao {

    @Query("SELECT * FROM UserBook ORDER BY ubId ASC")
    LiveData<List<UserBook>> getAllUserBooks();

    @Query("DELETE FROM UserBook WHERE user_id=:userId")
    void deleteAllBooks(int userId);

    @Query("SELECT * FROM UserBook WHERE user_id = :userId AND book_id = :bookId")
    UserBook getBookByUserAndBook(int userId, int bookId);

    @Query("DELETE FROM UserBook WHERE user_id = :userId AND book_id = :bookId")
    void deleteBookByUserAndBook(int userId, int bookId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserBook userBook);

    @Update
    void update(UserBook userBook);

    @Delete
    void delete(UserBook userBook);
}
