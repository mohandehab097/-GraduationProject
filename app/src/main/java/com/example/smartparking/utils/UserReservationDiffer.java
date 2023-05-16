package com.example.smartparking.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.smartparking.models.ParkingSlotBooking;

import java.util.Map;
import java.util.Objects;

public class UserReservationDiffer extends DiffUtil.ItemCallback<ParkingSlotBooking> {
    @Override
    public boolean areItemsTheSame(@NonNull ParkingSlotBooking oldItem, @NonNull ParkingSlotBooking newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull ParkingSlotBooking oldItem, @NonNull ParkingSlotBooking newItem) {
        return Objects.equals(oldItem,newItem);
    }
}
