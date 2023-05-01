package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.smartparking.models.ParkingSlots;
import com.example.smartparking.utils.SlotAdapter;
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

public class ShowParkingStatusActivity extends AppCompatActivity {

    RecyclerView rvSlots;
    DatabaseReference rootRef;
    DatabaseReference slotsRef;
    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_parking_status);
        radioGroup=findViewById(R.id.radioGroup);
        SlotAdapter adapter = new SlotAdapter();
        rvSlots = findViewById(R.id.rv_slots);
        rvSlots.setAdapter(adapter);

        rootRef = FirebaseDatabase.getInstance().getReference();
        slotsRef=rootRef.child("ParkingSlots");
        List<Map<String,String>> slots=new ArrayList<>();
        radioButton=findViewById(R.id.radioButtonAll);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton :
                        adapter.submitList(slots.stream().filter(map -> map.containsValue("on")).collect(Collectors.toList()));
                        break;
                    case R.id.radioButton2 :
                        adapter.submitList(slots.stream().filter(map -> map.containsValue("off")).collect(Collectors.toList()));
                        break;

                    case R.id.radioButtonAll:
                        adapter.submitList(slots);
                        break;
                }
            }
        });

        radioButton.setChecked(true);

        slotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slots.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String parkingSlotKey =data.getKey();
                    String parkingSlotsValue =data.getValue(String.class);
                    Map<String,String> map=new HashMap<>();
                    map.put(parkingSlotKey,parkingSlotsValue);
                    slots.add(map);
                }

                if(radioGroup.getCheckedRadioButtonId() == R.id.radioButtonAll) {
                    adapter.submitList(slots);

                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton) {
                    adapter.submitList(slots.stream().filter(map -> map.containsValue("on")).collect(Collectors.toList()));
                }
                else {
                    adapter.submitList(slots.stream().filter(map -> map.containsValue("off")).collect(Collectors.toList()));

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