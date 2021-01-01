package com.example.whatchadoin.ui.item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.whatchadoin.R;

public class ItemUpdate extends AppCompatActivity {
    private EditText etItemName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);
    }
}