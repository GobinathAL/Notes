package com.gobinathal.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email, password;
    private MaterialButton loginButton;
    private MaterialTextView goToRegsitration;
    private FirebaseAuth auth;
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
        loginButton.setText("Login");
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
                loginUser(txt_email, txt_password);
            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, NotesActivity.class));
                        finish();
                    }
                });

    }
}