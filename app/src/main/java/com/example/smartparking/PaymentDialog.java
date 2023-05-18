package com.example.smartparking;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.Payment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class PaymentDialog extends Dialog implements View.OnClickListener {

    public Activity c;

    public Button confirm, cancel;
    DatabaseReference userReservationReference;
    Query userBookingQueryDelete;
    CollectionReference collectionReference;
    String date;
    TextView paymentDate,paymentMoney,payentStayedTime;

    public PaymentDialog(Activity a, String Date) {
        super(a);
        this.c = a;
        this.date = Date;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payment_dialog);
        paymentDate = findViewById(R.id.date);
        paymentMoney=findViewById(R.id.money);
        payentStayedTime=findViewById(R.id.time);









        FirebaseFirestore.getInstance()
                .collection("UserBookingPayments").document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).collection("UserPayment")
                .whereEqualTo("date",date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {


                            Payment payment = document.toObject(Payment.class);

                            if(payment!=null){
                                paymentDate.setText(payment.getDate().toString().trim());
                                paymentMoney.setText(payment.getTheAmount().toString().trim());
                                payentStayedTime.setText(payment.getStayedHourTime()+"h"+" "+payment.getStayedMinutesTime()+"m");

                            }

                            break;

                        }
                    }
                });







        cancel = (Button) findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_cancel:
                dismiss();
                break;

            default:
                break;
        }

    }


}
