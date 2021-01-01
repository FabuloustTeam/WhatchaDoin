package com.example.whatchadoin.ui.item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Item;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {

    ArrayList<Item> allItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        String key = getIntent().getStringExtra("GROCERY_KEY");
        String name = getIntent().getStringExtra("GROCERY_NAME");


    }
}