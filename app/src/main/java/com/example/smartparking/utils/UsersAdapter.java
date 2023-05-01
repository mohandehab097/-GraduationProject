package com.example.smartparking.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparking.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsersAdapter extends ListAdapter<String, UsersAdapter.ViewHolder> {
    public UsersAdapter() {
        super(new UsersDiffer());
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_rv_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {

        holder.userName.setText(getItem(position));


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);

        }
    }
}
