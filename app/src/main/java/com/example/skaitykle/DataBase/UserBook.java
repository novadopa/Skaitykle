package com.example.skaitykle.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jspecify.annotations.NonNull;

@Entity(tableName = "UserBook", indices = {@Index(value = {"user_id", "book_id"}, unique = true)})
public class UserBook {
    @PrimaryKey(autoGenerate = true)
    public int ubId;

    @NonNull
    @ColumnInfo(name = "user_id")
    public int userId;

    @NonNull
    @ColumnInfo(name = "book_id")
    public int bookId;

    @NonNull
    @ColumnInfo(name = "read_pages")
    public int readPages;

    @NonNull
    @ColumnInfo(name = "last_read_page")
    public int lastReadPage;


    public UserBook(@NonNull int userId, @NonNull int bookId, @NonNull int readPages,
                    @NonNull int lastReadPage){
        this.userId = userId;
        this.bookId = bookId;
        this.readPages = readPages;
        this.lastReadPage = lastReadPage;
    }

    public void setUserBookId(int ubId){this.ubId=ubId;}
    public int getUserBookId(){return ubId;}
    public int getUserId(){return userId;}
    public int getBookId() {return bookId;}
    public int getReadPages(){return readPages;}
    public int getLastReadPage(){return lastReadPage;}
}
