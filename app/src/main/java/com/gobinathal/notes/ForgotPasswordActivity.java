package com.gobinathal.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import static com.gobinathal.notes.Utils.SignIn.checkEmailOnFocusChanged;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredEmail;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton resetPasswordButton;
    private TextInputEditText forgotPasswordEmailInput;
    private TextInputLayout forgotPasswordEmailInputContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordEmailInputContainer = findViewById(R.id.forgot_password_email_input_container);
        forgotPasswordEmailInput = findViewById(R.id.forgot_password_email_input);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        checkEmailOnFocusChanged(forgotPasswordEmailInputContainer, forgotPasswordEmailInput);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!verifyEnteredEmail(forgotPasswordEmailInputContainer, forgotPasswordEmailInput)) return;
                String email = forgotPasswordEmailInput.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Check your mail for password reset", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else {
                                    Log.i("ForgotPasswordActivity", "Password reset mail can't be sent");
                                    Toast.makeText(getApplicationContext(), "Email address not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}