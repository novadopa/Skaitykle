package com.example.skaitykle.DataBase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.R;

import java.util.ArrayList;
import java.util.List;

public class GenreRowAdapter extends RecyclerView.Adapter<GenreRowAdapter.GenreRowHolder> {

    private List<GenreRow> genreRows = new ArrayList<>();

    @NonNull
    @Override
    public GenreRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_row, parent, false);
        return new GenreRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreRowHolder holder, int position) {
        GenreRow row = genreRows.get(position);
        holder.textViewGenreName.setText(row.genreName);

        BookViewAdapter innerAdapter = new BookViewAdapter();
        holder.recyclerViewBooks.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewBooks.setAdapter(innerAdapter);
        innerAdapter.setBooks(row.books);
    }

    @Override
    public int getItemCount() { return genreRows.size(); }

    public void setGenreRows(List<GenreRow> rows) {
        this.genreRows = rows;
        notifyDataSetChanged();
    }

    class GenreRowHolder extends RecyclerView.ViewHolder {
        TextView textViewGenreName;
        RecyclerView recyclerViewBooks;

        public GenreRowHolder(@NonNull View itemView) {
            super(itemView);
            textViewGenreName = itemView.findViewById(R.id.textViewGenreName);
            recyclerViewBooks = itemView.findViewById(R.id.recyclerViewBooksInGenre);
        }
    }
}