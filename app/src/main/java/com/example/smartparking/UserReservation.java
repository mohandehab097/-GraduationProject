package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserReservation extends AppCompatActivity {


    TextView backHomePageBtn,currentReservationsBtn,historyReservationBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservation);
        backHomePageBtn=findViewById(R.id.backHomePage);
        currentReservationsBtn=findViewById(R.id.currentReservation);
        historyReservationBtn=findViewById(R.id.historyReservation);


        backHomePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAnotherPage(HomeActivity.class);
            }
        });

        currentReservationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAnotherPage(CurrentReservationsActivity.class);
            }
        });



        historyReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAnotherPage(HistoryReservations.class);
            }
        });

    }




    private void goToAnotherPage (Class<?> toActivity){
        startActivity(new Intent(UserReservation.this,toActivity));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}