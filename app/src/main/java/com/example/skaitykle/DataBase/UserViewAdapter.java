package com.example.skaitykle.DataBase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.R;

import java.util.ArrayList;
import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserHolder> {

    private List<User> users = new ArrayList<>();

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_for_title
                , parent, false);


        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

        User currentUser = users.get(position);
        //holder.textViewFirstName.setText(currentUser.getFirstName());
        //holder.textViewEmail.setText(currentUser.getEmail());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView textViewBookTitle;
        TextView textViewEmail;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewEmail = itemView.findViewById(R.id.textViewAuthor);

        }
    }
}
