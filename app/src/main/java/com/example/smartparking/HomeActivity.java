package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.PaymentNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {


    TextView reservationDatBtn;
    TextView logout, reservationBtn, profileBtn;
    DatabaseReference paymentNotification;
    DatabaseReference rootRef;
    DatabaseReference slotRefernce;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String sendNotification = null;
    DatabaseReference slotRef;
    List<String> slotsAvailability;
    String userLicenseNumber = null;
    String date="";
    String uid;
    Date dateForMax;
    String today;
    DateFormat dateFormat;
    Date date2;
    Handler handler2;
    Handler handler;
    Boolean checkNearTime;
    Boolean checkLimitTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        reservationBtn = findViewById(R.id.goToReservation);
        reservationDatBtn = findViewById(R.id.reservationData);
        handler2 = new Handler(Looper.getMainLooper());
        handler = new Handler(Looper.getMainLooper());

        logout = findViewById(R.id.logoutBtn);
        profileBtn = findViewById(R.id.goToProfile);
        slotsAvailability = new ArrayList<>();



        if (authUser != null) {
            uid = authUser.getUid();
            rootRef = FirebaseDatabase.getInstance().getReference();
            slotRef = database.getReference("ParkingSlots");
            paymentNotification = rootRef.child("PaymentNotification");
            slotRefernce = rootRef.child("UsersSlotBooking");
        }

        rootRef.child("TimeNotification").child("limitTimeNotification")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                checkLimitTime = (Boolean) snapshot.getValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        rootRef.child("TimeNotification").child("nearTimeNotification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        checkNearTime = (Boolean) snapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        reservationDatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserReservationsPage();
            }
        });

        checkUserReservationForPayment();
        checkUserReservationForSlots();
        logout();
        goToProfileActivity();


        reservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(HomeActivity.this, ReservationActivity.class));
                overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);


            }
        });


    }


    public void slotNotification() {
        slotRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {

                    String parkingSlotsValue = data.getValue(String.class);
                    if (parkingSlotsValue != null && parkingSlotsValue.equals("on")) {
                        slotsAvailability.add(parkingSlotsValue);
                        break;
                    }


                }

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                Date date2 = Calendar.getInstance().getTime();

                String today = dateFormat.format(date2);
                if ((date != null && !date.equals(today))) {

                    if (!slotsAvailability.isEmpty() && slotsAvailability.size() == 1) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel("slotNotify", "slotNotify", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationChannel.setShowBadge(true);
                            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                        sendNotificationWhenSlotEmpty();

                    }
                }


                slotsAvailability.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUserReservationForPayment() {
        if (slotRefernce != null && uid != null) {
            slotRefernce.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                        if (slotBooking != null) {
                            if (slotBooking.getLicenseNumbersAndCharacter() != null) {

                                userLicenseNumber = slotBooking.getLicenseNumber() + " " + slotBooking.getLiceseCharacter();

                                checkPaymentNotification();


                            }
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void checkPaymentNotification() {


        if (paymentNotification != null) {
            if (userLicenseNumber != null) {
                paymentNotification.child(userLicenseNumber).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        PaymentNotification userPaymentNotification = snapshot.getValue(PaymentNotification.class);
                        if (userPaymentNotification != null) {
                            sendNotification = userPaymentNotification.getSendNotification();
                        }
                        if (sendNotification != null && sendNotification.equals("ON")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel("my_notify", "my_notify", NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationChannel.setShowBadge(true);
                                notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }
                            sendPaymentNotificationToUser();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void sendPaymentNotificationToUser() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_notify");
        builder.setContentTitle("RAKNII");
        builder.setContentText("You Should Pay Now");
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
        builder.setSmallIcon(R.mipmap.icon);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(HomeActivity.this);
        notificationManagerCompat.notify(1, builder.build());

    }

    private void logout() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, SecondActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

            }
        });
    }

    private void sendNotificationWhenSlotEmpty() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date2 = Calendar.getInstance().getTime();

        String today = dateFormat.format(date2);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "slotNotify");
        builder.setContentTitle("RAKNII");
        builder.setContentText("There Is an Empty slot,Book Today"+""+today);
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
        builder.setSmallIcon(R.mipmap.icon);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(HomeActivity.this);
        notificationManagerCompat.notify(1, builder.build());
    }

    private void goToProfileActivity() {
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
    }


//    private void goToReservation(){
//
//    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String str = intent.getStringExtra("noSlots");
        if (str != null && !str.isEmpty() && str.equals("ShowErrorDialog")) {
            ErrorDialog cdd = new ErrorDialog(HomeActivity.this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.show();
        }

    }

    private void goToUserReservationsPage() {
        startActivity(new Intent(HomeActivity.this, UserReservation.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void checkUserReservationForSlots() {
        if (slotRefernce != null && uid != null) {
            slotRefernce.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                        if (slotBooking != null) {
                            if (slotBooking.getLicenseNumbersAndCharacter() != null) {

                                date = slotBooking.getStrBookingDate();
                                String maxDate = slotBooking.getStrBookingDate() + " " + slotBooking.getLimittime();
                                String bookingDateTime = slotBooking.getStrBookingDate() + " " + slotBooking.getTime();
                                boolean arrived = slotBooking.isArrived();
                                slotNotification();
                                checkDateBeforeBookingWithFewMinutes(bookingDateTime);
                                checkDateNow(maxDate, arrived);

                            }
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void checkDateBeforeBookingWithFewMinutes(String bookingDateTime) {




        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                date2 = Calendar.getInstance().getTime();

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                String today = dateFormat.format(date2);

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH);


                LocalDateTime ldt = LocalDateTime.parse(bookingDateTime, dateFormatter);
                ldt = ldt.minusMinutes(30);



                if (today.equals(ldt.format(dateFormatter)) && !checkNearTime) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        rootRef.child("TimeNotification").child("nearTimeNotification").setValue(true);
                        NotificationChannel notificationChannel = new NotificationChannel("soon", "soon", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationChannel.setShowBadge(true);
                        notificationChannel.enableLights(true);
                        notificationChannel.enableVibration(true);
                        notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    sendNotificationWhenBookingTimeNear();
                    handler2.removeCallbacks(this);
                }
                handler2.postDelayed(this, 10 * 1000);
            }
        };

        runnable.run();
    }


    private void checkDateNow(String maxDate, boolean isArrived) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                dateForMax = Calendar.getInstance().getTime();

                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                today = dateFormat.format(dateForMax);
                if (maxDate.equals(today) && !isArrived&&!checkLimitTime) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        rootRef.child("TimeNotification").child("limitTimeNotification").setValue(true);
                        NotificationChannel notificationChannel = new NotificationChannel("limitNotifiy", "limitNotifiy", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationChannel.setShowBadge(true);
                        notificationChannel.enableLights(true);
                        notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    addLimitTimeNotification();
                    deleteUserBooking();
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, 10 * 1000);
            }
        };

        runnable.run();
    }


    private void sendNotificationWhenBookingTimeNear() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "soon");
        builder.setContentTitle("RAKNII");
        builder.setContentText("Your Time for Your parking Slot Is Very Close");
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
        builder.setSmallIcon(R.mipmap.icon);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        Intent intent = new Intent(this, CounterBookingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(HomeActivity.this);
        notificationManagerCompat.notify(1, builder.build());

    }


    private void addLimitTimeNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "limitNotifiy");
        builder.setContentTitle("RAKNII");
        builder.setContentText("Sorry! You Are late,Your Reservation Is Cancelled");
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
        builder.setSmallIcon(R.mipmap.icon);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(HomeActivity.this);
        notificationManagerCompat.notify(1, builder.build());

    }

    private void deleteUserBooking() {
        if (slotRefernce != null && uid != null) {
            slotRefernce.child(uid).removeValue();

        }

        FirebaseFirestore.getInstance()
                .collection("UserBooking").document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).collection("userReservationDocument")
                .whereEqualTo("reservationEnds", false)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            writeBatch.delete(snapshot.getReference());

                        }
                        writeBatch.commit();
                    }
                });

    }

}