package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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


    TextView username,email,userId,phoneNumber;
    TextView logout,reservationBtn;
    String userEmail="";
    String userUid="";
    String userName="";
    String userPhoneNumber="";
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference("Users");
    DatabaseReference slotRef = database.getReference("ParkingSlots");
    String UID=authUser.getUid();
    String x="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        reservationBtn=findViewById(R.id.goToReservation);


        logout=findViewById(R.id.logoutBtn);

        List<String> slotsAvailability=new ArrayList<>();



        slotRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()) {

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


        reservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int emptySlotsCount=0;
                boolean validBooking=false;

                for (String slots:slotsAvailability){

                    if(slots.equals("on")){
                        emptySlotsCount++;
                        validBooking=true;
                    }
                }

                if(validBooking) {
                    startActivity(new Intent(HomeActivity.this, ReservationActivity.class));
                    overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
                    slotsAvailability.clear();
                }

                else{

                    ErrorDialog cdd=new ErrorDialog(HomeActivity.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations=R.style.CustomDialogAnimation;
                    cdd.show();
                }

            }
        });





    }

    private void logout(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                auth.signOut();
                startActivity(new Intent(HomeActivity.this,PaymentActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

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
        if(str!=null&&!str.isEmpty()&&str.equals("ShowErrorDialog")){
            ErrorDialog cdd=new ErrorDialog(HomeActivity.this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations=R.style.CustomDialogAnimation;
            cdd.show();
        }

    }
}