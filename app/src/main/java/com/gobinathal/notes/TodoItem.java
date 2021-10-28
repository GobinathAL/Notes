package com.gobinathal.notes;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoItem implements Parcelable {
    protected String title, description;
    protected boolean isFavorite, isPinned;

    public TodoItem() {
        this.title = "";
        this.description = "";
        this.isFavorite = false;
        this.isPinned = false;
    }
    public TodoItem(String title, String description, boolean isFavorite, boolean isPinned) {
        this.title = title;
        this.description = description;
        this.isFavorite = isFavorite;
        this.isPinned = isPinned;
    }

    public TodoItem(Parcel parcel) {
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.isFavorite = (boolean) parcel.readValue(null);
        this.isPinned = (boolean) parcel.readValue(null);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isFavorite=" + isFavorite +
                ", isPinned=" + isPinned +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeValue(isFavorite);
        parcel.writeValue(isPinned);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new TodoItem(parcel);
        }

        @Override
        public Object[] newArray(int i) {
            return new TodoItem[i];
        }
    };
}
