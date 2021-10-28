package com.gobinathal.notes;
/*
* This should not be used to pass notes in intent
* */
public class Note extends TodoItem{
    private String docid;

    public Note() {
        super();
        docid = "";
    }
    public Note(String title, String description, String docid, boolean isFavorite, boolean isPinned) {
        super(title, description, isFavorite, isPinned);
        this.docid = docid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }
}
