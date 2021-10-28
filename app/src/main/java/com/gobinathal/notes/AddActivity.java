package com.gobinathal.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gobinathal.notes.databinding.ActivityAddBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    
    private ActivityAddBinding binding;
    private Bundle bundle;
    private FirebaseFirestore db;
    private TodoItem todoItem;
    private String docid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.addTopToolbar.setTitle(null);
        setSupportActionBar(binding.addTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        if(bundle != null) {
            todoItem = (TodoItem) bundle.getParcelable("todoitem");
            docid = bundle.getString("docid");
            binding.inputTitle.setText(todoItem.getTitle());
            binding.inputDescription.setText(todoItem.getDescription());
            binding.deleteFab.setVisibility(View.VISIBLE);
        }
        binding.inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MenuItem save = binding.addTopToolbar.getMenu().findItem(R.id.add_menu_save);
                if(count == 0 && binding.inputDescription.getText().toString().isEmpty())
                    save.setVisible(false);
                else
                    save.setVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MenuItem save = binding.addTopToolbar.getMenu().findItem(R.id.add_menu_save);
                if(count == 0 && binding.inputTitle.getText().toString().isEmpty())
                    save.setVisible(false);
                else
                    save.setVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(binding.deleteFab == null) return;
        binding.deleteFab.setOnClickListener(new View.OnClickListener() {
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
            if(todoItem.isFavorite) {
                fav.setIcon(R.drawable.ic_baseline_favorite_24);
                fav.setContentDescription("true");
            }
            else {
                fav.setIcon(R.drawable.ic_baseline_favorite_border_24);
                fav.setContentDescription("false");
            }
            Log.i("AddActivity", "Pinned status " + todoItem.isPinned);
            if(todoItem.isPinned) {
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
                binding.addTopToolbar.getMenu().findItem(R.id.add_menu_save).setVisible(true);
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
                binding.addTopToolbar.getMenu().findItem(R.id.add_menu_save).setVisible(true);
                return true;
            case R.id.add_menu_save:
                String title = binding.inputTitle.getText().toString();
                String description = binding.inputDescription.getText().toString();
                boolean isFavorite = binding.addTopToolbar.getMenu().findItem(R.id.add_menu_favorite).getContentDescription().toString().equals("true");
                boolean isPinned = binding.addTopToolbar.getMenu().findItem(R.id.add_menu_pin).getContentDescription().toString().equals("true");
                TodoItem data = new TodoItem(title, description, isFavorite, isPinned);
                if(bundle != null) {
                    db.collection(FirebaseAuth.getInstance().getUid()).document(docid).set(data);
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