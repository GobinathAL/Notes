package com.gobinathal.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton deleteFab;
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
        deleteFab = null;
        if(bundle != null) {
            docid = bundle.getString("docid");
            inputTitle.setText(bundle.getString("title"));
            inputDescription.setText(bundle.getString("description"));
            deleteFab = findViewById(R.id.delete_fab);
            deleteFab.setVisibility(View.VISIBLE);
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
                onBackPressed();
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
                if(bundle != null) {
                    data.put("isFavorite", bundle.getBoolean("isFavorite"));
                    db.collection(FirebaseAuth.getInstance().getUid()).document(docid).update(data);
                }
                else {
                    data.put("isFavorite", false);
                    db.collection(FirebaseAuth.getInstance().getUid()).add(data);
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        if(deleteFab == null) return;
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(AddActivity.this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.collection(FirebaseAuth.getInstance().getUid()).document(docid).delete();
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }
}