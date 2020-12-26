package com.example.whatchadoin.models;

import java.util.ArrayList;

public class Task {
    private String name;
    private String date;
    private boolean isComplete;
    private boolean important;
    private ArrayList<Integer> tagId;

    public Task() {

    }

    public Task(String name, String date, boolean isComplete, boolean important, ArrayList<Integer> tag) {
        this.name = name;
        this.date = date;
        this.isComplete = isComplete;
        this.important = important;
        this.tagId = tag;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public ArrayList<Integer> getTagId() {
        return tagId;
    }

    public void setTagId(ArrayList<Integer> tagId) {
        this.tagId = tagId;
    }
}
