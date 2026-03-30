package com.example.skaitykle.DataBase;

import java.util.List;

public class GenreRow {
    public String genreName;
    public List<Book> books;

    public GenreRow(String genreName, List<Book> books) {
        this.genreName = genreName;
        this.books = books;
    }
}