package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.Payment;
import com.example.smartparking.models.PaymentNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    DatabaseReference paymentRef;
    DatabaseReference notifyRef;
    DatabaseReference slotReff;
    String uid;
    DatabaseReference uidRef;
    TextView stayedHourMinutes, amount;
    String userLicenseNumber = null;
    FirebaseAuth auth;
    FirebaseUser authUser;
    Button visaBtn;
    FirebaseFirestore db;
    Payment userPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        paymentRef = rootRef.child("Payment");
        notifyRef = rootRef.child("PaymentNotification");
        slotReff = rootRef.child("UsersSlotBooking");
        stayedHourMinutes = findViewById(R.id.hourMinutes);
        amount = findViewById(R.id.amount);
        visaBtn = findViewById(R.id.visaBtn);
        db = FirebaseFirestore.getInstance();


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


                            userLicenseNumber = slotBooking.getLicenseNumber() + " " + slotBooking.getLiceseCharacter();

                            checkPayment();


                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        visaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                payByVisa();
                goToHomePage();
            }
        });

    }


    private void checkPayment() {


        paymentRef.child(userLicenseNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                userPayment = snapshot.getValue(Payment.class);
                if (userPayment != null) {
                    stayedHourMinutes.setText(userPayment.getStayedHourTime() + "h" + " " + userPayment.getStayedMinutesTime() + "m");
                    amount.setText(userPayment.getTheAmount() + "$");
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

    public void payByVisa() {



        if (userPayment != null) {
            if (userPayment.getLicenseNumber() != null) {
                db.collection("UserBookingPayments").document(authUser.getUid()).
                        collection("UserPayment").document().set(userPayment);
            }




            FirebaseFirestore.getInstance()
                    .collection("UserBooking").document(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid()).collection("userReservationDocument")
                    .whereEqualTo("strBookingDate", userPayment.getDate())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ParkingSlotBooking slotBooking = document.toObject(ParkingSlotBooking.class);
                                DocumentReference reference =document.getReference();

                                if (slotBooking != null) {
                                    slotBooking.setReservationEnds(true);
                                }
                                WriteBatch batch = db.batch();
                                batch.update(reference, "reservationEnds",true);
                                batch.commit();

                            }

                        }
                    });

            PaymentNotification notification=new PaymentNotification();
            notification.setLicenseNumber(userLicenseNumber);
            notification.setSendNotification("OFF");

            notifyRef.child(userLicenseNumber).setValue(notification);


        }
    }


    private void goToHomePage() {
        startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}