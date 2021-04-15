package com.gobinathal.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    private MaterialButton saveButton;
    private ImageButton cancelButton;
    private TextInputEditText inputTitle, inputDescription;
    private FirebaseFirestore db;
    private String docid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Bundle bundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        cancelButton = findViewById(R.id.discard);
        saveButton = findViewById(R.id.save_button);
        inputTitle = findViewById(R.id.input_title);
        inputDescription = findViewById(R.id.input_description);
        if(bundle != null) {
            docid = bundle.getString("docid");
            inputTitle.setText(bundle.getString("title"));
            inputDescription.setText(bundle.getString("description"));
        }
        inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0 && inputDescription.getText().toString().isEmpty())
                    saveButton.setVisibility(View.GONE);
                else
                    saveButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0 && inputTitle.getText().toString().isEmpty())
                    saveButton.setVisibility(View.GONE);
                else
                    saveButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputTitle.getText().toString();
                String description = inputDescription.getText().toString();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("title", title);
                data.put("description", description);
                if(bundle != null)
                    db.collection(FirebaseAuth.getInstance().getUid()).document(docid).update(data);
                else
                    db.collection(FirebaseAuth.getInstance().getUid()).add(data);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}