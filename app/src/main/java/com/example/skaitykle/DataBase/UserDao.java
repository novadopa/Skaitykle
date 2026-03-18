package com.example.skaitykle.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface UserDao {
    @Query("SELECT * FROM user ORDER BY uid ASC")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user WHERE uid = :userId LIMIT 1")
    User getUserByIdDirect(int userId);

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Insert
    void insertAll(User... users);

    @Insert
    void insert(User user);
    @Update
    void update(User users);

    @Delete
    void delete(User user);

}
