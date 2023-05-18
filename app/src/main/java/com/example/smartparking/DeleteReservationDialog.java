package com.example.smartparking;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteReservationDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button confirm, cancel;
    DatabaseReference userReservationReference;
    Query userBookingQueryDelete;
    CollectionReference collectionReference;

    public DeleteReservationDialog(Activity a, DatabaseReference userReservationReference,Query userBookingQueryDelete,CollectionReference collectionReference) {
        super(a);
        this.c = a;
        this.userReservationReference = userReservationReference;
        this.userBookingQueryDelete = userBookingQueryDelete;
        this.collectionReference = collectionReference;
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



        userBookingQueryDelete.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        collectionReference.document(document.getId()).delete();
                    }
                } else {

                }
            }
        });

        if (userReservationReference != null) {
            userReservationReference.removeValue();
        }



    }


}
