package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRep {
    private UserDao userDao;
    private LiveData<List<User>> users;

    ExecutorService executors = Executors.newSingleThreadExecutor();

    public UserRep(Application application) {
        AppDatabase Adb = AppDatabase.getInstance(application);
        userDao = Adb.userDao();
        users   = userDao.getAllUsers();
    }

    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.delete(user));
    }

    public void setBanned(int userId, boolean banned) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.setBanned(userId, banned));
    }

    public LiveData<List<User>> getUsers()                { return users; }

    public LiveData<List<User>> getNonAdminUsers()        { return userDao.getNonAdminUsers(); }

    public User getUserByIdDirect(int userId) {
        return userDao.getUserByIdDirect(userId);
    }
}