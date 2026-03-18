package com.example.skaitykle.DataBase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Book.class},version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase Adb;

    //public abstract UserDao userDao();
    public abstract BookDao bookDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (Adb == null){
            Adb = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class
                    , "App_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return Adb;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            //new PopulateDbAsyncTask(Adb).execute();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //UserDao userDao = Adb.userDao();
                    // Users
                    /*userDao.insert(new User("Vardenis", "Pavardenis",
                            "vardenis@gmail.com", "123"));
                    userDao.insert(new User("Antras", "Pavardas",
                            "antras@gmail.com", "123"));
                    userDao.insert(new User("trečias", "Krepšias",
                            "trečias@gmail.com", "123"));*/

                    BookDao bookDao = Adb.bookDao();
                    // Books
                    bookDao.insert(new Book("The Great Gatsby",
                            "A story of the fabulously wealthy Jay Gatsby",
                            "F. Scott Fitzgerald", "", ""));

                    bookDao.insert(new Book("1984",
                            "A dystopian novel set in a totalitarian society",
                            "George Orwell", "", ""));

                    bookDao.insert(new Book("To Kill a Mockingbird",
                            "A story of racial injustice in the American South",
                            "Harper Lee", "", ""));
                }
            });
        }
    };

    /*private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{

        private UserDao userDao;

        private PopulateDbAsyncTask(AppDatabase database){
            userDao = database.userDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            userDao.insert(new User("Vardenis", "Pavardenis"
                    , "vardenis@gmail.com", "123"));
            userDao.insert(new User("Antras", "Pavardas"
                    , "antras@gmail.com", "123"));
            userDao.insert(new User("trečias", "Krepšias"
                    , "trečias@gmail.com", "123"));

            return null;
        }
    }*/
}
