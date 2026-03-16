package com.example.skaitykle;

import java.util.List;

public class BookEntity {
    private long id;
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

    public void setId(long id) {this.id=id;}
    public long getId() {return this.id;}

    public void setBookTitle(String bookTitle){this.bookTitle=bookTitle;}
    public String getBookTitle() {return this.bookTitle;}

    public void setBookAuthor(String bookTitle){this.bookAuthor=bookAuthor;}
    public String getBookAuthor() {return this.bookAuthor;}


    public int getBookCoverId() {return bookCoverId;}


    public void setTotalPages(int totalPages) {this.totalPages = totalPages;}
    public int getTotalBookPages() {return totalPages;}

    public void setPagesRead(int pagesRead) {this.pagesRead = pagesRead;}

    public int getPagesRead() {return pagesRead;}

    public void setGenres(List<String> genres) {this.genres = genres;}

    public List<String> getGenres() {return this.genres;}
}
