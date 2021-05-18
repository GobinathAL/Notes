package com.gobinathal.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import static com.gobinathal.notes.Utils.SignIn.checkEmailOnFocusChanged;
import static com.gobinathal.notes.Utils.SignIn.registerUser;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredConfirmPass;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredEmail;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredPass;

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
        emailContainer = findViewById(R.id.email_container);
        email = findViewById(R.id.email);
        passwordContainer = findViewById(R.id.password_container);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordContainer = findViewById(R.id.confirm_password_container);
        registerButton = findViewById(R.id.button);
        showPassword = findViewById(R.id.show_password);

        goToLogin.setText("Existing user? Click here to login");
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        confirmPasswordContainer.setVisibility(View.VISIBLE);
        registerButton.setText("Register");

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHidePassword(showPassword, password, confirmPassword);
            }
        });

        checkEmailOnFocusChanged(emailContainer, email);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b1 = verifyEnteredEmail(emailContainer, email);
                boolean b2 = verifyEnteredPass(passwordContainer, password);
                boolean b3 = verifyEnteredConfirmPass(confirmPasswordContainer, confirmPassword, password);
                if(b1 && b2 && b3) {
                    String txt_email = email.getText().toString();
                    String txt_password = password.getText().toString();
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