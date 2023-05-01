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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_booking);
        CountdownView mCvCountdownView = findViewById(R.id.mycountdown);


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
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    if (countDownToNewYear <= 200000 && countDownToNewYear>0) {
                        addNotification();
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

//
//    private void startAlarm(Calendar c,AlarmManager alarmManager,PendingIntent pendingIntent) {
//
//        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
//                c.getTimeInMillis(), pendingIntent);
//    }

    private void addNotification() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_notify");
        builder.setContentTitle("RAKNII");
        builder.setContentText("Your Time for Your parking Slot Is Very Close");

        builder.setSmallIcon(R.mipmap.icon);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setVibrate(new long[]{1000, 1000, 1000,
                1000, 1000});

        Intent intent=new Intent(this,CounterBookingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(CounterBookingActivity.this);
        notificationManagerCompat.notify(1,builder.build());

    }
    @SuppressLint("RemoteViewLayout")
    public RemoteViews getRemoteView(String title, String message){
        RemoteViews remoteViews= new RemoteViews("com.example.smartparking",R.layout.activity_counter_booking);
        return remoteViews;
    }


}

