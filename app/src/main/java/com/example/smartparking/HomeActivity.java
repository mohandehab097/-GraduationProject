package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    TextView reservationDatBtn;
    TextView logout, reservationBtn, profileBtn;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference slotRef = database.getReference("ParkingSlots");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        reservationBtn = findViewById(R.id.goToReservation);
        reservationDatBtn=findViewById(R.id.reservationData);

        logout = findViewById(R.id.logoutBtn);
        profileBtn = findViewById(R.id.goToProfile);
        List<String> slotsAvailability = new ArrayList<>();



        reservationDatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserReservationsPage();
            }
        });

        slotRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

//                    ParkingSlots parkingSlots = ds.getValue(ParkingSlots.class);

//                    String slotName = parkingSlots.getSlotName();
//                    String slotAvailability= parkingSlots.getSlotAvailability();

                    slotsAvailability.add(ds.getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        myRef.child(UID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
////                User userProfile=dataSnapshot.getValue(User.class);
//
//
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });

        logout();
        goToProfileActivity();


        reservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(HomeActivity.this, ReservationActivity.class));
                overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
                slotsAvailability.clear();


            }
        });


    }




    private void logout() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, SecondActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

            }
        });
    }

    private void goToProfileActivity() {
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
    }


//    private void goToReservation(){
//
//    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String str = intent.getStringExtra("noSlots");
        if (str != null && !str.isEmpty() && str.equals("ShowErrorDialog")) {
            ErrorDialog cdd = new ErrorDialog(HomeActivity.this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.show();
        }

    }

    private void goToUserReservationsPage (){
        startActivity(new Intent(HomeActivity.this, UserReservation.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}