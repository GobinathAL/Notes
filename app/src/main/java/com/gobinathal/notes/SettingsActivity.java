package com.gobinathal.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton goBack, twitter;
    private RadioGroup radioGroup;
    private MaterialButton logout, delete;
    private SharedPreferences sharedPreferences;
    private AlertDialog deleteDialog, loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", 0);
        if(theme == 0) {
            MaterialRadioButton b = findViewById(R.id.system_default);
            b.setChecked(true);
        }
        else if(theme == 1) {
            MaterialRadioButton b = findViewById(R.id.light_theme);
            b.setChecked(true);
        }
        else if(theme == 2) {
            MaterialRadioButton b = findViewById(R.id.dark_theme);
            b.setChecked(true);
        }

        goBack = findViewById(R.id.go_back_from_settings);
        radioGroup = findViewById(R.id.radio_group);
        twitter = findViewById(R.id.twitter);
        logout = findViewById(R.id.logout);
        delete = findViewById(R.id.delete);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.system_default) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt("Theme", 0);
                    editor.commit();
                }
                else if(checkedId == R.id.light_theme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt("Theme", 1);
                    editor.commit();
                }
                else if(checkedId == R.id.dark_theme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt("Theme", 2);
                    editor.commit();
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/gobinathal"));
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getApplicationContext().getSharedPreferences("ThemePref", 0).edit().clear().commit();
                Toast.makeText(SettingsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                setResult(RESULT_OK);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog = new MaterialAlertDialogBuilder(SettingsActivity.this)
                        .setView(R.layout.confirm_credentials_dialog)
                        .setTitle("Confirm Delete")
                        .setCancelable(false)
                        .setPositiveButton("Delete", null)
                        .setNegativeButton("Cancel", null)
                        .create();
                deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negativeButton = deleteDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextInputLayout emailContainer = ((AlertDialog) dialog).findViewById(R.id.email_container);
                                TextInputEditText email = ((AlertDialog) dialog).findViewById(R.id.email);
                                TextInputLayout passwordContainer = ((AlertDialog) dialog).findViewById(R.id.password_container);
                                TextInputEditText password = ((AlertDialog) dialog).findViewById(R.id.password);
                                if(!RegisterActivity.verifyEnteredEmail(emailContainer, email) || !RegisterActivity.verifyEnteredPass(passwordContainer, password)) return;

                                loadingDialog = new MaterialAlertDialogBuilder(SettingsActivity.this)
                                        .setView(R.layout.login_dialog)
                                        .setCancelable(false)
                                        .create();
                                loadingDialog.show();

                                String txt_email = email.getText().toString();
                                String txt_password = password.getText().toString();
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                AuthCredential credential = EmailAuthProvider.getCredential(txt_email, txt_password);
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    user.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    loadingDialog.dismiss();
                                                                    if(task.isSuccessful()) {
                                                                        Log.i("SettingsActivity", "Account Deleted");
                                                                        Toast.makeText(getApplicationContext(), "Account was deleted successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else {
                                                                        Log.i("SettingsActivity", "Account deletion failed");
                                                                        Toast.makeText(getApplicationContext(), "There was some problem in deleting your account. Try again later", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else {
                                                    Toast.makeText(SettingsActivity.this, "Invalid email / password. Logged out for security reasons. Account is not deleted", Toast.LENGTH_LONG).show();
                                                }
                                                getApplicationContext().getSharedPreferences("ThemePref", 0).edit().clear().commit();
                                                dialog.dismiss();
                                                loadingDialog.dismiss();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                                setResult(RESULT_OK);
                                                finish();
                                            }
                                        });
                            }
                        });

                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                deleteDialog.show();
            }
        });
    }
}