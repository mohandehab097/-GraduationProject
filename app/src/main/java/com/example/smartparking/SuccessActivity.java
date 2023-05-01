package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent start = new Intent(SuccessActivity.this, CounterBookingActivity.class);
                startActivity(start);
                overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);

                finish();
            }
        }, 4000);
    }
}