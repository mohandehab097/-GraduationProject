package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.Payment;
import com.example.smartparking.models.PaymentNotification;
import com.example.smartparking.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

public class PaymentActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    DatabaseReference paymentRef;
    DatabaseReference notifyRef;
    DatabaseReference userPaymentRef;
    DatabaseReference slotReff ;
    DatabaseReference slotUserRef ;
    String uid;
    DatabaseReference uidRef;
    TextView stayedHourMinutes,amount;
    boolean checkLicense;
    String userLicenseNumber=null;
    FirebaseAuth auth;
    FirebaseUser authUser;
    String sendNotification=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        paymentRef=rootRef.child("Payment");
        notifyRef=rootRef.child("PaymentNotification");
        slotReff=rootRef.child("UsersSlotBooking");
        stayedHourMinutes=findViewById(R.id.hourMinutes);
        amount=findViewById(R.id.amount);

        if (authUser != null) {
            uid = authUser.getUid();
        }



        slotReff.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                    if (slotBooking != null) {
                        if (slotBooking.getLicenseNumbersAndCharacter() != null) {


                            userLicenseNumber=slotBooking.getLicenseNumber()+" "+slotBooking.getLiceseCharacter();

                            checkPayment();

//                            if(slotBooking.getSendNotification().equals("ON")){
//
//
//
//
//
//                            }
//                            checkNotification();


                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }


    private void checkPayment(){



        paymentRef.child(userLicenseNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                    Payment userPayment = snapshot.getValue(Payment.class);
                    if(userPayment!=null){
                        stayedHourMinutes.setText(userPayment.getStayedHourTime()+"h"+" "+userPayment.getStayedMinutesTime()+"m");
                        amount.setText(userPayment.getTheAmount()+ "$");
                    }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {

        Intent new_intent = new Intent(this, HomeActivity.class);

        this.startActivity(new_intent);

    }



}