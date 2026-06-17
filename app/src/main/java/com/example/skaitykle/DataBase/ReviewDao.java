package com.example.skaitykle.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Review review);

    @Query("SELECT * FROM Review WHERE book_id = :bookId")
    LiveData<List<Review>> getReviewsForBook(int bookId);

    @Query("SELECT * FROM Review WHERE book_id = :bookId AND user_id = :userId LIMIT 1")
    Review getReviewByUserAndBook(int bookId, int userId);

    @Query("DELETE FROM Review WHERE book_id = :bookId")
    void deleteAllForBook(int bookId);


    @Query("SELECT Review.*, User.first_name || ' ' || User.last_name AS userName " +
            "FROM Review " +
            "LEFT JOIN User ON Review.user_id = User.uid " +
            "WHERE Review.book_id = :bookId")
    LiveData<List<ReviewWithUsername>> getReviewsWithUserNames(int bookId);
}