package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartparking.models.User;
import com.example.smartparking.utils.SlotAdapter;
import com.example.smartparking.utils.UsersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowUsresActivity extends AppCompatActivity {

    RecyclerView rvUsers;
    DatabaseReference rootRef;
    DatabaseReference usersRef;
    List<String> users=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_usres);
        UsersAdapter adapter = new UsersAdapter();
        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setAdapter(adapter);
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef=rootRef.child("Users");


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {



                    User userProfile = data.getValue(User.class);

                    if(!userProfile.getUsername().equals("admin")){
                        users.add(userProfile.getUsername());
                        adapter.submitList(users);
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent new_intent = new Intent(this, AdminHomeActivity.class);

        this.startActivity(new_intent);

        this.finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

}