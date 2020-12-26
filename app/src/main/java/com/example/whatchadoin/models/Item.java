package com.example.whatchadoin.models;

public class Item {
    private String name;
    private int groceryId;

    public Item() {

    }

    public Item(String name, int groceryId) {
        this.name = name;
        this.groceryId = groceryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroceryId() {
        return groceryId;
    }

    public void setGroceryId(int groceryId) {
        this.groceryId = groceryId;
    }
}
