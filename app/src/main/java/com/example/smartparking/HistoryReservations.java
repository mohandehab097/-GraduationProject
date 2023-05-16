package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.utils.UserReservationAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryReservations extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    TextView goToBooking,noBookingText;
    RecyclerView userReservationList;
    List<ParkingSlotBooking> parkingSlotBookings;
    UserReservationAdapter adapter;
    RelativeLayout layoutGoToBooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_reservations);
        userReservationList = findViewById(R.id.userReserve);
        goToBooking = findViewById(R.id.goToBooking);
        noBookingText=findViewById(R.id.noBooking);
        layoutGoToBooking=findViewById(R.id.layoutGoToBooking);
        adapter= new UserReservationAdapter();
        parkingSlotBookings=new ArrayList<>();
        userReservationList.setAdapter(adapter);
        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("UserBooking").document(authUser.getUid()).collection("userReservationDocument");
        if (collectionReference != null) {

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ParkingSlotBooking slotBooking = document.toObject(ParkingSlotBooking.class);
                        if (slotBooking != null) {
                            parkingSlotBookings.add(slotBooking);
                        }

                    }

                    if(!parkingSlotBookings.isEmpty()||parkingSlotBookings.size()>0){
                        adapter.submitList(parkingSlotBookings);
                        goToBooking.setVisibility(View.GONE);
                        layoutGoToBooking.setVisibility(View.GONE);
                        noBookingText.setVisibility(View.GONE);
                    }
                    else{
                        userReservationList.setVisibility(View.GONE);
                        goToBooking.setVisibility(View.VISIBLE);
                        noBookingText.setVisibility(View.VISIBLE);
                        layoutGoToBooking.setVisibility(View.VISIBLE);


                    }


                }
            });
        }

        goToBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBookingPage();
            }
        });
    }


    private void goToBookingPage() {
        startActivity(new Intent(HistoryReservations.this, ReservationActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }


}