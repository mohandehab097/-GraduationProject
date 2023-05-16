package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.PaymentNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Animation bottomAnim;
    private ImageView icon;
    private TextView title;
    DatabaseReference rootRefernce;
    DatabaseReference slotRefernce;
    private final static int SPLASH_SCREEN = 3000;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    FirebaseDatabase databasee = FirebaseDatabase.getInstance();
    DatabaseReference slotReff = databasee.getReference("UsersSlotBooking");
    Handler handler;
    Handler handler2;
    String uid;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    String bookingUserName = null;
    String sendNotification = null;
    DatabaseReference notifyRef;
    String userLicenseNumber = null;
    Date date;
    Date date2;
    DateFormat dateFormat;
    String today;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        handler = new Handler(Looper.getMainLooper());
        handler2 = new Handler(Looper.getMainLooper());

        if (authUser != null) {
            uid = authUser.getUid();
            rootRef = FirebaseDatabase.getInstance().getReference();
            uidRef = rootRef.child("Users").child(uid);
            notifyRef = rootRef.child("PaymentNotification");
            rootRefernce = FirebaseDatabase.getInstance().getReference();
            slotRefernce = rootRef.child("UsersSlotBooking");
        }


        if (slotRefernce != null && uid != null) {
            slotRefernce.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                        if (slotBooking != null) {
                            if (slotBooking.getLicenseNumbersAndCharacter() != null) {

                                userLicenseNumber = slotBooking.getLicenseNumber() + " " + slotBooking.getLiceseCharacter();

                                checkNotification();

                                String maxDate = slotBooking.getStrBookingDate() + " " + slotBooking.getLimittime();
                                String bookingDateTime = slotBooking.getStrBookingDate() + " " + slotBooking.getTime();
                                boolean arrived = slotBooking.isArrived();
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

        createAnimation();
        goToSecondPage();


    }


    public void createAnimation() {


        //Animations

        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        icon = this.findViewById(R.id.logo);
        title = findViewById(R.id.title);
        final SpringAnimation springAnim = new SpringAnimation(icon, DynamicAnimation.TRANSLATION_Y, 0);
        Animation translatebu = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left_2);
        icon.startAnimation(translatebu);
        title.setAnimation(bottomAnim);

    }

    public void goToSecondPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                DatabaseReference userBooking = null;


                if (slotReff != null && uid != null) {
                    checkNotification();
                }
                if (uidRef != null && bookingUserName == null) {
                    goToHomePage();
                } else {
                    goToSecondActivityPage();
                }


            }
        }, SPLASH_SCREEN);
    }

    private void goToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    private void goToSecondActivityPage() {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToPaymentActivity() {
        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkNotification() {


        if (notifyRef != null) {
            if (userLicenseNumber != null) {
                notifyRef.child(userLicenseNumber).addValueEventListener(new ValueEventListener() {
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
                            addNotification();
                            goToPaymentActivity();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void addNotification() {

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
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
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
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(1, builder.build());

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
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(1, builder.build());

    }

    private void checkDateNow(String maxDate, boolean isArrived) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                date = Calendar.getInstance().getTime();

                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                today = dateFormat.format(date);
                if (maxDate.equals(today) && !isArrived) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                handler.postDelayed(this, 45 * 1000);
            }
        };

        runnable.run();
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


                if (today.equals(ldt.format(dateFormatter))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                handler2.postDelayed(this, 39 * 1000);
            }
        };

        runnable.run();
    }

    private void deleteUserBooking() {
        if (slotRefernce != null && uid != null) {
            slotRefernce.child(uid).removeValue();

        }

    }

}