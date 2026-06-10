package com.example.skaitykle.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Review")
public class Review {
    @PrimaryKey(autoGenerate = true)
    public int reviewId;

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "rating")
    public float rating;

    @ColumnInfo(name = "comment")
    public String comment;

    public Review(int bookId, int userId, float rating, String comment) {
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public int getUserId() { return userId; }
    public int getBookId() { return bookId; }
}
