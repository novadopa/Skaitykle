package com.example.skaitykle;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.skaitykle.BookDetails;
import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class SearchDropdownAdapter extends RecyclerView.Adapter<SearchDropdownAdapter.DropdownHolder> {

    private List<Book> books = new ArrayList<>();

    @NonNull
    @Override
    public DropdownHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        return new DropdownHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DropdownHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        Glide.with(holder.itemView.getContext())
                .load(book.getCoverUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.cover)
                .error(R.drawable.cover)
                .centerCrop()
                .into(holder.cover);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetails.class);
            intent.putExtra("BookId", book.getBid());
            intent.putExtra("BookTitle", book.getTitle());
            intent.putExtra("BookAuthor", book.getAuthor());
            intent.putExtra("BookDescription", book.getDescription());
            intent.putExtra("BookPath", book.getBookPath());
            intent.putExtra("BookCover", book.getCoverUri());
            intent.putExtra("BookTotalPages", book.getTotalPages());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return books.size(); }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    class DropdownHolder extends RecyclerView.ViewHolder {
        TextView title, author;
        ShapeableImageView cover;

        public DropdownHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.searchResultTitle);
            author = itemView.findViewById(R.id.searchResultAuthor);
            cover = itemView.findViewById(R.id.searchResultCover);
        }
    }
}