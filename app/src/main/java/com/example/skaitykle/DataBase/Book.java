package com.example.skaitykle.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import org.jspecify.annotations.NonNull;

import java.util.List;

@Entity(tableName = "Book")
public class Book {

    public static final String STATUS_APPROVED      = "APPROVED";
    public static final String STATUS_PERSONAL      = "PERSONAL";
    public static final String STATUS_PENDING       = "PENDING";
    public static final String STATUS_REJECTED      = "REJECTED";

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

    @ColumnInfo(name = "author_country")
    public String authorCountry;

    @NonNull
    @ColumnInfo(name = "book_path")
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

    @ColumnInfo(name = "added_by_user_id")
    public int addedByUserId = -1;

    @NonNull
    @ColumnInfo(name = "status")
    public String status = STATUS_APPROVED;

    public Book(@NonNull String title, @NonNull String description,
                @NonNull String author, @NonNull String authorCountry,
                @NonNull String bookPath, @NonNull String coverUri,
                int totalPages, @NonNull List<String> genres) {
        this.title       = title;
        this.description = description;
        this.author      = author;
        this.authorCountry = authorCountry;
        this.bookPath    = bookPath;
        this.coverUri    = coverUri;
        this.totalPages  = totalPages;
        this.genres      = genres;
    }

    public void setBid(int bid)      { this.bid = bid; }
    public int getBid()              { return bid; }

    public @NonNull String getTitle()        { return title; }
    public @NonNull String getDescription()  { return description; }
    public @NonNull String getAuthor()       { return author; }
    public String getAuthorCountry()         { return authorCountry; }
    public @NonNull String getBookPath()     { return bookPath; }
    public @NonNull String getCoverUri()     { return coverUri; }
    public int getTotalPages()               { return totalPages; }
    public @NonNull List<String> getGenres() { return genres; }
    public int getAddedByUserId()            { return addedByUserId; }
    public @NonNull String getStatus()       { return status; }
}