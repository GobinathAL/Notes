package com.gobinathal.notes;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton resetPasswordButton;
    private EditText forgotPasswordEmailInput;
    private TextInputLayout forgotPasswordEmailInputContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordEmailInputContainer = findViewById(R.id.forgot_password_email_input_container);
        forgotPasswordEmailInput = findViewById(R.id.forgot_password_email_input);

        resetPasswordButton = findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotPasswordEmailInput.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    forgotPasswordEmailInputContainer.setError("Invalid email");
                }
                else {
                    forgotPasswordEmailInputContainer.setError(null);
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
            }
        });
    }
}