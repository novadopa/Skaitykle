package com.example.skaitykle.DataBase;

import androidx.room.Embedded;
import androidx.room.Relation;

public class BookWithReadingProgress {
    @Embedded
    public Book book;

    @Relation(parentColumn = "bid", entityColumn = "book_id")
    public UserBook userBook;

    public int getReadPages(){
        if(userBook == null){
            return 0;
        }
        return userBook.getLastReadPage();
    }
}
