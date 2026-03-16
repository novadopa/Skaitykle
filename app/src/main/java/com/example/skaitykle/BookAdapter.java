package com.example.skaitykle;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<BookEntity> bookList;
    private List<BookEntity> fullBookList;
    private String currentSearch="";
    private String currentAuthor = "All";
    private String currentGenre = "All";

    public interface OnBookClickListener {
        void onBookClick(BookEntity book);
    }

    private OnBookClickListener bookClickListener;


    public BookAdapter(List<BookEntity> bookList, OnBookClickListener bookClickListener){
        this.bookList = new ArrayList<>(bookList);
        this.fullBookList = new ArrayList<>(bookList);
        this.bookClickListener = bookClickListener;
    }


    public int getItemCount(){
        return bookList.size();
    }


    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext())).inflate(R.layout.book_item, parent,
                false);
        return new BookViewHolder(view);
    }

    public void onBindViewHolder(BookViewHolder holder, int position){
        BookEntity book = bookList.get(position);

        holder.title.setText(book.getBookTitle());
        holder.author.setText(book.getBookAuthor());
        holder.cover.setImageResource(book.getBookCoverId());
        holder.bookPages.setText(book.getTotalBookPages() + " pages");
        holder.bookPercentage.setText("Reading progress: " +
                ((book.getPagesRead() * 100) / book.getTotalBookPages()) + "%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookClickListener.onBookClick(book);
                /*Intent bookReaderIntent = new Intent(v.getContext(), BookReader.class);
                bookReaderIntent.putExtra("title", book.getBookTitle());
                bookReaderIntent.putExtra("author", book.getBookAuthor());
                bookReaderIntent.putExtra("totalPages", book.getTotalBookPages());
                bookReaderIntent.putExtra("pagesRead", book.getPagesRead());

                v.getContext().startActivity(bookReaderIntent);*/
            }
        });
    }


    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;
        TextView bookPages;
        TextView bookPercentage;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.book_cover);
            title = itemView.findViewById(R.id.book_title);
            author = itemView.findViewById(R.id.book_author);
            bookPages = itemView.findViewById(R.id.book_pages);
            bookPercentage = itemView.findViewById(R.id.book_percentage);
        }
    }


    public void SearchBooks(String searchText){
        currentSearch = searchText.toLowerCase();
        ApplyFilters();
    }


    public void FilterAuthor(String author){
        currentAuthor = author;
        ApplyFilters();
    }


    public void FilterGenre(String genre){
        currentGenre = genre;
        ApplyFilters();
    }


    private void ApplyFilters(){
        bookList.clear();

        for(BookEntity book : fullBookList) {
            boolean matchesSearch =
                    book.getBookTitle().toLowerCase().contains(currentSearch) ||
                            book.getBookAuthor().toLowerCase().contains(currentSearch);

            boolean matchesAuthor =
                    currentAuthor.equals("All") ||
                            book.getBookAuthor().equals(currentAuthor);

            boolean matchesGenre =
                    currentGenre.equals("All") ||
                            book.getGenres().contains(currentGenre);

            if (matchesSearch && matchesAuthor && matchesGenre) {
                bookList.add(book);
            }
        }
        notifyDataSetChanged();
    }
}
