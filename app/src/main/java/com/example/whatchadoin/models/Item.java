package com.example.whatchadoin.models;

public class Item {
    private String name;
    private int grocery;
    private boolean done;
    private String id;

    public Item() {

    }

    public Item(String name, int grocery, boolean done, String id) {
        this.name = name;
        this.grocery = grocery;
        this.done = done;
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrocery() {
        return grocery;
    }

    public void setGrocery(int grocery) {
        this.grocery = grocery;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
