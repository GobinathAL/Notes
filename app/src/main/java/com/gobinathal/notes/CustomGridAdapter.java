package com.gobinathal.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class CustomGridAdapter extends RecyclerView.Adapter<CustomGridAdapter.ViewHolder> {
    private List<Note> itemList;
    private Context mContext;

    public CustomGridAdapter(Context mContext, List<Note> itemList) {
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
        Note note = itemList.get(position);

        holder.title.setText(note.getTitle());
        holder.description.setText(note.getDescription());
        holder.docid.setText(note.getDocid());
        if(note.isFavorite())
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        else
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        holder.favorite.setTag(new Boolean(note.isFavorite()));
        if(note.isPinned())
            holder.pinned.setImageResource(R.drawable.ic_baseline_push_pin_24);
        else
            holder.pinned.setImageDrawable(null);
        holder.pinned.setTag(new Boolean(note.isPinned()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView root;
        MaterialTextView title, description, docid;
        ImageButton favorite;
        AppCompatImageView pinned;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);
            description = itemView.findViewById(R.id.item_description);
            docid = itemView.findViewById(R.id.item_docid);
            favorite = itemView.findViewById(R.id.item_favorite);
            favorite.setOnClickListener(NotesActivity.favoriteOnClickListener);
            pinned = itemView.findViewById(R.id.item_pin);
            root = itemView.findViewById(R.id.item_card_view);
            NotesActivity.cardArr.add(root);
            root.setOnClickListener(NotesActivity.noteOnClickListener);
            root.setOnLongClickListener(NotesActivity.noteOnLongClickListener);
        }
    }
}
