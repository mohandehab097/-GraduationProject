package com.example.smartparking;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class DeleteReservationDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button confirm, cancel;
    DatabaseReference userReservationReference;

    public DeleteReservationDialog(Activity a, DatabaseReference userReservationReference) {
        super(a);
        this.c = a;
        this.userReservationReference = userReservationReference;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_delete_reservation_dialog);
        confirm = (Button) findViewById(R.id.btn_confrim);
        cancel = (Button) findViewById(R.id.btn_cancel);


        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confrim:
                deleteReservation();
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;

            default:
                break;
        }

    }


    public void deleteReservation() {
        userReservationReference.removeValue();
    }


}
