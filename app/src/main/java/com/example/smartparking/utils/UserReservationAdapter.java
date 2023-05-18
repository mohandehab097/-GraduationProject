package com.example.smartparking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparking.DeleteReservationDialog;
import com.example.smartparking.PaymentDialog;
import com.example.smartparking.HistoryReservations;
import com.example.smartparking.HomeActivity;
import com.example.smartparking.R;
import com.example.smartparking.UserReservation;
import com.example.smartparking.models.ParkingSlotBooking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserReservationAdapter extends ListAdapter<ParkingSlotBooking, UserReservationAdapter.ViewHolder> {
    Context context;
    Activity a;

    public UserReservationAdapter(Context context , Activity a) {

        super(new UserReservationDiffer());
        this.context=context;
        this.a=a;
    }

    @NonNull
    @Override
    public UserReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_user_reservation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserReservationAdapter.ViewHolder holder, int position) {
        List<ParkingSlotBooking> list = new ArrayList<ParkingSlotBooking>(Collections.singleton(getItem(position)));

        holder.bookingDate.setText(list.get(0).getStrBookingDate());
        holder.bookingTime.setText(list.get(0).getTime());
        holder.carCharacter.setText(list.get(0).getLiceseCharacter());
        holder.carNumbers.setText(list.get(0).getLicenseNumber());
        holder.maxTime.setText(list.get(0).getLimittime());

        holder.paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =  new Intent(context, HomeActivity.class);
//                context.startActivity(intent);

                PaymentDialog paymentDialog = new PaymentDialog(a,list.get(0).getStrBookingDate());
                paymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                paymentDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                paymentDialog.show();
            }
        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDate, bookingTime, carCharacter, carNumbers, maxTime, paymentBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            carNumbers = itemView.findViewById(R.id.carNumber);
            carCharacter = itemView.findViewById(R.id.carCharacter);
            maxTime = itemView.findViewById(R.id.allowLateTime);
            paymentBtn = itemView.findViewById(R.id.payment);

        }
    }
}
