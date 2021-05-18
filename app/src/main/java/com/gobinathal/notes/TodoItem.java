package com.gobinathal.notes;

public class TodoItem {
    private String title, description, docid;
    private boolean isFavorite, isPinned;

    public TodoItem() {
        this.title = "";
        this.description = "";
        this.docid = "";
        this.isFavorite = false;
        this.isPinned = false;
    }
    public TodoItem(String title, String description, String docid, boolean isFavorite, boolean isPinned) {
        this.title = title;
        this.description = description;
        this.docid = docid;
        this.isFavorite = isFavorite;
        this.isPinned = isPinned;
    }

    public String getTitle() {
        return title;
    }

    public String getDocid() {
        return docid;
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

    public void setDocid(String docid) {
        this.docid = docid;
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
}
