package com.gobinathal.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gobinathal.notes.databinding.ActivityLoginBinding;
import com.gobinathal.notes.databinding.CredentialsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.gobinathal.notes.Utils.SignIn.checkEmailOnFocusChanged;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredEmail;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredPass;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityLoginBinding binding;
    private CredentialsBinding credentialsBinding;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(
LoginActivity.this, NotesActivity.class));
            finish();
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        credentialsBinding = CredentialsBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());

        checkEmailOnFocusChanged(credentialsBinding.emailContainer, credentialsBinding.email);

        binding.registerPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, ForgotPasswordActivity.class), 2);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyEnteredEmail(credentialsBinding.emailContainer, credentialsBinding.email) || !verifyEnteredPass(credentialsBinding.passwordContainer, credentialsBinding.password)) return;
                String txt_email = credentialsBinding.email.getText().toString();
                String txt_password = credentialsBinding.password.getText().toString();
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