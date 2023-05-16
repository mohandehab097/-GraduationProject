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
import com.example.smartparking.models.ParkingSlotBooking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserReservationAdapter extends ListAdapter<ParkingSlotBooking, UserReservationAdapter.ViewHolder> {


    public UserReservationAdapter() {
        super(new UserReservationDiffer());
    }

    @NonNull
    @Override
    public UserReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_user_reservation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserReservationAdapter.ViewHolder holder, int position) {
        List<ParkingSlotBooking> list =new ArrayList<ParkingSlotBooking>(Collections.singleton(getItem(position)));

        holder.bookingDate.setText(list.get(0).getStrBookingDate());
        holder.bookingTime.setText(list.get(0).getTime());
        holder.carCharacter.setText(list.get(0).getLiceseCharacter());
        holder.carNumbers.setText(list.get(0).getLicenseNumber());
        holder.maxTime.setText(list.get(0).getLimittime());


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDate,bookingTime,carCharacter,carNumbers,maxTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            carNumbers = itemView.findViewById(R.id.carNumber);
            carCharacter = itemView.findViewById(R.id.carCharacter);
            maxTime = itemView.findViewById(R.id.allowLateTime);

        }
    }
}
