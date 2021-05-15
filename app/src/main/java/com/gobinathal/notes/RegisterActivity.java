package com.gobinathal.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText email, password, confirmPassword;
    private TextInputLayout emailContainer, passwordContainer, confirmPasswordContainer;
    private MaterialCheckBox showPassword;
    private MaterialTextView registerHeader, goToLogin;
    private MaterialButton registerButton;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        registerHeader = findViewById(R.id.login_register_header);
        registerHeader.setText("Register");
        findViewById(R.id.forgot_password).setVisibility(View.GONE);
        goToLogin = findViewById(R.id.register_prompt);
        goToLogin.setText("Existing user? Click here to login");
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        emailContainer = findViewById(R.id.email_container);
        email = findViewById(R.id.email);
        passwordContainer = findViewById(R.id.password_container);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordContainer = findViewById(R.id.confirm_password_container);
        confirmPasswordContainer.setVisibility(View.VISIBLE);
        registerButton = findViewById(R.id.button);
        registerButton.setText("Register");
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

        checkEmailOnFocusChanged(emailContainer, email);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b1 = verifyEnteredEmail(emailContainer, email);
                boolean b2 = verifyEnteredPass(passwordContainer, password);
                boolean b3 = verifyEnteredConfirmPass();
                if(b1 && b2 && b3) {
                    String txt_email = email.getText().toString();
                    String txt_password = password.getText().toString();
                    registerUser(txt_email, txt_password);
                }
            }
        });
    }

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

    public static void checkEmailOnFocusChanged(TextInputLayout emailContainer, TextInputEditText email) {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    verifyEnteredEmail(emailContainer, email);
                }
            }
        });
    }
    private void registerUser(String txt_email, String txt_password) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Success. Check your inbox to verify your email", Toast.LENGTH_SHORT).show();
                            sendVerificationEmail();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                        else {
                            Log.i("RegisterActivity", "email verification message can't be send");
                            Toast.makeText(getApplicationContext(), "Email can't be sent. Check the mail id.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}