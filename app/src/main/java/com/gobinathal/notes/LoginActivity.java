package com.gobinathal.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.gobinathal.notes.Utils.SignIn.checkEmailOnFocusChanged;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredEmail;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredPass;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailContainer, passwordContainer;
    private TextInputEditText email, password;
    private MaterialButton loginButton;
    private MaterialCheckBox showPassword;
    private MaterialTextView goToRegsitration, forgotPassword;
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

        emailContainer = findViewById(R.id.email_container);
        email = findViewById(R.id.email);
        passwordContainer = findViewById(R.id.password_container);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);
        showPassword = findViewById(R.id.show_password);
        goToRegsitration = findViewById(R.id.register_prompt);
        forgotPassword = findViewById(R.id.forgot_password);

        checkEmailOnFocusChanged(emailContainer, email);

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showPassword.isChecked()) {
                    password.setTransformationMethod(null);
                }
                else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
                password.setSelection(password.getText().length());
            }
        });

        goToRegsitration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, ForgotPasswordActivity.class), 2);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyEnteredEmail(emailContainer, email) || !verifyEnteredPass(passwordContainer, password)) return;
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
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
                        dialog.dismiss();
                        checkIfEmailVerified();
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Snackbar.make(findViewById(R.id.loginParentLayout), "Login Failed. Check your credentials.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user.isEmailVerified()) {
            startActivityForResult(new Intent(LoginActivity.this, NotesActivity.class), 1);
            finish();
            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseAuth.getInstance().signOut();
            Snackbar.make(findViewById(R.id.loginParentLayout), "Verify your email to login!", Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}