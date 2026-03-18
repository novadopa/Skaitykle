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

    public UserRep(Application application){
        AppDatabase Adb = AppDatabase.getInstance(application);
        //userDao = Adb.userDao();
        users = userDao.getAllUsers();
    }

    public void insert(User user) {
        //new InsertUserAsyncTask(userDao).execute(user);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                userDao.insert(user);
            }
        });
    }
    public void update(User user) {
        //new UpdateUserAsyncTask(userDao).execute(user);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                userDao.update(user);
            }
        });
    }
    public void delete(User user) {
        //new DeleteUserAsyncTask(userDao).execute(user);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                userDao.delete(user);
            }
        });
    }

    public LiveData<List<User>> getUsers() {return users;}

    public User getUserByIdDirect(int userId) {
        return userDao.getUserByIdDirect(userId);
    }

    /*private static class InsertUserAsyncTask extends AsyncTask<User,Void,Void> {

        private UserDao userDao;

        private InsertUserAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }
        //1.parameter for doInBackground method
        //2.paramater for onProgressUpdate method
        //3.parameter return type of doInBackground
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User,Void,Void> {

        private UserDao userDao;

        private UpdateUserAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
        //1.parameter for doInBackground method
        //2.paramater for onProgressUpdate method
        //3.parameter return type of doInBackground
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User,Void,Void> {

        private UserDao userDao;

        private DeleteUserAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
        //1.parameter for doInBackground method
        //2.paramater for onProgressUpdate method
        //3.parameter return type of doInBackground
    }*/
}
