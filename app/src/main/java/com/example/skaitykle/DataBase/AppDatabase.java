package com.example.skaitykle.DataBase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Book.class, UserBook.class},version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static AppDatabase Adb;

    public abstract UserDao userDao();
    public abstract BookDao bookDao();
    public abstract UserBookDao userBookDao();

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
                    UserDao userDao = Adb.userDao();
                    // Users
                    userDao.insert(new User("Vardenis", "Pavardenis",
                            "vardenis@gmail.com", "123"));
                    userDao.insert(new User("Antras", "Pavardas",
                            "antras@gmail.com", "123"));
                    userDao.insert(new User("trečias", "Krepšias",
                            "trečias@gmail.com", "123"));

                    BookDao bookDao = Adb.bookDao();
                    // Books
                    bookDao.insert(new Book("Ant stuff",
                            "A story of the fabulously wealthy Jay Gatsby",
                            "F. Scott Fitzgerald", "ant stuff.pdf", "",  245,
                            Arrays.asList("Drama")));

                    bookDao.insert(new Book("1984",
                            "A dystopian novel set in a totalitarian society",
                            "George Orwell", "1984test.pdf", "", 268,
                            Arrays.asList("Dystopian")));

                    bookDao.insert(new Book("To Kill a Mockingbird",
                            "A story of racial injustice in the American South",
                            "Harper Lee", "mockingTest.pdf", "", 178,
                            Arrays.asList("Drama, comedy")));

                    bookDao.insert(new Book("Great gatsby", "nonel about ants",
                            "Antman johnson", "gatsbytest.pdf", "", 245,
                            Arrays.asList("Horror, Fantasy")));
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
