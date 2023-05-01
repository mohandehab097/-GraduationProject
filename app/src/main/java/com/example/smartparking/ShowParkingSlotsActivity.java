package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ShowParkingSlotsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_parking_slots);
    }

    @Override
    public void onBackPressed() {

        Intent new_intent = new Intent(this, AdminHomeActivity.class);

        this.startActivity(new_intent);

        this.finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}