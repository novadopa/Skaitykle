package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class UserBookViewModel extends AndroidViewModel {
    private UserBookRep userBookRep;
    private LiveData<List<UserBook>> userBooks;

    public UserBookViewModel(@NonNull Application application){
        super(application);

        userBookRep = new UserBookRep(application);
        userBooks = userBookRep.getUserBooks();
    }

    public void insert(UserBook userBook) {userBookRep.insert(userBook);}
    public void update(UserBook userBook) {userBookRep.update(userBook);}
    public void delete(UserBook userBook) {userBookRep.delete(userBook);}
    public LiveData<List<UserBook>> getUserBooks() {return userBooks;}
}
