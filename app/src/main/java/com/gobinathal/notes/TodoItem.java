package com.gobinathal.notes;

public class TodoItem {
    private String title, description, docid;

    public TodoItem() {
        this.title = "";
        this.description = "";
        this.docid = "";
    }
    public TodoItem(String title, String description, String docid) {
        this.title = title;
        this.description = description;
        this.docid = docid;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
