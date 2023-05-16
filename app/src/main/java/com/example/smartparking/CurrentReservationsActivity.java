package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentReservationsActivity extends AppCompatActivity {


    TextView backUserReservationBtn, bookingDate, bookingTime, carNumbers, carCharacter, timeAllowed,noBooking,goToBooking,cancelReservation;
    CardView bookingData;
    RelativeLayout layoutGoToBooking,cancelReservationLayout;
    DatabaseReference rootRef;
    DatabaseReference slotRefernce;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_reservations);
        backUserReservationBtn = findViewById(R.id.backUserReservationBtn);
        bookingDate = findViewById(R.id.bookingDate);
        bookingTime = findViewById(R.id.bookingTime);
        carCharacter = findViewById(R.id.carCharacter);
        carNumbers = findViewById(R.id.carNumber);
        timeAllowed = findViewById(R.id.allowLateTime);
        noBooking = findViewById(R.id.noBooking);
        goToBooking = findViewById(R.id.goToBooking);
        bookingData=findViewById(R.id.bookingData);
        layoutGoToBooking=findViewById(R.id.layoutGoToBooking);
        cancelReservation=findViewById(R.id.cancelReservation);
        cancelReservationLayout=findViewById(R.id.cancelReservationLayout);

        if (authUser != null) {
            uid = authUser.getUid();
            rootRef = FirebaseDatabase.getInstance().getReference();
            slotRefernce = rootRef.child("UsersSlotBooking");
        }



        cancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelReservationDialog();
            }
        });


        if (slotRefernce != null && uid != null) {
            slotRefernce.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                    if (slotBooking != null) {
                        bookingDate.setText(slotBooking.getStrBookingDate().trim());
                        bookingTime.setText(slotBooking.getTime().trim());
                        carCharacter.setText(slotBooking.getLiceseCharacter().trim());
                        carNumbers.setText(slotBooking.getLicenseNumber().trim());
                        timeAllowed.setText(slotBooking.getLimittime().trim());
                        goToBooking.setVisibility(View.GONE);
                        noBooking.setVisibility(View.GONE);
                        layoutGoToBooking.setVisibility(View.GONE);

                    }

                    else{
                        bookingData.setVisibility(View.GONE);
                        layoutGoToBooking.setVisibility(View.VISIBLE);
                        goToBooking.setVisibility(View.VISIBLE);
                        noBooking.setVisibility(View.VISIBLE);
                        cancelReservationLayout.setVisibility(View.GONE);
                        bookingDate.setVisibility(View.GONE);
                        bookingTime.setVisibility(View.GONE);
                        carCharacter.setVisibility(View.GONE);
                        carNumbers.setVisibility(View.GONE);
                        timeAllowed.setVisibility(View.GONE);
                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else{
            cancelReservationLayout.setVisibility(View.GONE);
            bookingData.setVisibility(View.GONE);
            layoutGoToBooking.setVisibility(View.VISIBLE);
            goToBooking.setVisibility(View.VISIBLE);
            noBooking.setVisibility(View.VISIBLE);
            bookingDate.setVisibility(View.GONE);
            bookingTime.setVisibility(View.GONE);
            carCharacter.setVisibility(View.GONE);
            carNumbers.setVisibility(View.GONE);
            timeAllowed.setVisibility(View.GONE);
        }


        backUserReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserReservationPage();
            }
        });

        goToBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBookingPage();
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



    private void showCancelReservationDialog() {

        if (slotRefernce != null && uid != null) {

        }
        DeleteReservationDialog deleteReservationDialog = new DeleteReservationDialog(CurrentReservationsActivity.this, slotRefernce.child(uid));
        deleteReservationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteReservationDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        deleteReservationDialog.show();
    }
}