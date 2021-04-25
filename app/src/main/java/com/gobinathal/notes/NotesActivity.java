package com.gobinathal.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.function.Consumer;

public class NotesActivity extends AppCompatActivity {

    private ViewStub stubGrid;
    private GridView gvItems;
    private ImageButton settings;
    private MaterialCardView cardView;
    private TextInputEditText searchField;
    private ArrayList<TodoItem> tasksArr = new ArrayList<TodoItem>(), currentNotes = new ArrayList<TodoItem>();
    private FirebaseFirestore db;
    private DocumentReference dRef;
    private CollectionReference cRef;
    private CustomGridAdapter gridAdapter;
    private ExtendedFloatingActionButton fab;
    private ListenerRegistration listener;
    private final int ADD_OR_DISCARD = 1;
    private final int EDIT_OR_DISCARD = 2;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", 0);
        if(theme == 0)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if(theme == 1)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if(theme == 2)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_notes);

        stubGrid = findViewById(R.id.stub_grid);
        stubGrid.inflate();
        Log.i("NotesActivity", "inflated stub");
        gvItems = findViewById(R.id.items_gridview);
        db = FirebaseFirestore.getInstance();
        cardView = findViewById(R.id.item_card_view);
        cRef = db.collection(FirebaseAuth.getInstance().getUid());
        gridAdapter = new CustomGridAdapter(NotesActivity.this, R.layout.grid_item, tasksArr);
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
                    for(DocumentSnapshot d : todoList) {
                        TodoItem todoItem = new TodoItem(d.getString("title"), d.getString("description"), d.getId());
                        tasksArr.add(todoItem);
                        Log.i("fetch", todoItem.toString());
                    }
                    if(tasksArr != null && tasksArr.size() > 0) {
                        Log.i("NotesActivity", "before setadapter");
                        gvItems.setAdapter(gridAdapter);
                        currentNotes.clear();
                        currentNotes.addAll(tasksArr);
                        Log.i("NotesActivity", "after setadapter");
                    }
                }
            }
        });
        searchField = findViewById(R.id.search);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null) return;
                String searchText = s.toString().toLowerCase();
                Log.i("NotesActivity", s.toString() + " " + start + " " + before + " " + count);
                tasksArr.clear();
                tasksArr.addAll(currentNotes);
                if(count == 0 && tasksArr != null) {
                    gridAdapter.notifyDataSetChanged();
                    return;
                }
                ArrayList<TodoItem> hideList = new ArrayList<TodoItem>();
                for(TodoItem t : currentNotes) {
                    if(!t.getTitle().toLowerCase().contains(searchText) && !t.getDescription().toLowerCase().contains(searchText)) {
                        hideList.add(t);
                    }
                }
                tasksArr.removeAll(hideList);
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                intent.putExtra("docid", tasksArr.get(position).getDocid());
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
        Date d = getUTCdatetimeAsDate();
        Log.i("NotesActivity", getUTCdatetimeAsString() + "@" + d.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 3 && resultCode == RESULT_OK) {
            finish();
        }
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
    public static Date getUTCdatetimeAsDate() {
        return stringDateToDate(getUTCdatetimeAsString());
    }

    public static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date stringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }
}