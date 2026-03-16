package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookRep {
    private BookDao bookDao;
    private LiveData<List<Book>> books;

    ExecutorService executors = Executors.newSingleThreadExecutor();

    public BookRep(Application application){
        AppDatabase Adb = AppDatabase.getInstance(application);
        bookDao = Adb.bookDao();
        books = bookDao.getAllBooks();
    }

    public void insert(Book book) {
        //new InsertUserAsyncTask(userDao).execute(user);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                bookDao.insert(book);
            }
        });
    }
    public void update(Book book) {
        //new InsertUserAsyncTask(userDao).execute(user);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                bookDao.update(book);
            }
        });
    }
    public void delete(Book book) {
        //new InsertUserAsyncTask(userDao).execute(user);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                bookDao.delete(book);
            }
        });
    }

    public LiveData<List<Book>> getBooks() {return books;}

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
