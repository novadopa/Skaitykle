package com.example.skaitykle;

import java.util.List;

public class BookEntity {
    private String bookTitle;
    private String bookAuthor;
    private int bookCoverId;
    private int totalPages;
    private int pagesRead;
    private List<String> genres;

    public BookEntity(String title, String author, int coverId, int totalPages, int pagesRead,
                      List<String> genres){
        this.bookTitle = title;
        this.bookAuthor = author;
        this.bookCoverId = coverId;
        this.totalPages = totalPages;
        this.pagesRead = pagesRead;
        this.genres = genres;
    }

    public String getBookTitle() {return bookTitle;}
    public String getBookAuthor() {return bookAuthor;}
    public int getBookCoverId() {return bookCoverId;}
    public int getTotalBookPages() {return totalPages;}
    public int getPagesRead() {return pagesRead;}
    public List<String> getGenres() {return genres;}
}
