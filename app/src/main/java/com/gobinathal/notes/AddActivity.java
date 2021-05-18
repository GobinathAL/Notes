package com.gobinathal.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText inputTitle, inputDescription;
    private FirebaseFirestore db;
    private String docid;
    private Bundle bundle;
    private FloatingActionButton deleteFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        toolbar = findViewById(R.id.add_top_toolbar);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
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
                MenuItem save = toolbar.getMenu().findItem(R.id.add_menu_save);
                if(count == 0 && inputDescription.getText().toString().isEmpty())
                    save.setVisible(false);
                else
                    save.setVisible(true);
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
                MenuItem save = toolbar.getMenu().findItem(R.id.add_menu_save);
                if(count == 0 && inputTitle.getText().toString().isEmpty())
                    save.setVisible(false);
                else
                    save.setVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_add_app_bar, menu);

        MenuItem fav = menu.findItem(R.id.add_menu_favorite);
        MenuItem pin = menu.findItem(R.id.add_menu_pin);
        if(bundle != null) {
            Log.i("AddActivity", "edit mode");
            if(bundle.getBoolean("isFavorite")) {
                fav.setIcon(R.drawable.ic_baseline_favorite_24);
                fav.setContentDescription("true");
            }
            else {
                fav.setIcon(R.drawable.ic_baseline_favorite_border_24);
                fav.setContentDescription("false");
            }
            Log.i("AddActivity", "Pinned status " + bundle.getBoolean("isPinned"));
            if(bundle.getBoolean("isPinned")) {
                pin.setIcon(R.drawable.ic_unpin);
                pin.setTitle("Unpin");
                pin.setContentDescription("true");
            }
            else {
                pin.setIcon(R.drawable.ic_baseline_push_pin_24);
                pin.setTitle("Pin");
                pin.setContentDescription("false");
            }
        }
        else {
            fav.setIcon(R.drawable.ic_baseline_favorite_border_24);
            fav.setContentDescription("false");
            pin.setIcon(R.drawable.ic_baseline_push_pin_24);
            pin.setContentDescription("false");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_menu_favorite:
                if(item.getContentDescription().toString().equals("true")) {
                    item.setIcon(R.drawable.ic_baseline_favorite_border_24);
                    item.setContentDescription("false");
                }
                else {
                    item.setIcon(R.drawable.ic_baseline_favorite_24);
                    item.setContentDescription("true");
                }
                toolbar.getMenu().findItem(R.id.add_menu_save).setVisible(true);
                return true;
            case R.id.add_menu_pin:
                if(item.getContentDescription().toString().equals("true")) {
                    item.setIcon(R.drawable.ic_baseline_push_pin_24);
                    item.setContentDescription("false");
                }
                else {
                    item.setIcon(R.drawable.ic_unpin);
                    item.setContentDescription("true");
                }
                toolbar.getMenu().findItem(R.id.add_menu_save).setVisible(true);
                return true;
            case R.id.add_menu_save:
                String title = inputTitle.getText().toString();
                String description = inputDescription.getText().toString();
                boolean isFavorite = toolbar.getMenu().findItem(R.id.add_menu_favorite).getContentDescription().toString().equals("true");
                boolean isPinned = toolbar.getMenu().findItem(R.id.add_menu_pin).getContentDescription().toString().equals("true");
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("title", title);
                data.put("description", description);
                data.put("isFavorite", isFavorite);
                data.put("isPinned", isPinned);
                if(bundle != null) {
                    db.collection(FirebaseAuth.getInstance().getUid()).document(docid).update(data);
                }
                else {
                    db.collection(FirebaseAuth.getInstance().getUid()).add(data);
                }
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}