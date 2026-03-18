package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserBookRep {
    private UserBookDao userBookDao;
    private LiveData<List<UserBook>> userBooks;

    ExecutorService executors = Executors.newSingleThreadExecutor();

    public UserBookRep(Application application){
        AppDatabase Adb = AppDatabase.getInstance(application);
        userBookDao = Adb.userBookDao();
        userBooks = userBookDao.getAllUserBooks();
    }


    public void insert(UserBook userBook){
        executors.execute(new Runnable() {
            @Override
            public void run() {userBookDao.insert(userBook);}
        });
    }


    public void update(UserBook userBook){
        executors.execute(new Runnable() {
            @Override
            public void run() {userBookDao.update(userBook);}
        });
    }


    public void delete(UserBook userBook){
        executors.execute(new Runnable() {
            @Override
            public void run() {userBookDao.delete(userBook);}
        });
    }


    public LiveData<List<UserBook>> getUserBooks() {return userBooks;}
}
