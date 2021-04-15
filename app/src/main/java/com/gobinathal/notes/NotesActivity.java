package com.gobinathal.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.function.Consumer;

public class NotesActivity extends AppCompatActivity {

    private ViewStub stubGrid;
    private GridView gvItems;
    private ImageButton settings;
    ArrayList<TodoItem> tasksArr = new ArrayList<TodoItem>();
    ArrayList<String> documentId = new ArrayList<String>();
    FirebaseFirestore db;
    DocumentReference dRef;
    CollectionReference cRef;
    CustomGridAdapter gridAdapter;
    FloatingActionButton fab;
    private ListenerRegistration listener;
    private final int ADD_OR_DISCARD = 1;
    private final int EDIT_OR_DISCARD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        stubGrid = findViewById(R.id.stub_grid);
        stubGrid.inflate();
        gvItems = findViewById(R.id.items_gridview);
        db = FirebaseFirestore.getInstance();
        cRef = db.collection(FirebaseAuth.getInstance().getUid());
        listener = cRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.i("fetch", "failed to listen");
                    return;
                }
                if(value != null && !value.isEmpty()) {
                    ArrayList<DocumentSnapshot> todoList= (ArrayList<DocumentSnapshot>) value.getDocuments();
                    Log.i("fetch", "fetched " + todoList.size());
                    tasksArr.clear();
                    documentId.clear();
                    for(DocumentSnapshot d : todoList) {
                        TodoItem todoItem = new TodoItem(d.getString("title"), d.getString("description"));
                        tasksArr.add(todoItem);
                        documentId.add(d.getId());
                        Log.i("fetch", todoItem.toString());
                    }
                    if(tasksArr != null && tasksArr.size() > 0) {
                        gridAdapter = new CustomGridAdapter(getApplicationContext(), R.layout.grid_item, tasksArr);
                        gvItems.setAdapter(gridAdapter);
                    }
                }
            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NotesActivity.this, AddActivity.class), ADD_OR_DISCARD);
            }
        });
        gvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotesActivity.this, AddActivity.class);
                intent.putExtra("title", tasksArr.get(position).getTitle());
                intent.putExtra("description", tasksArr.get(position).getDescription());
                intent.putExtra("docid", documentId.get(position));
                startActivityForResult(intent, EDIT_OR_DISCARD);
            }
        });
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NotesActivity.this, SettingsActivity.class), 3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    private TodoItem constructTodoItem(String s) {
        TodoItem todoItem = new TodoItem();
        String title = s.substring(s.indexOf("title=") + 6, s.indexOf(", de"));
        todoItem.setTitle(title);
        String description = s.substring(s.indexOf(", description=") + 14, s.indexOf("}"));
        todoItem.setDescription(description);
        return todoItem;
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(NotesActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(NotesActivity.this, LoginActivity.class));
        finish();
    }
}