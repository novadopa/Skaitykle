package com.example.skaitykle.DataBase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRep userRep;
    private LiveData<List<User>> users;
    public UserViewModel(@NonNull Application application) {
        super(application);

        userRep = new UserRep(application);
        users = userRep.getUsers();
    }

    public void insert(User user) { userRep.insert(user);}

    public void update(User user) { userRep.update(user);}

    public void delete(User user) { userRep.delete(user);}

    public LiveData<List<User>> getUsers() { return users;}
}
