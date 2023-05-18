package com.example.smartparking.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparking.CurrentReservationsActivity;
import com.example.smartparking.DeleteReservationDialog;
import com.example.smartparking.R;
import com.example.smartparking.models.ParkingSlotBooking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentUserReservationAdapter extends ListAdapter<ParkingSlotBooking, CurrentUserReservationAdapter.ViewHolder> {



    Context context;

    public CurrentUserReservationAdapter(Context context) {
        super(new CurrentUserReservationDiffer());
        this.context=context;
    }

    @NonNull
    @Override
    public CurrentUserReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_current_user_reservation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentUserReservationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        List<ParkingSlotBooking> list = new ArrayList<ParkingSlotBooking>(Collections.singleton(getItem(position)));

        holder.bookingDate.setText(list.get(0).getStrBookingDate());
        holder.bookingTime.setText(list.get(0).getTime());
        holder.carCharacter.setText(list.get(0).getLiceseCharacter());
        holder.carNumbers.setText(list.get(0).getLicenseNumber());
        holder.maxTime.setText(list.get(0).getLimittime());

        holder.cancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference rootRef;
                DatabaseReference slotRefernce;
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String uid;
                FirebaseUser authUser = auth.getCurrentUser();
                uid = authUser.getUid();
                rootRef = FirebaseDatabase.getInstance().getReference();
                slotRefernce = rootRef.child("UsersSlotBooking");
                deleteUserBooking(position,list);
                if (slotRefernce != null && uid != null) {
                    slotRefernce.child(uid).removeValue();
                }
                list.remove(0);


            }
        });




    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDate, bookingTime, carCharacter, carNumbers, maxTime, cancelReservation;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            carNumbers = itemView.findViewById(R.id.carNumber);
            carCharacter = itemView.findViewById(R.id.carCharacter);
            maxTime = itemView.findViewById(R.id.allowLateTime);
            cancelReservation = itemView.findViewById(R.id.cancelReservation);

        }
    }


    private void deleteUserBooking(int position, List<ParkingSlotBooking> list){

        FirebaseFirestore.getInstance()
                .collection("UserBooking").document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).collection("userReservationDocument")
                .document(list.get(0).getDocumentId()).delete()

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            if(list.size()>0||!list.isEmpty())
                            list.remove(0);
                        }
                        notifyDataSetChanged();
                        context.startActivity(new Intent(context, CurrentReservationsActivity.class));

                    }
                });
    }
}
