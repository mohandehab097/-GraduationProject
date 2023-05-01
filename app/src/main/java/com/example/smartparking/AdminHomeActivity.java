package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartparking.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeActivity extends AppCompatActivity {

    TextView logout, showUsers, showReservation, showParkingStatus, showParkingSlots;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);


        logout = findViewById(R.id.logoutBtn);
        showUsers = findViewById(R.id.Show_Users);
        showReservation = findViewById(R.id.Show_Reservation);
        showParkingStatus = findViewById(R.id.Show_Status);
        showParkingSlots = findViewById(R.id.Show_Slots);



        goToAnotherPage(showUsers,ShowUsresActivity.class);
        goToAnotherPage(showParkingSlots,ShowParkingSlotsActivity.class);
        goToAnotherPage(showParkingStatus,ShowParkingStatusActivity.class);
        goToAnotherPage(showReservation,ShowReservationActivity.class);

        logout();


    }

private void goToAnotherPage(TextView textView, Class<?> secondActivity){
    textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            startActivity(new Intent(AdminHomeActivity.this, secondActivity));
            finish();
        }
    });
}

    private void logout() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(AdminHomeActivity.this, SecondActivity.class));
                finish();
            }
        });
    }
}