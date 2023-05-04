package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.Payment;
import com.example.smartparking.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

public class PaymentActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    DatabaseReference paymentRef;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        paymentRef=rootRef.child("Payment");
        slotReff=rootRef.child("UsersSlotBooking");


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
                            StringBuilder reverseLicenseNumber = new StringBuilder();
                            reverseLicenseNumber.append(slotBooking.getLicenseNumber());

                            // reverse StringBuilder input1
                            reverseLicenseNumber.reverse();
                            userLicenseNumber=slotBooking.getLiceseCharacter() + " " + reverseLicenseNumber;
                            fun();
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }


    private void fun(){

        FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentifier.identifyLanguage(userLicenseNumber).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@Nullable String languageCode) {
                if (languageCode != "und") {
                    Log.i("TAG", "Language: " + languageCode);
                } else {
                    Log.i("TAG", "Can't identify language.");
                }
            }
        });
//
//        paymentRef.child(userLicenseNumber).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//                for (DataSnapshot snapshot2 :  snapshot.getChildren() ){
//                    Payment userPayment = snapshot2.getValue(Payment.class);
////                    stayedHourMinutes.setText(userPayment.getUserStayedHour()+"h"+userPayment.getUserStayedMinutes()+"m");
//                    if(userPayment!=null){
//                        amount.setText(userPayment.getLicenseNumber()+ "$");
//                    }
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}