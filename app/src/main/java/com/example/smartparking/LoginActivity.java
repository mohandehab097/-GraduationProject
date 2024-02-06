package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    FirebaseAuth auth;
    EditText email, password;
    TextView loginBtn, toResestPage,validationCredentials,emailValidation,toSignUpPage;
    ProgressBar progressBar;
    String checkEmail;
    String uid;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    String strEmail, strPassword;

    ImageView errorIconCredentials,errorIconEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_input);
        password = findViewById(R.id.pass_input);
        validationCredentials = findViewById(R.id.Credentials);
        errorIconCredentials = findViewById(R.id.error_icon_credentials);
        emailValidation = findViewById(R.id.emailValidation);
        errorIconEmail = findViewById(R.id.error_icon_email);
        progressBar = findViewById(R.id.progressBar);
        loginBtn = findViewById(R.id.loginBtn);
        toResestPage = findViewById(R.id.toResestPage);
        toSignUpPage=findViewById(R.id.toSignUpPage2);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
             uid = user.getUid();
             rootRef = FirebaseDatabase.getInstance().getReference();
             uidRef = rootRef.child("Users").child(uid);
        }


toSignUpPage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        goToSignUpPage();
    }
});

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emaill = email.getText().toString().trim();
                if (!emaill.isEmpty()) {
                    if (!isEmailValid(emaill)) {
                        emailValidation.setVisibility(View.VISIBLE);
                        errorIconEmail.setVisibility(View.VISIBLE);
                        emailValidation.setText("You Should Enter Valid Email !");

                        email.requestFocus();

                    }
                } else {
                    emailValidation.setVisibility(View.VISIBLE);
                    errorIconEmail.setVisibility(View.VISIBLE);
                    emailValidation.setText("Email is Required !");

                    email.requestFocus();
                }

                if (isEmailValid(emaill)) {
                    emailValidation.setVisibility(View.GONE);
                    errorIconEmail.setVisibility(View.GONE);
                }


            }
        });


        toResestPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToResetPage();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                isValid = userLoginValidation();
                if (isValid) {
                    progressBar.setVisibility(View.VISIBLE);
                    strEmail = email.getText().toString().trim();


                    strPassword = password.getText().toString().trim();

                    auth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

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
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {


                                    if (strEmail.equals("admin@gmail.com")) {
                                        goToAdminHomePage();
                                    }

                                    else{
                                        goToHomePage();
                                    }
                                }

                            } else {


                                validationCredentials.setVisibility(View.VISIBLE);
                                errorIconCredentials.setVisibility(View.VISIBLE);
                                validationCredentials.setText("Wrong Password !");
                                Animation translatebu= AnimationUtils.loadAnimation(LoginActivity.this, R.anim.push_down_in);
                                validationCredentials.startAnimation(translatebu);
                                errorIconCredentials.startAnimation(translatebu);
                                email.requestFocus();
                            }
                        }
                    });

                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent new_intent = new Intent(this, SecondActivity.class);

        this.startActivity(new_intent);

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void goToHomePage() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToAdminHomePage() {
        Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private boolean userLoginValidation() {

        boolean valid = true;
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (emailStr.isEmpty()) {
            email.setError("Please Enter The Email !");
            email.requestFocus();
            valid = false;
        }

        if (passwordStr.isEmpty()) {
            password.setError("Please Enter The Password !");
            password.requestFocus();
            valid = false;
        }

        return valid;


    }

    private void goToResetPage() {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToSignUpPage() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void ShowHidePass(View view) {

        if (view.getId() == R.id.showHidepass) {
            if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hidepassword);
                //Show Password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.showpassword);
                //Hide Password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

}