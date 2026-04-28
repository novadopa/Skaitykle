package com.example.skaitykle.DataBase;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skaitykle.BookDetails;
import com.example.skaitykle.R;
import com.google.android.material.imageview.ShapeableImageView;

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


        String coverUri = currentBook.getCoverUri();
        if (coverUri != null && !coverUri.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(coverUri))
                    .placeholder(R.drawable.cover)
                    .error(R.drawable.cover)
                    .centerCrop()
                    .into(holder.imageViewCover);
        } else {
            holder.imageViewCover.setImageResource(R.drawable.cover);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetails.class);
            intent.putExtra("BookId", currentBook.getBid());
            intent.putExtra("BookTitle", currentBook.getTitle());
            intent.putExtra("BookAuthor", currentBook.getAuthor());
            intent.putExtra("BookDescription", currentBook.getDescription());
            intent.putExtra("BookPath", currentBook.getBookPath());
            intent.putExtra("BookCover", currentBook.getCoverUri());
            intent.putExtra("BookTotalPages", currentBook.getTotalPages());

            // Create the transition animation
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    (Activity) v.getContext(),
                    holder.imageViewCover,
                    "bookCover"
            );

            v.getContext().startActivity(intent, options.toBundle());
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
        ShapeableImageView imageViewCover;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);
        }
    }
}