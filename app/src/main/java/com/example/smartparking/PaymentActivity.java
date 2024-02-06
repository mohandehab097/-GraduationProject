package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    DatabaseReference paymentRef;
    DatabaseReference notifyRef;
    DatabaseReference slotReff;
    DatabaseReference sucessPaymentRef;
    String uid;
    DatabaseReference uidRef;
    TextView stayedHourMinutes, amount;
    String userLicenseNumber = null;
    FirebaseAuth auth;
    FirebaseUser authUser;
    Button visaBtn;
    FirebaseFirestore db;
    Payment userPayment;
    Button payment;
    String SK = "sk_test_51N89EPFVe2Vg6v7wzG36Xm85TibZAwp8i1iUJcUgjZaHFraXXo8B4KPR2hzWbU2m1ZNyBya5T5L0HL9UW56tj8jp00NliH0fq9";
    String PK = "pk_test_51N89EPFVe2Vg6v7wBGVzNCTIsCJaf8wJuOFM0mKliaVOfLy61BbR40VlxD2bQzaIvA0OhdyYFAzRUfkY43RsikJv00IGIwzj0M";
    String CustomerId;
    String EphericalKey;
    String ClientSecret;
    PaymentSheet paymentSheet;
    String amountToPay;

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
        sucessPaymentRef = rootRef.child("SuccessPayment");
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








        PaymentConfiguration.init(this,PK);
        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });



        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            CustomerId = object.getString("id");
//                            Toast.makeText(PaymentActivity.this,CustomerId,Toast.LENGTH_SHORT).show();
                            getEphericalKey();


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SK);

                return header;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);










        visaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paymentFlow();

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

                    amountToPay= userPayment.getTheAmount().toString();
                    amount.setText(userPayment.getTheAmount() + "EGP");
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





        sucessPaymentRef.child(userLicenseNumber).setValue(false);

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


    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret,new PaymentSheet.Configuration("Adham Bakrii",new PaymentSheet.CustomerConfiguration(
                CustomerId,
                EphericalKey
        )));
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            getEphericalKey();
            Toast.makeText(this,"Payment Success",Toast.LENGTH_SHORT).show();
            SuccessPaymentDialog cdd = new SuccessPaymentDialog(PaymentActivity.this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.show();
            payByVisa();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent start = new Intent(PaymentActivity.this, HomeActivity.class);
                    startActivity(start);
                    overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);

                    finish();
                }
            }, 4000);
        }
    }

    private void getEphericalKey() {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
//                            Toast.makeText(PaymentActivity.this,CustomerId,Toast.LENGTH_SHORT).show();
                            getClientSecret(CustomerId,EphericalKey);


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SK);
                header.put("Stripe-Version","2022-11-15");
                return header;
            }
            @Nullable
            @Override
            protected Map<String,String>getParams()throws AuthFailureError{
                Map<String,String>params=new HashMap<>();
                params.put("customer",CustomerId);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void getClientSecret(String customerId, String ephericalKey) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret= object.getString("client_secret");

//                            Toast.makeText(PaymentActivity.this,ClientSecret,Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SK);
                return header;
            }
            @Nullable
            @Override
            protected Map<String,String>getParams()throws AuthFailureError{
                Map<String,String>params=new HashMap<>();
                params.put("customer",CustomerId);
                params.put("amount",amountToPay+"00");
                params.put("currency","EGP");
                params.put("automatic_payment_methods[enabled]","true");

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

}