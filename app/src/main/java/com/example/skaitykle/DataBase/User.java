package com.example.skaitykle.DataBase;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import org.jspecify.annotations.NonNull;

@Entity(tableName = "User")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @NonNull
    @ColumnInfo(name = "first_name")
    public String firstName;

    @NonNull
    @ColumnInfo(name = "last_name")
    public String lastName;

    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @NonNull
    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "profile_photo_uri")
    public String profilePhotoUri;

    @ColumnInfo(name = "is_admin")
    public boolean isAdmin = false;

    @ColumnInfo(name = "is_banned")
    public boolean isBanned = false;

    public User(@NonNull String firstName, @NonNull String lastName,
                @NonNull String email, @NonNull String password) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.password  = password;
    }

    public int getUid()                { return uid; }
    public void setUid(int uid)        { this.uid = uid; }

    public @NonNull String getFirstName()  { return firstName; }
    public @NonNull String getLastName()   { return lastName; }
    public @NonNull String getEmail()      { return email; }
    public @NonNull String getPassword()   { return password; }
    public String getProfilePhotoUri()     { return profilePhotoUri; }
    public boolean isAdmin()               { return isAdmin; }
    public boolean isBanned()              { return isBanned; }
}