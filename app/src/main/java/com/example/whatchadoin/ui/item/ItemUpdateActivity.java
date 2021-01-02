package com.example.whatchadoin.ui.item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.whatchadoin.R;

public class ItemUpdateActivity extends AppCompatActivity {

    private EditText etItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);

        String key = getIntent().getStringExtra("GROCERY_KEY");
        String name = getIntent().getStringExtra("GROCERY_NAME");
        etItemName = findViewById(R.id.etItemName);
        etItemName.setText(name);

    }
}