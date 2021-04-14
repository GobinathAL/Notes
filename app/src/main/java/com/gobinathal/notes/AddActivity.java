package com.gobinathal.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    private MaterialButton cancelButton, saveButton;
    private TextInputEditText inputTitle, inputDescription;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = FirebaseFirestore.getInstance();
        inputTitle = findViewById(R.id.input_title);
        inputDescription = findViewById(R.id.input_description);
        cancelButton = findViewById(R.id.discard);
        saveButton = findViewById(R.id.save_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddActivity.this, NotesActivity.class));
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputTitle.getText().toString();
                String description = inputDescription.getText().toString();
                Map<String, String> data = new HashMap<String, String>();
                data.put("title", title);
                data.put("description", description);
                db.collection(FirebaseAuth.getInstance().getUid()).add(data);
                startActivity(new Intent(AddActivity.this, NotesActivity.class));
            }
        });
    }
}