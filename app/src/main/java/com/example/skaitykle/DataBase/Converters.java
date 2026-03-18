package com.example.skaitykle.DataBase;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> list){
        return  String.join(",", list);
    }

    @TypeConverter
    public static List<String> toList(String data){
        return Arrays.asList(data.split(","));
    }
}
