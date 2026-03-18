package com.example.skaitykle.DataBase;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import org.jspecify.annotations.NonNull;

import java.util.List;

@Entity(tableName = "Book")
public class Book {

    @PrimaryKey(autoGenerate = true)
    public int bid;

    @NonNull
    @ColumnInfo(name = "title")
    public String title;
    @NonNull
    @ColumnInfo(name = "description")
    public String description;

    @NonNull
    @ColumnInfo(name = "author")
    public String author;

    @NonNull
    @ColumnInfo(name = "book_path")    //a path to a book file
    public String bookPath;

    @NonNull
    @ColumnInfo(name = "cover_uri")
    public String coverUri;


    @NonNull
    @ColumnInfo(name = "total_pages")
    public int totalPages;

    @NonNull
    @ColumnInfo(name = "genres")
    public List<String> genres;


    public Book(@NonNull String title, @NonNull String description, @NonNull String author,
                @NonNull String bookPath, @NonNull String coverUri, @NonNull int totalPages,
                @NonNull List<String> genres) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.bookPath = bookPath;
        this.coverUri = coverUri;
        this.totalPages = totalPages;
        this.genres = genres;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }
    public int getBid() {
        return bid;
    }

    public @NonNull String getTitle() {
        return title;
    }

    public @NonNull String getDescription() {
        return description;
    }

    public @NonNull String getAuthor() {
        return author;
    }

    public @NonNull String getBookPath() {
        return bookPath;
    }

    public @NonNull String getCoverUri() {
        return coverUri;
    }

    public  @NonNull int getTotalPages(){return totalPages;}
    public @NonNull List<String> getGenres() {return genres;}
}
