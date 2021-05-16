package com.gobinathal.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class NotesActivity extends AppCompatActivity {

    private ViewStub stubGrid;
    private RecyclerView gvItems;
    private MaterialToolbar toolbar;
    private MaterialCardView cardView;
    private ArrayList<TodoItem> tasksArr = new ArrayList<TodoItem>(), currentNotes = new ArrayList<TodoItem>();
    public static ArrayList<MaterialCardView> cardArr = new ArrayList<MaterialCardView>();
    private FirebaseFirestore db;
    private DocumentReference dRef;
    private CollectionReference cRef;
    private CustomGridAdapter gridAdapter;
    private ExtendedFloatingActionButton fab;
    private ListenerRegistration listener;
    private final int ADD_OR_DISCARD = 1;
    private final int EDIT_OR_DISCARD = 2;
    public static View.OnClickListener noteOnClickListener;
    public static View.OnLongClickListener noteOnLongClickListener;
    private ActionMode mActionMode, currMode;
    private static int NO_OF_COLUMNS = 2;
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
        noteOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionMode == null) {
                    Intent intent = new Intent(NotesActivity.this, AddActivity.class);
                    String title = ((TextView) v.findViewById(R.id.item_title)).getText().toString();
                    String description = ((TextView) v.findViewById(R.id.item_description)).getText().toString();
                    String docid = ((TextView) v.findViewById(R.id.item_docid)).getText().toString();
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("docid", docid);
                    Log.i("NotesActivity", title + " " + description + " " + docid);
                    startActivityForResult(intent, EDIT_OR_DISCARD);
                }
                else {
                    MaterialCardView cv = (MaterialCardView) v;
                    cv.setChecked(!cv.isChecked());
                    updateSelectedNotesCount();
                }
            }
        };
        noteOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MaterialCardView cv = (MaterialCardView) v;
                cv.setChecked(!cv.isChecked());
                if(mActionMode == null) {
                    mActionMode = startSupportActionMode(mActionModeCallback);
                }
                return true;
            }
        };
        Log.i("NotesActivity", "inflated stub");
        gvItems = findViewById(R.id.items_gridview);
        db = FirebaseFirestore.getInstance();
        cardView = findViewById(R.id.item_card_view);
        cRef = db.collection(FirebaseAuth.getInstance().getUid());
        gvItems.setLayoutManager(new GridLayoutManager(NotesActivity.this, 2));
        gvItems.addItemDecoration(new SpaceItemDecoration(48, 24));
        gridAdapter = new CustomGridAdapter(NotesActivity.this, tasksArr);

        // Firestore database realtime listener
        listener = cRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.i("fetch", "failed to listen");
                    return;
                }
                if(value != null && !value.isEmpty()) {
                    ArrayList<DocumentSnapshot> todoList= (ArrayList<DocumentSnapshot>) value.getDocuments();
                    Log.i("NotesActivity", "fetched " + todoList.size());
                    cardArr.clear();
                    tasksArr.clear();
                    for(DocumentSnapshot d : todoList) {
                        TodoItem todoItem = new TodoItem(d.getString("title"), d.getString("description"), d.getId());
                        tasksArr.add(todoItem);
                        Log.i("NotesActivity", todoItem.toString());
                    }
                    if(tasksArr != null && tasksArr.size() > 0) {
                        currentNotes.clear();
                        currentNotes.addAll(tasksArr);
                        gvItems.setAdapter(gridAdapter);
//                        CharSequence searchTerm = searchField.getText().toString();
//                        startSearch(searchTerm, searchTerm.length());
                    }
                }
            }
        });

        toolbar = findViewById(R.id.top_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        startActivityForResult(new Intent(NotesActivity.this, SettingsActivity.class), 3);
                        return true;
                    case R.id.search:

                        return true;
                    default:
                        return false;
                }
            }
        });
        // Go to AddActivity when fab is clicked
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NotesActivity.this, AddActivity.class), ADD_OR_DISCARD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If user presses logout in SettingsActivity, go to login screen
        if(requestCode == 3 && resultCode == RESULT_OK) {
            finish();
        }
        else if(requestCode == EDIT_OR_DISCARD && resultCode == RESULT_OK) {
//            CharSequence s = searchField.getText().toString();
//            startSearch(s, s.length());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // Converts a string of the form TodoItem{title='SOME_TITLE', description='SOME_DESCRIPTION'} to TodoItem and returns it
    private TodoItem constructTodoItem(String s) {
        TodoItem todoItem = new TodoItem();
        String title = s.substring(s.indexOf("title=") + 6, s.indexOf(", de"));
        todoItem.setTitle(title);
        String description = s.substring(s.indexOf(", description=") + 14, s.indexOf("}"));
        todoItem.setDescription(description);
        return todoItem;
    }

    private void startSearch(CharSequence s, int count) {
        if(s == null) return;
        String searchText = s.toString().toLowerCase();
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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_app_bar, menu);
            currMode = mode;
            updateSelectedNotesCount();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_share:
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    return true;
                case R.id.menu_select_all:
                    for(MaterialCardView cv : cardArr)
                        cv.setChecked(true);
                    int n = cardArr.size();
                    currMode.setTitle(n + "/" + n + " selected");
                    return true;
                case R.id.menu_unselect_all:
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            currMode = null;
            for(MaterialCardView cv : cardArr) {
                if(cv.isChecked()) {
                    cv.setChecked(false);
                }
            }
        }
    };

    private void updateSelectedNotesCount() {
        int count = 0;
        for(MaterialCardView cv : cardArr) {
            if(cv.isChecked()) count++;
        }
        currMode.setTitle(count + "/" + cardArr.size() + " selected");
    }
}