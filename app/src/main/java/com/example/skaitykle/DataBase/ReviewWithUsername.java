package com.example.skaitykle.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class ReviewWithUsername {
    @Embedded
    public Review review;

    @ColumnInfo(name = "userName")
    public String userName;
}
