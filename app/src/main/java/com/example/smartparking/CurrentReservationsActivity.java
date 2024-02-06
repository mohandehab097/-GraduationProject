package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.utils.CurrentUserReservationAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class CurrentReservationsActivity extends AppCompatActivity {


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    TextView goToBooking,noBookingText,cancelReservation,backUserReservation;
    ImageView cancelReservationIcon;
    RecyclerView userReservationList;
    List<ParkingSlotBooking> parkingSlotBookings;
    CurrentUserReservationAdapter adapter;
    RelativeLayout layoutGoToBooking;
    RelativeLayout cancelReservationLayout;
    DatabaseReference rootRef;
    DatabaseReference slotRefernce;
    String uid;
    Query userBookingQueryDelete ;
    CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_reservations);
        userReservationList = findViewById(R.id.userReserve);
        goToBooking = findViewById(R.id.goToBooking);
        noBookingText=findViewById(R.id.noBooking);
        layoutGoToBooking=findViewById(R.id.layoutGoToBooking);
        cancelReservation=findViewById(R.id.cancelReservation);
        backUserReservation=findViewById(R.id.backUserReservationBtn);
        cancelReservationIcon=findViewById(R.id.cancelReservation_icon);
        cancelReservationLayout=findViewById(R.id.cancelReservationLayout);
        adapter= new CurrentUserReservationAdapter(CurrentReservationsActivity.this);

         collectionReference = FirebaseFirestore.getInstance()
                .collection("UserBooking").document(authUser.getUid()).collection("userReservationDocument");

         userBookingQueryDelete = collectionReference.whereEqualTo("reservationEnds",false);
        parkingSlotBookings=new ArrayList<>();
        userReservationList.setAdapter(adapter);

        if (authUser != null) {
            uid = authUser.getUid();
            rootRef = FirebaseDatabase.getInstance().getReference();
            slotRefernce = rootRef.child("UsersSlotBooking");
        }







        if (collectionReference != null) {
            getUserBookingData(false);
        }

        goToBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBookingPage();
            }
        });

        cancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance()
                        .collection("UserBooking").document(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).collection("userReservationDocument")
                        .whereEqualTo("reservationEnds",false)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                WriteBatch writeBatch=FirebaseFirestore.getInstance().batch();
                                List<DocumentSnapshot>snapshotList=queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot snapshot:snapshotList){
                                    writeBatch.delete(snapshot.getReference());

                                }
                                writeBatch.commit();
                            }
                        });

                if (slotRefernce != null && uid != null) {
                    slotRefernce.child(uid).removeValue();

                }

                parkingSlotBookings.clear();
                adapter.notifyDataSetChanged();
                getUserBookingData(true);
            }
        });

        backUserReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserReservationPage();
            }
        });



    }

    private void getUserBookingData(boolean checkDelete){

        collectionReference.whereEqualTo("reservationEnds",false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot document : task.getResult()) {


                    ParkingSlotBooking slotBooking = document.toObject(ParkingSlotBooking.class);
                    slotBooking.setDocumentId(document.getId());
                    if (slotBooking != null) {
                        parkingSlotBookings.add(slotBooking);
                    }

                }
                if(checkDelete){
                    parkingSlotBookings.clear();
                }

                if(!parkingSlotBookings.isEmpty()||parkingSlotBookings.size()>0){
                    adapter.submitList(parkingSlotBookings);
                    goToBooking.setVisibility(View.GONE);
                    layoutGoToBooking.setVisibility(View.GONE);
                    noBookingText.setVisibility(View.GONE);
                    cancelReservation.setVisibility(View.VISIBLE);
                    cancelReservationIcon.setVisibility(View.VISIBLE);
                    cancelReservationLayout.setVisibility(View.VISIBLE);
                }
                else{
                    userReservationList.setVisibility(View.GONE);
                    cancelReservationIcon.setVisibility(View.GONE);
                    goToBooking.setVisibility(View.VISIBLE);
                    noBookingText.setVisibility(View.VISIBLE);
                    layoutGoToBooking.setVisibility(View.VISIBLE);
                    cancelReservation.setVisibility(View.GONE);
                    cancelReservationLayout.setVisibility(View.GONE);
                }


            }
        });
    }






    private void goToUserReservationPage() {
        startActivity(new Intent(CurrentReservationsActivity.this, UserReservation.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void goToBookingPage() {
        startActivity(new Intent(CurrentReservationsActivity.this, ReservationActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

}