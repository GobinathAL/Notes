package com.gobinathal.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gobinathal.notes.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.gobinathal.notes.Utils.SignIn.checkEmailOnFocusChanged;
import static com.gobinathal.notes.Utils.SignIn.verifyEnteredEmail;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkEmailOnFocusChanged(binding.forgotPasswordEmailInputContainer, binding.forgotPasswordEmailInput);

        binding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!verifyEnteredEmail(binding.forgotPasswordEmailInputContainer, binding.forgotPasswordEmailInput)) return;
                String email = binding.forgotPasswordEmailInput.getText().toString();
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