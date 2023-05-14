package com.example.smartparking;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button confirm, cancel;
    FirebaseUser authUser;
    ProgressBar progressBar;
    TextView confirmPasswordValidation,passwordValidation,wrongPasswordValidation,forgotPassword;
    EditText newPassword, confirmPassword,oldPassword;
    ImageView showHidePassword,showHideConfirmPassword,showHideOldPassword,errorIconConfirmPassword,errorIconPassword,errorIconWrongPassword;



    public ChangePasswordDialog(Activity a, FirebaseUser authUser) {
        super(a);
        this.c = a;
        this.authUser = authUser;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_password_dialog);
        confirm = (Button) findViewById(R.id.btn_confrim);
        cancel = (Button) findViewById(R.id.btn_cancel);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        showHideOldPassword=findViewById(R.id.showHideOldPass);
        showHidePassword=findViewById(R.id.showHidepass);
        showHideConfirmPassword=findViewById(R.id.showHideConfirmpass);
        confirmPasswordValidation = findViewById(R.id.cofirmPasswordValidation);
        errorIconConfirmPassword = findViewById(R.id.error_icon_cpassword);
        passwordValidation = findViewById(R.id.passwordValidation);
        errorIconPassword = findViewById(R.id.error_icon_password);
        progressBar = findViewById(R.id.progressBar);
        wrongPasswordValidation=findViewById(R.id.wrongPasswordValidation);
        errorIconWrongPassword=findViewById(R.id.wrongPasswordIcon);
        forgotPassword=findViewById(R.id.forgotPassword);
        forgotPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReserPasswordPage();
            }
        });

        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                wrongPasswordValidation.setVisibility(View.GONE);
                errorIconWrongPassword.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    checkPasswordOnTextChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkPasswordAfterPasswordChanged();

            }
        });


        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConfirmPassword(editable);

            }
        });
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        showHidePassword.setOnClickListener(this);
        showHideConfirmPassword.setOnClickListener(this);
        showHideOldPassword.setOnClickListener(this);

    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confrim:
                changePassword(v);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;

            case R.id.showHidepass:
                ShowHidePass(v);
                break;
            case R.id.showHideConfirmpass:
                ShowHideConfirmPass(v);
                break;
            case R.id.showHideOldPass:
                showHideOldPassword(v);
                break;

            default:
                break;
        }

    }

    public void ShowHidePass(View view) {


            if (newPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView)view).setImageResource(R.drawable.hidepassword);
                //Show Password
                newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.showpassword);
                //Hide Password
                newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }
    }


    public void ShowHideConfirmPass(View view) {

        if (view.getId() == R.id.showHideConfirmpass) {
            if (confirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hidepassword);
                //Show Password
                confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.showpassword);
                //Hide Password
                confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void showHideOldPassword(View view) {

        if (view.getId() == R.id.showHideOldPass) {
            if (oldPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hidepassword);
                //Show Password
                oldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.showpassword);
                //Hide Password
                oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private void changePassword(View view) {

        boolean isValid = true;
        isValid = validation();

        if (isValid&&checkEmptyFields(oldPassword.getText().toString().trim(), oldPassword, "Old Password is Required!!")) {
            final String email = authUser.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword.getText().toString().trim());

            authUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        authUser.updatePassword(newPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Snackbar snackbar_fail = Snackbar
                                            .make(view, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                    snackbar_fail.show();

                                }else {
                                    Snackbar snackbar_su = Snackbar
                                            .make(view, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                    snackbar_su.show();
                                    goToLoginPage();
                                }
                            }
                        });
                    }else {
                        wrongPasswordValidation.setVisibility(View.VISIBLE);
                        errorIconWrongPassword.setVisibility(View.VISIBLE);
                        forgotPassword.setVisibility(View.VISIBLE);
                    }
                }
            });

        }

    }

    private void goToLoginPage() {
        dismiss();
        Intent intent = new Intent(c, LoginActivity.class);
        c.startActivity(intent);
        c.finish();
        c.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goToReserPasswordPage() {
        dismiss();
        Intent intent = new Intent(c, ResetPasswordActivity.class);
        c.startActivity(intent);
        c.finish();
        c.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void checkConfirmPassword(Editable editable){

        String pass = newPassword.getText().toString().trim();
        String confirmedPass = confirmPassword.getText().toString().trim();
        if (editable.length() > 0 && confirmedPass.length() > 0) {
            if (!confirmedPass.equals(pass)) {

                confirmPasswordValidation.setVisibility(View.VISIBLE);
                errorIconConfirmPassword.setVisibility(View.VISIBLE);
                confirmPasswordValidation.setText("Two Passwords Should Be Equal! !");

                confirmPassword.requestFocus();


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


    private void checkPasswordOnTextChange() {


        String pass = newPassword.getText().toString().trim();
        String confirmedPass = confirmPassword.getText().toString().trim();
        if (!confirmedPass.equals(pass)) {

            confirmPasswordValidation.setVisibility(View.VISIBLE);
            errorIconConfirmPassword.setVisibility(View.VISIBLE);
            confirmPasswordValidation.setText("Two Passwords Should Be Equal! !");


        } else {

            confirmPasswordValidation.setVisibility(View.GONE);
            errorIconConfirmPassword.setVisibility(View.GONE);
        }

    }


    private void checkPasswordAfterPasswordChanged() {

        String pass = newPassword.getText().toString().trim();
        if (!pass.isEmpty()) {
            if (pass.length() < 6) {


                passwordValidation.setVisibility(View.VISIBLE);
                errorIconPassword.setVisibility(View.VISIBLE);
                passwordValidation.setText("Password Must Be At Least 6 Characters!");
                newPassword.requestFocus();


            }
        } else {

            passwordValidation.setVisibility(View.VISIBLE);
            errorIconPassword.setVisibility(View.VISIBLE);
            passwordValidation.setText("Password is Required !");
            newPassword.requestFocus();
        }

        if (!pass.isEmpty() && pass.length() > 5) {
            passwordValidation.setVisibility(View.GONE);
            errorIconPassword.setVisibility(View.GONE);
        }

    }



    private boolean validation() {
        boolean valid = true;

        String pass = newPassword.getText().toString().trim();
        String confirmedPass = confirmPassword.getText().toString().trim();

        valid = checkEmptyFields(pass, newPassword, "New Password is Required!!");
        valid = checkEmptyFields(confirmedPass, confirmPassword, "ConfirmedPassword is Required!!");


        if (pass.length() < 6) {
            newPassword.setError("Password Must Be At Least 6 Characters!");
            newPassword.requestFocus();
            valid = false;
        }

        return valid;


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






}
