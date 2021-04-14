package com.gobinathal.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class CustomGridAdapter extends ArrayAdapter<TodoItem> {
    public CustomGridAdapter(@NonNull Context context, int resource, @NonNull List<TodoItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.grid_item, null);
        }
        TodoItem todoItem = getItem(position);
        MaterialTextView title = v.findViewById(R.id.item_title);
        MaterialTextView description = v.findViewById(R.id.item_description);
        title.setText(todoItem.getTitle());
        description.setText(todoItem.getDescription());
        return v;
    }
}
