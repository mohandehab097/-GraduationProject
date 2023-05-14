package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.iwgang.countdownview.CountdownView;

public class CounterBookingActivity extends AppCompatActivity {

    FirebaseDatabase databasee = FirebaseDatabase.getInstance();
    DatabaseReference slotReff = databasee.getReference("UsersSlotBooking");
    String uid;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    String userBookingDate = null;
    String userBookingTime = null;
    long countDownToNewYear;
    TextView backToHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_booking);
        CountdownView mCvCountdownView = findViewById(R.id.mycountdown);
        backToHomeBtn=findViewById(R.id.backToHome);

        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomePage();
            }
        });

        if (authUser != null) {
            uid = authUser.getUid();
        }
        slotReff = databasee.getReference("UsersSlotBooking").child(uid);

        slotReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                if (slotBooking != null) {
                    userBookingDate = slotBooking.getStrBookingDate();
                    userBookingTime = slotBooking.getTime();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm");
                String countDate = userBookingDate + " " + userBookingTime;
                Date now = new Date();


                try {
                    //Formatting from String to Date
                    Date date = sdf.parse(countDate);
                    long currentTime = now.getTime();
                    long bookingDate = date.getTime();
                    countDownToNewYear = bookingDate - currentTime;
                    mCvCountdownView.start(countDownToNewYear);

                    if (countDownToNewYear == 0 || countDownToNewYear < 0) {
                        goToHomePage();
                    }

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        NotificationChannel notificationChannel=new NotificationChannel("my_notify","my_notify",NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager=getSystemService(NotificationManager.class);
                        notificationChannel.setShowBadge(true);
                        notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    if (countDownToNewYear <= 200000 && countDownToNewYear>0) {
//                        addNotification();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //

    }



    private void goToHomePage() {
        Intent intent = new Intent(CounterBookingActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }




}

