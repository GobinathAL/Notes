package com.gobinathal.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
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
import java.util.HashMap;
import java.util.Map;

import static com.gobinathal.notes.Theme.Preferences.setPreferredTheme;

public class NotesActivity extends AppCompatActivity {

    private ViewStub stubGrid;
    private RecyclerView gvItems;
    private MaterialToolbar toolbar;
    private ArrayList<TodoItem> tasksArr = new ArrayList<TodoItem>(), currentNotes = new ArrayList<TodoItem>();
    public static ArrayList<MaterialCardView> cardArr = new ArrayList<MaterialCardView>();
    private FirebaseFirestore db;
    private CollectionReference cRef;
    private CustomGridAdapter gridAdapter;
    private ExtendedFloatingActionButton fab;
    private ListenerRegistration listener;
    private final int ADD_OR_DISCARD = 1;
    private final int EDIT_OR_DISCARD = 2;
    public static View.OnClickListener noteOnClickListener, favoriteOnClickListener;
    public static View.OnLongClickListener noteOnLongClickListener;
    private ActionMode mActionMode, currMode;
    private static int NO_OF_COLUMNS = 2;
    private  int SELECTED_COUNT;
    private SharedPreferences sharedPreferences;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        setPreferredTheme(this);
        sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        NO_OF_COLUMNS = sharedPreferences.getInt("ColumnCount", 0);
        NO_OF_COLUMNS = (NO_OF_COLUMNS == 0) ? 2 : NO_OF_COLUMNS;

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
                    ImageButton b = (ImageButton) v.findViewById(R.id.item_favorite);
                    AppCompatImageView pin = (AppCompatImageView) v.findViewById(R.id.item_pin);
                    Log.i("NotesActivity", b.toString() + " " + b.getDrawable().toString());
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("docid", docid);
                    intent.putExtra("isFavorite", (boolean) b.getTag());
                    intent.putExtra("isPinned", (boolean) pin.getTag());
                    Log.i("NotesActivity", title + " " + description + " " + docid);
                    startActivityForResult(intent, EDIT_OR_DISCARD);
                }
                else {
                    MaterialCardView cv = (MaterialCardView) v;
                    cv.setChecked(!cv.isChecked());
                    updateSelectedNotesCount();
                    currMode.invalidate();
                }
            }
        };
        noteOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MaterialCardView cv = (MaterialCardView) v;
                cv.setChecked(!cv.isChecked());
                if(mActionMode == null) {
                    mActionMode = toolbar.startActionMode(mActionModeCallback);
                }
                return true;
            }
        };

        favoriteOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton fav = (ImageButton) v;
                LinearLayout l= (LinearLayout) v.getParent();
                MaterialTextView docidView = l.findViewById(R.id.item_docid);
                String docid = docidView.getText().toString();

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("isFavorite", !(boolean) fav.getTag());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(FirebaseAuth.getInstance().getUid()).document(docid).update(data);
                Log.i("NotesActivity", ((boolean) fav.getTag()) + " ");
            }
        };
        Log.i("NotesActivity", "inflated stub");
        gvItems = findViewById(R.id.items_gridview);
        db = FirebaseFirestore.getInstance();
        cRef = db.collection(FirebaseAuth.getInstance().getUid());
        gvItems.setLayoutManager(new GridLayoutManager(NotesActivity.this, NO_OF_COLUMNS));
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
                        TodoItem todoItem = new TodoItem(d.getString("title"), d.getString("description"), d.getId(), d.getBoolean("isFavorite"), d.getBoolean("isPinned"));
                        tasksArr.add(todoItem);
                        Log.i("NotesActivity", todoItem.toString());
                    }
                    int index = 0;
                    for(int i = 0; i < tasksArr.size(); i++) {
                        TodoItem t = tasksArr.get(i);
                        if(!t.isPinned()) continue;
                        tasksArr.remove(t);
                        tasksArr.add(index, t);
                    }
                    if(tasksArr != null && tasksArr.size() > 0) {
                        currentNotes.clear();
                        currentNotes.addAll(tasksArr);
                        if(toolbar == null || !toolbar.hasExpandedActionView()) {
                            gvItems.setAdapter(gridAdapter);
                            return;
                        }
                        tasksArr.clear();
                        for(TodoItem t : currentNotes) {
                            String searchTerm = searchView.getQuery().toString().toLowerCase().trim();
                            if(t.getTitle().toLowerCase().contains(searchTerm)
                            || t.getDescription().toLowerCase().contains(searchTerm)) {
                                tasksArr.add(t);
                            }
                        }
                        gvItems.setAdapter(gridAdapter);
                    }
                }
            }
        });

        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

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
        super.onActivityResult(requestCode, resultCode, data);
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
            boolean hasUnpinnedNote = false;
            int count = 0;
            for(MaterialCardView cv : cardArr) {
                if(cv.isChecked()) count++;
                ImageButton fav = cv.findViewById(R.id.item_favorite);
                fav.setClickable(false);
                cv.setLongClickable(false);
                AppCompatImageView pin = cv.findViewById(R.id.item_pin);
                if(cv.isChecked() && !(boolean) pin.getTag()) hasUnpinnedNote = true;
            }
            Log.i("NotesActivity", "Unpinned note is checked");
            MenuItem item;
            Log.i("NotesActivity", menu.toString());
            item = menu.findItem(R.id.menu_pin);
            if(count == 0) {
                item.setVisible(false);
                return true;
            }
            item.setVisible(true);
            if(hasUnpinnedNote) {
                item.setIcon(R.drawable.ic_baseline_push_pin_24);
                item.setContentDescription("pin");
            }
            else {
                item.setIcon(R.drawable.ic_unpin);
                item.setContentDescription("unpin");
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_pin:
                    pinOrUnpinSelectedNotes(item);
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    deleteSelectedNotes();
                    return true;
                case R.id.menu_share:
                    mode.finish();
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
                ImageButton fav = cv.findViewById(R.id.item_favorite);
                fav.setClickable(true);
                if(cv.isChecked()) {
                    cv.setChecked(false);
                }
                cv.setLongClickable(true);
            }
        }
    };

    private void updateSelectedNotesCount() {
        int count = 0;
        for(MaterialCardView cv : cardArr) {
            if(cv.isChecked()) count++;
        }
        SELECTED_COUNT = count;
        currMode.setTitle(count + "/" + cardArr.size() + " selected");
    }

    private void deleteSelectedNotes() {
        new MaterialAlertDialogBuilder(NotesActivity.this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count = 0;
                        Log.i("NotesActivity", cardArr.size() + "");
                        for(MaterialCardView cv : cardArr) {
                            Log.i("NotesActivity", cv.isChecked() + "");
                            if(!cv.isChecked()) continue;
                            count++;
                            MaterialTextView docidView = cv.findViewById(R.id.item_docid);
                            String docid = docidView.getText().toString();
                            db.collection(FirebaseAuth.getInstance().getUid()).document(docid).delete();
                        }
                        Toast.makeText(NotesActivity.this, count + " notes deleted", Toast.LENGTH_SHORT).show();
                        currMode.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void pinOrUnpinSelectedNotes(MenuItem item) {
        Log.i("NotesActivity", "pinorunpin");
        Log.i("NotesActivity", item.getContentDescription().toString());
        if(item.getContentDescription().toString().equals("pin")) {
            Log.i("NotesActivity", "pin");
            for(MaterialCardView cv : cardArr) {
                if(!cv.isChecked()) continue;
                MaterialTextView docidView = cv.findViewById(R.id.item_docid);
                String docid = docidView.getText().toString();
                Map<String, Object> data  = new HashMap<String, Object>();
                data.put("isPinned", true);
                db.collection(FirebaseAuth.getInstance().getUid()).document(docid).update(data);
            }
            Toast.makeText(getApplicationContext(), "Pinned", Toast.LENGTH_SHORT).show();
        }
        else if(item.getContentDescription().toString().equals("unpin")) {
            Log.i("NotesActivity", "unpin");
            for(MaterialCardView cv : cardArr) {
                if(!cv.isChecked()) continue;
                MaterialTextView docidView = cv.findViewById(R.id.item_docid);
                String docid = docidView.getText().toString();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("isPinned", false);
                db.collection(FirebaseAuth.getInstance().getUid()).document(docid).update(data);
            }
            Toast.makeText(getApplicationContext(), "Unpinned", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                tasksArr.clear();
                tasksArr.addAll(currentNotes);
                gvItems.setAdapter(gridAdapter);
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchPattern = newText.toLowerCase().trim();
                tasksArr.clear();
                for(TodoItem t : currentNotes) {
                    if(t.getTitle().toLowerCase().contains(searchPattern) || t.getDescription().toLowerCase().contains(searchPattern)) {
                        tasksArr.add(t);
                    }
                }
                gvItems.setAdapter(gridAdapter);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivityForResult(new Intent(NotesActivity.this, SettingsActivity.class), 3);
                return true;
            case R.id.search:
                item.expandActionView();
                return true;
            case R.id.menu_increase_column:
                if(NO_OF_COLUMNS == 10) {
                    Toast.makeText(getApplicationContext(), "Columns cannot exceed 10", Toast.LENGTH_SHORT).show();
                    return true;
                }
                gvItems.setLayoutManager(new GridLayoutManager(NotesActivity.this, ++NO_OF_COLUMNS));
                sharedPreferences.edit().putInt("ColumnCount", NO_OF_COLUMNS).apply();
                return true;
            case R.id.menu_decrease_column:
                if(NO_OF_COLUMNS == 1) {
                    Toast.makeText(getApplicationContext(), "Columns cannot be 0", Toast.LENGTH_SHORT).show();
                    return true;
                }
                gvItems.setLayoutManager(new GridLayoutManager(NotesActivity.this, --NO_OF_COLUMNS));
                sharedPreferences.edit().putInt("ColumnCount", NO_OF_COLUMNS).apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}