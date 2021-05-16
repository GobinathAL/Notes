package com.gobinathal.notes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class CustomGridAdapter extends RecyclerView.Adapter<CustomGridAdapter.ViewHolder> {
    private List<TodoItem> itemList;
    private Context mContext;

    public CustomGridAdapter(Context mContext, List<TodoItem> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = itemList.get(position);

        holder.title.setText(todoItem.getTitle());
        holder.description.setText(todoItem.getDescription());
        holder.docid.setText(todoItem.getDocid());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView root;
        MaterialTextView title, description, docid;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);
            description = itemView.findViewById(R.id.item_description);
            docid = itemView.findViewById(R.id.item_docid);
            root = itemView.findViewById(R.id.item_card_view);
            NotesActivity.cardArr.add(root);
            root.setOnClickListener(NotesActivity.noteOnClickListener);
            root.setOnLongClickListener(NotesActivity.noteOnLongClickListener);
        }
    }
}
