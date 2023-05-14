package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText email;
    TextView resetBtn,emailValidation,successMessage,toLogin;
    ProgressBar progressBar;
    ImageView errorIconEmail,successIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailValidation = findViewById(R.id.emailValidation);
        errorIconEmail = findViewById(R.id.error_icon_email);
        successMessage = findViewById(R.id.successSent);
        successIcon = findViewById(R.id.success_icon_email);
        email = findViewById(R.id.email_input);
        toLogin = findViewById(R.id.toLogin);
        progressBar = findViewById(R.id.progressBar);
        resetBtn = findViewById(R.id.resetBtn);
        auth = FirebaseAuth.getInstance();
        Animation translatebu= AnimationUtils.loadAnimation(ResetPasswordActivity.this, R.anim.push_down_in);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginPage();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emaill = email.getText().toString().trim();
                if (!emaill.isEmpty()) {
                    if (!isEmailValid(emaill)) {
                        emailValidation.setVisibility(View.VISIBLE);
                        errorIconEmail.setVisibility(View.VISIBLE);
                        emailValidation.setText("You Should Enter Valid Email !");
                        emailValidation.startAnimation(translatebu);
                        errorIconEmail.startAnimation(translatebu);
                        email.requestFocus();

                    }
                } else {
                    emailValidation.setVisibility(View.VISIBLE);
                    errorIconEmail.setVisibility(View.VISIBLE);
                    emailValidation.setText("Email is Required !");
                    emailValidation.startAnimation(translatebu);
                    errorIconEmail.startAnimation(translatebu);
                    email.requestFocus();
                }

                if (isEmailValid(emaill)&&!emaill.isEmpty()) {
                    emailValidation.setVisibility(View.GONE);
                    errorIconEmail.setVisibility(View.GONE);

                    auth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){

                                emailValidation.setVisibility(View.VISIBLE);
                                successMessage.setText("The Entered Email Not Found!");
                                errorIconEmail.setVisibility(View.VISIBLE);

                                emailValidation.startAnimation(translatebu);
                                errorIconEmail.startAnimation(translatebu);
                            }

                            else if (task.isSuccessful()){
                                successMessage.setVisibility(View.VISIBLE);
                                successMessage.setText("Sent Successfully ! Check Your Email to Reset Your Password");
                                successIcon.setVisibility(View.VISIBLE);


                                successMessage.startAnimation(translatebu);
                                successIcon.startAnimation(translatebu);
                            }

                            else{
                                successMessage.setVisibility(View.GONE);
                                successIcon.setVisibility(View.GONE);
                            }
                        }
                    });
                }


            }


        });

    }

    private  boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void goToLoginPage() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}