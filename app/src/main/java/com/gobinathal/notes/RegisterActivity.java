package com.gobinathal.notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText email, password, confirmPassword;
    private TextInputLayout passwordContainer, confirmPasswordContainer;
    private MaterialCheckBox showPassword;
    private MaterialTextView registerHeader;
    private MaterialButton registerButton;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.register_prompt).setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        registerHeader = findViewById(R.id.login_register_header);
        registerHeader.setText("Register");
        findViewById(R.id.login_helper_text).setVisibility(View.GONE);
        email = findViewById(R.id.email);
        passwordContainer = findViewById(R.id.password_container);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordContainer = findViewById(R.id.confirm_password_container);
        confirmPasswordContainer.setVisibility(View.VISIBLE);
        showPassword = findViewById(R.id.show_password);
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showPassword.isChecked()) {
                    password.setTransformationMethod(null);
                    confirmPassword.setTransformationMethod(null);
                }
                else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                password.setSelection(password.getText().length());
                confirmPassword.setSelection(confirmPassword.getText().length());
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    verifyEnteredEmail();
                }
            }
        });
        registerButton = findViewById(R.id.button);
        registerButton.setText("Register");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b1 = verifyEnteredEmail();
                boolean b2 = verifyEnteredPass();
                boolean b3 = verifyEnteredConfirmPass();
                if(b1 && b2 && b3) {
                    String txt_email = email.getText().toString();
                    String txt_password = password.getText().toString();
                    registerUser(txt_email, txt_password);
                }
            }
        });
    }

    private boolean verifyEnteredEmail() {
        TextInputLayout emailContainer = findViewById(R.id.email_container);
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailContainer.setError("Invalid email");
            return false;
        }
        emailContainer.setError(null);
        return true;
    }

    private boolean verifyEnteredPass() {
        String txtPassword = password.getText().toString();
        if(txtPassword.length() < 8) {
            password.setError("Password should have atleast 8 characters");
            return false;
        }
        password.setError(null);
        return true;
    }
    private boolean verifyEnteredConfirmPass() {
        String txtConfirmPassword = confirmPassword.getText().toString();
        String txtPassword = password.getText().toString();
        if(!txtConfirmPassword.equals(txtPassword)) {
            confirmPasswordContainer.setError("Passwords do not match");
            return false;
        }
        confirmPasswordContainer.setError(null);
        return true;
    }
    private void registerUser(String txt_email, String txt_password) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, NotesActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}