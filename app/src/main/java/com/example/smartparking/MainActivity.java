package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;
    private ImageView icon;
    private TextView title, secondTitle;
    String checkEmail;
    private final static int SPLASH_SCREEN = 3000;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser authUser = auth.getCurrentUser();
    FirebaseDatabase databasee = FirebaseDatabase.getInstance();
    DatabaseReference slotReff = databasee.getReference("UsersSlotBooking");
    String uid;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    String bookingUserName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (authUser != null) {
            uid = authUser.getUid();
            rootRef = FirebaseDatabase.getInstance().getReference();
            uidRef = rootRef.child("Users").child(uid);
        }
        createAnimation();
        goToSecondPage();
    }


    public void createAnimation() {


        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        icon = this.findViewById(R.id.logo);
        title = findViewById(R.id.title);
        final SpringAnimation springAnim = new SpringAnimation(icon, DynamicAnimation.TRANSLATION_Y, 0);
        Animation translatebu = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left_2);
        icon.startAnimation(translatebu);
        title.setAnimation(bottomAnim);
//        secondTitle.setAnimation(bottomAnim);
    }

    public void goToSecondPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                DatabaseReference userBooking = null;


                if(slotReff!=null) {
                    if(uid!=null){
                        slotReff.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ParkingSlotBooking slotBooking = snapshot.getValue(ParkingSlotBooking.class);

                                if (slotBooking != null) {
                                    if (slotBooking.getUserName() != null) {
                                        bookingUserName = slotBooking.getUserName();
                                        goToCounterPage();
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }


                    if (authUser != null&&bookingUserName==null) {
                        if (uidRef != null) {
                            uidRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User userProfile = snapshot.getValue(User.class);
                                    if (userProfile != null) {


                                        checkEmail = userProfile.getEmail();

                                        if (checkEmail.equals("admin@gmail.com")) {
                                            goToAdminHomePage();
                                        } else {
                                            goToHomePage();
                                        }


                                    }
                                    else{
                                        goToSecondActivityPage();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else{
                            goToSecondActivityPage();

                        }

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

    private void goToCounterPage() {
        Intent intent = new Intent(MainActivity.this, CounterBookingActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToAdminHomePage() {
        Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToSecondActivityPage() {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
        finish();
    }
}