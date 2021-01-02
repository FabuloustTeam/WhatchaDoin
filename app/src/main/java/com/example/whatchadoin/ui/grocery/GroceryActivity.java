package com.example.whatchadoin.ui.grocery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.Adapter.GroceryAdapter;
import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Grocery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.logging.Logger;

public class GroceryActivity extends AppCompatActivity {

    private RecyclerView rvGroceryList;
    private EditText etAdd, etSearch;
    private int maxKey;
    private ValueEventListener defaultListener;
    private ArrayList<Grocery> dataSet;
    private ArrayList<Integer> keys;
    private boolean isTheFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        rvGroceryList = findViewById(R.id.rvGroceryList);
        etAdd = findViewById(R.id.etAdd);
        etSearch = findViewById(R.id.etSearch);
        rvGroceryList.setLayoutManager(new LinearLayoutManager(this));
        defaultListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataSet = new ArrayList<>();
                keys = new ArrayList<>();
                maxKey = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    dataSet.add(data.getValue(Grocery.class));
                    int key = Integer.parseInt(data.getKey());
                    keys.add(key);
                    maxKey = key > maxKey ? key : maxKey;
                }
                if (isTheFirstTime) {
                    search("");
                    isTheFirstTime = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroceryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        db.getReference("grocery")
                .addValueEventListener(defaultListener);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textSearch = s.toString();
                search(textSearch);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            String strName = etAdd.getText().toString();
            if (!strName.isEmpty()) {
                Grocery item = new Grocery(strName);
                db.getReference("grocery")
                        .child(String.valueOf(maxKey + 1))
                        .setValue(item);
                etAdd.setText("");
                etAdd.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void search(String textSearch) {
        if (textSearch.isEmpty()) {
            rvGroceryList.setAdapter(new GroceryAdapter(dataSet, keys));
            return;
        }
        ArrayList<Grocery> dataInRecyclerView = new ArrayList<>();
        ArrayList<Integer> keysInRecyclerView = new ArrayList<>();
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getName().contains(textSearch)) {
                dataInRecyclerView.add(dataSet.get(i));
                keysInRecyclerView.add(keys.get(i));
            }
        }
        rvGroceryList.setAdapter(new GroceryAdapter(dataInRecyclerView, keysInRecyclerView));
    }
}