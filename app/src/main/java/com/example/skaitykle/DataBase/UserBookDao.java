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

    @Query("SELECT * FROM UserBook WHERE ubId IN (:userBookIds)")
    List<UserBook> loadAllIds(int[] userBookIds);

    @Insert
    void insertAll(UserBook... userBook);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserBook userBook);

    @Update
    void update(UserBook userBook);

    @Delete
    void delete(UserBook userBook);
}
