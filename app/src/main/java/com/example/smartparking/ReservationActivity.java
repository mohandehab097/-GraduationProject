package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartparking.models.ParkingSlotBooking;
import com.example.smartparking.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();

    EditText licenseCharacter, licenseNumbers, bookingDate, bookingTime;

    TextView bookingBtn, errorDateMessage, errorTimeMessage, errorCarNumberMessage, errorCarCharacterMessage;
    ImageView errorDateIcon, errorTimeIcon, errorCarCharacterIcon, errorCarNumberIcon;
    FirebaseDatabase databasee = FirebaseDatabase.getInstance();
    DatabaseReference slotReff = databasee.getReference("ParkingSlots");
    FirebaseAuth auth;
    FirebaseUser authUser;
    ParkingSlotBooking slotBooking;

    boolean validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        setContentView(R.layout.activity_reservation);
        bookingDate = findViewById(R.id.date);
        bookingTime = findViewById(R.id.time);
        licenseNumbers = findViewById(R.id.LicenseNumber_input2);
        licenseCharacter = findViewById(R.id.LicenseCharacter);
        errorCarCharacterIcon = findViewById(R.id.error_icon_car_character);
        errorCarCharacterMessage = findViewById(R.id.carCharacterValidation);
        errorCarNumberIcon = findViewById(R.id.error_icon_car_number);
        errorCarNumberMessage = findViewById(R.id.carNumberValidation);
        bookingBtn = findViewById(R.id.bookingBtn);
        errorDateMessage = findViewById(R.id.dateCredentials);
        errorTimeMessage = findViewById(R.id.timeCredentials);
        errorDateIcon = findViewById(R.id.error_date_icon_credentials);
        errorTimeIcon = findViewById(R.id.error_time_icon_credentials);
        validate = true;
        slotBooking = new ParkingSlotBooking();

        List<String> slotsAvailability = new ArrayList<>();


        slotReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot ds : snapshot.getChildren()) {

                    slotsAvailability.add(ds.getValue().toString());
                }
                List<String> fullParking = new ArrayList<>();


                boolean validBooking = false;

                for (String slots : slotsAvailability) {

                    if (slots.equals("on")) {

                        fullParking.add(slots);
                    }
                }

                slotsAvailability.clear();

                if (fullParking.size() == 0) {
                    Intent intent = new Intent(ReservationActivity.this, HomeActivity.class);
                    intent.putExtra("noSlots", "ShowErrorDialog");
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
                    fullParking.clear();
                    slotsAvailability.clear();

                }
                fullParking.clear();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        bookingTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String time = bookingTime.getText().toString();

                if (time.isEmpty()) {
                    errorTimeIcon.setVisibility(View.VISIBLE);
                    errorTimeMessage.setText("You Should Enter Booking Time");
                    errorTimeMessage.setVisibility(View.VISIBLE);
                }

                if (!time.isEmpty()) {
                    errorTimeMessage.setVisibility(View.GONE);
                    errorTimeIcon.setVisibility(View.GONE);
                    slotBooking.setTime(time);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        licenseCharacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String carCharacter = licenseCharacter.getText().toString();

                if (carCharacter.isEmpty()) {
                    errorCarCharacterIcon.setVisibility(View.VISIBLE);
                    errorCarCharacterMessage.setText("You Should Enter Your Car Character");
                    errorCarCharacterMessage.setVisibility(View.VISIBLE);
                }
                if (!carCharacter.isEmpty()) {
                    errorCarCharacterIcon.setVisibility(View.GONE);
                    errorCarCharacterMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        licenseNumbers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String carNumber = licenseNumbers.getText().toString();

                if (carNumber.isEmpty()) {
                    errorCarNumberMessage.setVisibility(View.VISIBLE);
                    errorCarNumberMessage.setText("You Should Enter Your Car Number");
                    errorCarNumberIcon.setVisibility(View.VISIBLE);
                }
                if (!carNumber.isEmpty()) {
                    errorCarNumberIcon.setVisibility(View.GONE);
                    errorCarNumberMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bookingDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String date = bookingDate.getText().toString();
                Date bookingDate = null;
                try {
                    bookingDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date.isEmpty()) {
                    errorDateIcon.setVisibility(View.VISIBLE);
                    errorDateMessage.setText("You Should Enter Booking Date");
                    errorDateMessage.setVisibility(View.VISIBLE);

                }

                if (!date.isEmpty()) {
                    errorDateMessage.setVisibility(View.GONE);
                    errorDateIcon.setVisibility(View.GONE);
                    slotBooking.setBookingDate(bookingDate);
                    slotBooking.setStrBookingDate(date);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(authUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User userProfile = dataSnapshot.getValue(User.class);

                        slotBooking.setUserId(userProfile.getUserId());
                        slotBooking.setUserName(userProfile.getUsername());


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });


        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String licenseNumber = licenseNumbers.getText().toString();
                String licenseCharacters = licenseCharacter.getText().toString();



                String licenseCharacterWithSpaces= licenseCharacters.replace("", " ").trim();
                String licenseNumbersWithSpaces= licenseNumber.replace("", " ").trim();
                String licenseNumbersAndCharacter = licenseNumbersWithSpaces+" "+licenseCharacterWithSpaces;


                validate = validateData();
                if (validate) {
                    slotBooking.setUserId(authUser.getUid());
                    slotBooking.setLicenseNumber(licenseNumbersWithSpaces);
                    slotBooking.setLiceseCharacter(licenseCharacterWithSpaces);
                    slotBooking.setLicenseNumbersAndCharacter(licenseNumbersAndCharacter);
                    showConfirmBookingDialog(slotBooking);
                }
            }
        });

        bookingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(ReservationActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        bookingTime.setText(selectedHour + ":" + selectedMinute);
                        slotBooking.setLimittime(selectedHour+2+":"+selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateLabel();
            }
        };


        bookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ReservationActivity.this, R.style.TimePickerTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        bookingDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private void showConfirmBookingDialog(ParkingSlotBooking slotBooking) {
        FirebaseDatabase.getInstance().getReference("incrementValue").setValue(ServerValue.increment(1));
        CustomDialog cdd = new CustomDialog(ReservationActivity.this, authUser, slotBooking,slotBooking.getStrBookingDate(),slotBooking.getTime(),slotBooking.getLicenseNumber(),slotBooking.getLiceseCharacter());
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        cdd.show();
    }

    private boolean validateData() {
        boolean validate = true;
        String date = bookingDate.getText().toString();
        String time = bookingTime.getText().toString();
        String carNumber = licenseNumbers.getText().toString();
        String carCharacter = licenseCharacter.getText().toString();
        if (date.isEmpty()) {
            errorDateIcon.setVisibility(View.VISIBLE);
            errorDateMessage.setText("You Should Enter Booking Date");
            errorDateMessage.setVisibility(View.VISIBLE);
            validate = false;
        }
        if (time.isEmpty()) {
            errorTimeMessage.setText("You Should Enter Booking Time");
            errorTimeIcon.setVisibility(View.VISIBLE);
            errorTimeMessage.setVisibility(View.VISIBLE);
            validate = false;
        }

        if (carNumber.isEmpty()) {
            errorCarNumberMessage.setVisibility(View.VISIBLE);
            errorCarNumberMessage.setText("You Should Enter Your Car Number");
            errorCarNumberIcon.setVisibility(View.VISIBLE);
            validate = false;
        }
        if (!carNumber.isEmpty()) {
            errorCarNumberIcon.setVisibility(View.GONE);
            errorCarNumberMessage.setVisibility(View.GONE);
        }

        if (carCharacter.isEmpty()) {
            errorCarCharacterIcon.setVisibility(View.VISIBLE);
            errorCarCharacterMessage.setText("You Should Enter Your Car Character");
            errorCarCharacterMessage.setVisibility(View.VISIBLE);
            validate = false;
        }
        if (!carCharacter.isEmpty()) {
            errorCarCharacterIcon.setVisibility(View.GONE);
            errorCarCharacterMessage.setVisibility(View.GONE);
        }

        if (!time.isEmpty()) {
            errorTimeIcon.setVisibility(View.GONE);
            errorTimeMessage.setVisibility(View.GONE);
        }

        if (!date.isEmpty()) {
            errorDateMessage.setVisibility(View.GONE);
            errorDateIcon.setVisibility(View.GONE);
        }

        return validate;

    }

    public boolean verifyAllEqualUsingALoop(List<String> list) {
        for (String s : list) {
            if (!s.equals(list.get(0)))
                return false;
        }
        return true;
    }
}