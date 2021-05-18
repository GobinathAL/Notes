package com.gobinathal.notes.Utils;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gobinathal.notes.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn {
    public static boolean verifyEnteredEmail(TextInputLayout emailContainer, TextInputEditText email) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailContainer.setError("Invalid email");
            return false;
        }
        emailContainer.setError(null);
        return true;
    }

    public static boolean verifyEnteredPass(TextInputLayout passwordContainer, TextInputEditText password) {
        String txtPassword = password.getText().toString();
        if(txtPassword.length() < 8) {
            passwordContainer.setError("Password should have atleast 8 characters");
            return false;
        }
        passwordContainer.setError(null);
        return true;
    }

    public static void checkEmailOnFocusChanged(TextInputLayout emailContainer, TextInputEditText email) {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    verifyEnteredEmail(emailContainer, email);
                }
            }
        });
    }

    public static boolean verifyEnteredConfirmPass(TextInputLayout confirmPassContainer, TextInputEditText confirmPass, TextInputEditText password) {
        String txtConfirmPassword = confirmPass.getText().toString();
        String txtPassword = password.getText().toString();
        if(!txtConfirmPassword.equals(txtPassword)) {
            confirmPassContainer.setError("Passwords do not match");
            return false;
        }
        confirmPassContainer.setError(null);
        return true;
    }

    public static void registerUser(AppCompatActivity activity, FirebaseAuth auth, String txt_email, String txt_password) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(activity, "Registration Success. Check your inbox to verify your email", Toast.LENGTH_SHORT).show();
                            sendVerificationEmail(activity);
                        } else {
                            Toast.makeText(activity, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public static void sendVerificationEmail(AppCompatActivity activity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();
                        }
                        else {
                            Log.i("RegisterActivity", "email verification message can't be send");
                            Toast.makeText(activity.getApplicationContext(), "Email can't be sent. Check the mail id.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
