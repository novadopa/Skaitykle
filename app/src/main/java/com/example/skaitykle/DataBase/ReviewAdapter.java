package com.example.skaitykle.DataBase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private List<ReviewWithUsername> reviews = new ArrayList<>();

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView ratingAuthor;
        TextView commentText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
            ratingAuthor = itemView.findViewById(R.id.reviewAuthor);
            commentText = itemView.findViewById(R.id.reviewCommentText);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewWithUsername item = reviews.get(position);
        holder.ratingBar.setRating(item.review.getRating());

        holder.ratingAuthor.setText(
                item.userName != null ? item.userName : "Unknown user");

        String comment = item.review.getComment();
        if (comment != null && !comment.isEmpty()) {
            holder.commentText.setVisibility(View.VISIBLE);
            holder.commentText.setText(comment);
        } else {
            holder.commentText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return reviews.size(); }

    public void setReviews(List<ReviewWithUsername> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}
