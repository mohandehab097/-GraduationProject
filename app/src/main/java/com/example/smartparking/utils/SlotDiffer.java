package com.example.smartparking.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.smartparking.models.ParkingSlots;

import java.util.Map;
import java.util.Objects;

public class SlotDiffer extends DiffUtil.ItemCallback<Map<String,String>> {
    @Override
    public boolean areItemsTheSame(@NonNull Map<String,String> oldItem, @NonNull Map<String,String> newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Map<String,String> oldItem, @NonNull Map<String,String> newItem) {
        return Objects.equals(oldItem,newItem);
    }
}
