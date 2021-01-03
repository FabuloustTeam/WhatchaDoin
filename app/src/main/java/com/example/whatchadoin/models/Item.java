package com.example.whatchadoin.models;

public class Item {
    private String name;
    private int grocery;

    public Item() {

    }

    public Item(String name, int groceryId) {
        this.name = name;
        this.grocery = groceryId;
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
}
