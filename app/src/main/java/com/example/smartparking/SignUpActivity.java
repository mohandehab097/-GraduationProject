package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartparking.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {


    FirebaseAuth auth;
    EditText username, email, password, confirmedPasssword, phoneNumber;
    TextView signUpbtn, toLoginBtn;
    ProgressBar progressBar;
    ImageView errorIconPassword, errorIconUsername, errorIconEmail, errorIconPhone, errorIconConfirmPassword, errorIconEmailExists;
    TextView emailValidation, passwordValidation, confirmPasswordValidation, phoneValidation, usernameValidation, emailExistsValidation;

    String strEmail, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        auth = FirebaseAuth.getInstance();

        emailValidation = findViewById(R.id.emailValidation);
        errorIconEmail = findViewById(R.id.error_icon);
        passwordValidation = findViewById(R.id.passwordValidation);
        errorIconPassword = findViewById(R.id.error_icon_password);
        usernameValidation = findViewById(R.id.usernameValidation);
        errorIconUsername = findViewById(R.id.error_icon_username);
        phoneValidation = findViewById(R.id.phoneValidation);
        errorIconPhone = findViewById(R.id.error_icon_phone);
        emailExistsValidation = findViewById(R.id.emailExistsValidation);
        errorIconEmailExists = findViewById(R.id.error_icon_emailExists);
        confirmPasswordValidation = findViewById(R.id.cofirmPasswordValidation);
        errorIconConfirmPassword = findViewById(R.id.error_icon_cpassword);
        username = findViewById(R.id.username_input);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.pass_input);
        confirmedPasssword = findViewById(R.id.pass2_input);
        phoneNumber = findViewById(R.id.phone_input);
        progressBar = findViewById(R.id.progressBar);
        toLoginBtn = findViewById(R.id.toLoginBtn);


        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginPage();
            }
        });

        confirmedPasssword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pass = password.getText().toString().trim();
                String confirmedPass = confirmedPasssword.getText().toString().trim();
                if (editable.length() > 0 && confirmedPass.length() > 0) {
                    if (!confirmedPass.equals(pass)) {

                        confirmPasswordValidation.setVisibility(View.VISIBLE);
                        errorIconConfirmPassword.setVisibility(View.VISIBLE);
                        confirmPasswordValidation.setText("Two Passwords Should Be Equal! !");

                        confirmedPasssword.requestFocus();


                    }

                } else {
                    confirmPasswordValidation.setVisibility(View.VISIBLE);
                    errorIconConfirmPassword.setVisibility(View.VISIBLE);
                    confirmPasswordValidation.setText("Confirmed Password is Required!");

                }

                if (confirmedPass.equals(pass) && confirmedPass.length() > 0) {
                    confirmPasswordValidation.setVisibility(View.GONE);
                    errorIconConfirmPassword.setVisibility(View.GONE);
                }
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

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phone = phoneNumber.getText().toString().trim();
                if (!phone.isEmpty()) {
                    if (!isPhoneVaild(phone)) {


                        phoneValidation.setVisibility(View.VISIBLE);
                        errorIconPhone.setVisibility(View.VISIBLE);
                        phoneValidation.setText("Please Enter Valid PhoneNumber !");

                        phoneNumber.requestFocus();

                    }
                } else {


                    phoneValidation.setVisibility(View.VISIBLE);
                    errorIconPhone.setVisibility(View.VISIBLE);
                    phoneValidation.setText("PhoneNumber is Required !");

                    phoneNumber.requestFocus();
                }

                if (!phone.isEmpty() && isPhoneVaild(phone)) {
                    phoneValidation.setVisibility(View.GONE);
                    errorIconPhone.setVisibility(View.GONE);
                }


            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass = password.getText().toString().trim();
                String confirmedPass = confirmedPasssword.getText().toString().trim();
                if (!confirmedPass.equals(pass)) {

                    confirmPasswordValidation.setVisibility(View.VISIBLE);
                    errorIconConfirmPassword.setVisibility(View.VISIBLE);
                    confirmPasswordValidation.setText("Two Passwords Should Be Equal! !");


                } else {

                    confirmPasswordValidation.setVisibility(View.GONE);
                    errorIconConfirmPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pass = password.getText().toString().trim();
                if (!pass.isEmpty()) {
                    if (pass.length() < 6) {


                        passwordValidation.setVisibility(View.VISIBLE);
                        errorIconPassword.setVisibility(View.VISIBLE);
                        passwordValidation.setText("Password Must Be At Least 6 Characters!");
                        password.requestFocus();


                    }
                } else {

                    passwordValidation.setVisibility(View.VISIBLE);
                    errorIconPassword.setVisibility(View.VISIBLE);
                    passwordValidation.setText("Password is Required !");
                    password.requestFocus();
                }

                if (!pass.isEmpty() && pass.length() > 5) {
                    passwordValidation.setVisibility(View.GONE);
                    errorIconPassword.setVisibility(View.GONE);
                }


            }
        });


        signUpbtn = findViewById(R.id.signup);
        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                isValid = validation();


                if (isValid) {
                    progressBar.setVisibility(View.VISIBLE);
                    String name = username.getText().toString().trim();
                    strEmail = email.getText().toString().trim();
                    String phone = phoneNumber.getText().toString().trim();
                    strPassword = password.getText().toString().trim();
                    auth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {

                                FirebaseUser authUser = auth.getCurrentUser();


                                User currUser = new User();
                                currUser.setUserId(authUser.getUid());
                                currUser.setUsername(name);
                                currUser.setEmail(authUser.getEmail());
                                currUser.setPhoneNumber(phone);
                                FirebaseDatabase.getInstance().getReference("Users").child(authUser.getUid()).setValue(currUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            goToLoginPage();

                                        } else {
                                            Toast.makeText(SignUpActivity.this, "User Registered Failed", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });


//                            updateUI(user);
                            } else {
                                try {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    Toast.makeText(SignUpActivity.this, "Weak Password", Toast.LENGTH_LONG).show();
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    emailExistsValidation.setVisibility(View.VISIBLE);
                                    errorIconEmailExists.setVisibility(View.VISIBLE);
                                    emailExistsValidation.setText("This Email Is Already Exists !");
                                    email.requestFocus();


                                    // TODO: Take your action
                                } catch (Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }



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
        finish();
        overridePendingTransition(R.anim.push_up_out, R.anim.push_up_in);

    }


    private boolean validation() {
        boolean valid = true;
        String name = username.getText().toString().trim();
        String emaill = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirmedPass = confirmedPasssword.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();

        valid = checkEmptyFields(name, username, "Username is Required!!");
        valid = checkEmptyFields(emaill, email, "Email is Required!!");
        valid = checkEmptyFields(pass, password, "password is Required!!");
        valid = checkEmptyFields(confirmedPass, confirmedPasssword, "ConfirmedPassword is Required!!");
        valid = checkEmptyFields(phone, phoneNumber, "PhoneNumber is Required!!");


        if (!isPhoneVaild(phone)) {
            phoneNumber.setError("Please Enter Vaild Phone Number!");
            phoneNumber.requestFocus();
            valid = false;
        }

        if (pass.length() < 6) {
            password.setError("Password Must Be At Least 6 Characters!");
            password.requestFocus();
            valid = false;

        }

        return valid;


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


    public void ShowHideConfirmPass(View view) {

        if (view.getId() == R.id.showHideConfirmpass) {
            if (confirmedPasssword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hidepassword);
                //Show Password
                confirmedPasssword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.showpassword);
                //Hide Password
                confirmedPasssword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private boolean checkEmptyFields(String value, EditText field, String message) {
        boolean valid = true;
        if (value.isEmpty()) {
            field.setError(message);
            field.requestFocus();
            valid = false;
        }

        return valid;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isPhoneVaild(String phonenumber) {
        String expression = "^01[0125][0-9]{8}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phonenumber);
        return matcher.matches();
    }




    private void goToLoginPage() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}