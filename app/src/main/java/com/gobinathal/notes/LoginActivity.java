package com.gobinathal.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email, password;
    private MaterialButton loginButton;
    private MaterialTextView goToRegsitration;
    private FirebaseAuth auth;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, NotesActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);
        goToRegsitration = findViewById(R.id.register_prompt);
        goToRegsitration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegsitration.setTextColor(Color.parseColor("#0000ff"));
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if(email != null && password != null)
                    loginUser(txt_email, txt_password);
            }
        });
    }

    private void loginUser(String email, String password) {
        if((email != null && email.isEmpty()) || (password != null && password.isEmpty())){
            Toast.makeText(LoginActivity.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new MaterialAlertDialogBuilder(LoginActivity.this)
                .setView(R.layout.login_dialog)
                .setCancelable(false)
                .create();
        dialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startActivityForResult(new Intent(LoginActivity.this, NotesActivity.class), 1);
                        finish();
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Snackbar.make(findViewById(R.id.loginParentLayout), "Login Failed", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}