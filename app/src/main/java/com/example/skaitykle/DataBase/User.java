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

    //public void setUid(@NonNull int name) {this.uid = uid; }
    //public int getUid() {return this.uid; }
    //public void setFirstName (@NonNull String firstName) { this.firstName = firstName; }
    //public String getFirstName() {return this.firstName; }
    //public void setLastName(@NonNull String lastName) {this.lastName = lastName; }
    //public String getLastName() {return this.lastName; }


    public User(@NonNull String firstName, @NonNull String lastName, @NonNull String email, @NonNull String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public @NonNull String getFirstName() {
        return firstName;
    }

    public @NonNull String getLastName() {
        return lastName;
    }

    public @NonNull String getEmail() {
        return email;
    }

    public @NonNull String getPassword() {
        return password;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
