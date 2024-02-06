package com.example.smartparking;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartparking.models.Incrementer;
import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button confirm, cancel;
    FirebaseUser authUser;
    ParkingSlotBooking slotBooking;
    TextView bookingDate, bookingTime, carNumber, carCharacter;
    String userBookingDate, userBookingTime, userCarNumber, userCarCharacter;
    Integer incrementValue = 0;
    Incrementer incrementer;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase databasee = FirebaseDatabase.getInstance();
    DatabaseReference slotReff = databasee.getReference("ParkingSlots");


    boolean checkReserved = false;


    public CustomDialog(Activity a, FirebaseUser authUser, ParkingSlotBooking slotBooking, String userBookingDate, String userBookingTime, String userCarNumber, String userCarCharacter) {
        super(a);
        // TODO Auto-generated constructor stub

        slotBooking.setArrived(false);
        slotBooking.setReservationEnds(false);
        this.c = a;
        this.authUser = authUser;
        this.slotBooking = slotBooking;
        this.userBookingDate = userBookingDate;
        this.userBookingTime = userBookingTime;
        this.userCarNumber = userCarNumber;
        this.userCarCharacter = userCarCharacter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        confirm = (Button) findViewById(R.id.btn_confrim);
        cancel = (Button) findViewById(R.id.btn_cancel);
        bookingDate = findViewById(R.id.date);
        bookingTime = findViewById(R.id.time);
        carCharacter = findViewById(R.id.carCharacter);
        carNumber = findViewById(R.id.carNumber);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        bookingDate.setText(userBookingDate);
        bookingTime.setText(userBookingTime);
        carCharacter.setText(userCarCharacter);
        carNumber.setText(userCarNumber);
        incrementer = new Incrementer();
        FirebaseDatabase.getInstance().getReference().child("incrementValue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incrementer.setIncrementValue(snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confrim:


                slotReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String date = bookingDate.getText().toString();
                        Date now = Calendar.getInstance().getTime();
                        DateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
                        String todayy = dateFormater.format(now);
                        Date bookingDatee = null;
                        try {
                            bookingDatee = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String parkingSlotKey;
                        String parkingSlotsValue;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            parkingSlotKey = ds.getKey();
                            parkingSlotsValue = ds.getValue(String.class);
                            if (checkReserved){
                                break;
                            }

                            else{
                                if (parkingSlotsValue.equals("on")) {
                                    if (date.equals(todayy)) {
                                        slotReff.child(parkingSlotKey).setValue("reserved");
                                        checkReserved = true;
                                    }
                                    break;
                                }
                            }



                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                db.collection("UserBooking").document(authUser.getUid()).collection("userReservationDocument").document().set(slotBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });


                FirebaseDatabase.getInstance().getReference("reservationLicense").child(incrementer.getIncrementValue().toString()).setValue(slotBooking.getLicenseNumbersAndCharacter()).addOnCompleteListener(new OnCompleteListener<Void>() {


                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                        } else {
                            Toast.makeText(c, "User Registered Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });


                FirebaseDatabase.getInstance().getReference("UsersSlotBooking").child(authUser.getUid()).setValue(slotBooking).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(c, SuccessActivity.class);
                            c.startActivity(intent);
                            c.finish();

                        } else {
                            Toast.makeText(c, "User Registered Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });


                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
