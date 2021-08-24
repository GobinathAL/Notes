package com.gobinathal.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gobinathal.notes.databinding.ActivityLoginBinding;
import com.gobinathal.notes.databinding.CredentialsBinding;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import static com.gobinathal.notes.Utils.SignIn.checkEmailOnFocusChanged;
import static com.gobinathal.notes.Utils.SignIn.registerUser;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredConfirmPass;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredEmail;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredPass;

public class RegisterActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CredentialsBinding credentialsBinding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        credentialsBinding = CredentialsBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        binding.loginRegisterHeader.setText("Register");
        binding.loginHelperText.setText("Enter credentials for your new account");
        findViewById(R.id.forgot_password).setVisibility(View.GONE);

        binding.registerPrompt.setText("Existing user? Click here to login");
        binding.registerPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.confirmPasswordContainer.setVisibility(View.VISIBLE);
        binding.button.setText("Register");

        checkEmailOnFocusChanged(credentialsBinding.emailContainer, credentialsBinding.email);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b1 = verifyEnteredEmail(credentialsBinding.emailContainer, credentialsBinding.email);
                boolean b2 = verifyEnteredPass(credentialsBinding.passwordContainer, credentialsBinding.password);
                boolean b3 = verifyEnteredConfirmPass(binding.confirmPasswordContainer, binding.confirmPassword, credentialsBinding.password);
                if(b1 && b2 && b3) {
                    String txt_email = credentialsBinding.email.getText().toString();
                    String txt_password = credentialsBinding.password.getText().toString();
                    registerUser(RegisterActivity.this, auth, txt_email, txt_password);
                }
            }
        });
    }

    private static void showOrHidePassword(MaterialCheckBox showPasswordCheckbox, TextInputEditText password, TextInputEditText confirmPassword) {
        if(showPasswordCheckbox.isChecked()) {
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
}