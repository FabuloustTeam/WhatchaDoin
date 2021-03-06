package com.example.whatchadoin.models;

import java.util.ArrayList;

public class UpdatedTask {
    private String name;
    private String date;
    private boolean completion;
    private boolean important;
    private ArrayList<Integer> tag;

    public UpdatedTask() {

    }

    public UpdatedTask(String name, String date, boolean completion, boolean important, ArrayList<Integer> tag) {
        this.name = name;
        this.date = date;
        this.completion = completion;
        this.important = important;
        this.tag = tag;
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

    public boolean isCompletion() {
        return completion;
    }

    public void setCompletion(boolean complete) {
        completion = complete;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public ArrayList<Integer> getTag() {
        return tag;
    }

    public void setTag(ArrayList<Integer> tag) {
        this.tag = tag;
    }
}
