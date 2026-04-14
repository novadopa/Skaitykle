package com.example.skaitykle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.DataBase.AppDatabase;
import com.example.skaitykle.DataBase.BookWithReadingProgress;

import java.util.ArrayList;
import java.util.List;


public class LibraryBookAdapter extends RecyclerView.Adapter<LibraryBookAdapter.BookViewHolder> {

    private List<BookWithReadingProgress> bookList;
    private List<BookWithReadingProgress> fullBookList;
    private String currentSearch="";
    private String currentAuthor = "All";
    private String currentGenre = "All";

    private String searchMode = "All";

    private static final int currentUserId = 1;

    public interface OnBookClickListener {
        void onBookClick(BookWithReadingProgress book);
    }

    private OnBookClickListener bookClickListener;


    public LibraryBookAdapter(List<BookWithReadingProgress> bookList, OnBookClickListener bookClickListener){
        this.bookList = new ArrayList<>(bookList);
        this.fullBookList = new ArrayList<>(bookList);
        this.bookClickListener = bookClickListener;
    }


    public int getItemCount(){
        return bookList.size();
    }


    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;
        TextView bookPages;
        TextView bookPercentage;
        Button deleteBookButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.bookCoverImageView);
            title = itemView.findViewById(R.id.bookTitleTextView);
            author = itemView.findViewById(R.id.bookAuthorTextView);
            bookPages = itemView.findViewById(R.id.bookPagesTextView);
            bookPercentage = itemView.findViewById(R.id.bookPercentageTextView);
            deleteBookButton = itemView.findViewById(R.id.deleteBookButton);
        }
    }


    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext())).inflate(R.layout.book_item, parent,
                false);
        return new BookViewHolder(view);
    }

    public void onBindViewHolder(BookViewHolder holder, int position){
        BookWithReadingProgress bookItem = bookList.get(position);

        holder.title.setText(bookItem.book.getTitle());
        holder.author.setText(bookItem.book.getAuthor());

        if(bookItem.book.getCoverUri() != null && !bookItem.book.getCoverUri().isEmpty()){
            holder.cover.setImageURI(Uri.parse(bookItem.book.getCoverUri()));
        }else{
            holder.cover.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.bookPages.setText(bookItem.book.getTotalPages() + " pages");
        int totalPages = bookItem.book.getTotalPages();
        int pagesRead  = bookItem.getReadPages();
        if (totalPages > 1) {
            int percent = (pagesRead * 100) / (totalPages-1);
            holder.bookPercentage.setText("Reading progress: " + percent + "%");
        } else {
            holder.bookPercentage.setText("Reading progress: 0%");
        }

        holder.deleteBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext(), R.style.AnimatedDialog).setMessage("Are you sure you want to " +
                        "delete this book?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(bookItem.userBook == null) {return;}
                        AppDatabase db = AppDatabase.getInstance(v.getContext());
                        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {

                                db.userBookDao().deleteBookByUserAndBook(currentUserId,
                                        bookItem.book.getBid());
                            }
                        });
                    }
                }).setNegativeButton("No", null).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookClickListener.onBookClick(bookItem);
            }
        });
    }

    public void setBooks(List<BookWithReadingProgress> books){
        fullBookList.clear();
        fullBookList.addAll(books);
        ApplyFilters();
    }


    public void setSearchMode(String mode){
        searchMode = mode;
        ApplyFilters();
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

        for(BookWithReadingProgress bookItem : fullBookList) {
            /*boolean matchesSearch =
                    bookItem.book.getTitle().toLowerCase().contains(currentSearch) ||
                            bookItem.book.getAuthor().toLowerCase().contains(currentSearch);*/
            boolean matchesSearch;
            if(searchMode.equals("Title")){
                matchesSearch = bookItem.book.getTitle().toLowerCase().contains(currentSearch);
            }else if(searchMode.equals("Author")){
                matchesSearch = bookItem.book.getAuthor().toLowerCase().contains(currentSearch);
            }
            else{
                matchesSearch = bookItem.book.getTitle().toLowerCase().contains(currentSearch) ||
                        bookItem.book.getAuthor().toLowerCase().contains(currentSearch);
            }

            boolean matchesAuthor =
                    currentAuthor.equals("All") ||
                            bookItem.book.getAuthor().equals(currentAuthor);

            boolean matchesGenre =
                    currentGenre.equals("All") ||
                            bookItem.book.getGenres().contains(currentGenre);

            if (matchesSearch && matchesAuthor && matchesGenre) {
                bookList.add(bookItem);
            }
        }
        notifyDataSetChanged();
    }
}
