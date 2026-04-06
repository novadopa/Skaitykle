package com.example.skaitykle.DataBase;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.BookDetails;
import com.example.skaitykle.R;

import java.util.ArrayList;
import java.util.List;

public class BookViewAdapter extends RecyclerView.Adapter<BookViewAdapter.BookHolder> {

    private List<Book> books = new ArrayList<>();

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.books_for_title, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        Book currentBook = books.get(position);
        holder.textViewTitle.setText(currentBook.getTitle());
        holder.textViewAuthor.setText(currentBook.getAuthor());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetails.class);
            intent.putExtra("BookId", currentBook.getBid());
            intent.putExtra("BookTitle", currentBook.getTitle());
            intent.putExtra("BookAuthor", currentBook.getAuthor());
            intent.putExtra("BookDescription", currentBook.getDescription());
            intent.putExtra("BookPath",        currentBook.getBookPath());
            intent.putExtra("BookCover",       currentBook.getCoverUri());
            intent.putExtra("BookTotalPages",  currentBook.getTotalPages());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return books.size(); }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    class BookHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewAuthor;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
        }
    }
}