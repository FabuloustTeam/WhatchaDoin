package com.example.whatchadoin.ui.grocery;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.adapter.GroceryAdapter;
import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Grocery;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroceryActivity extends AppCompatActivity {

    private RecyclerView rvGroceryList;
    private EditText etAdd, etSearch;
    private Button addGrocery;
    private int maxKey;
    private ValueEventListener defaultListener;
    private ArrayList<Grocery> dataSet;
    private ArrayList<Integer> keys;
    private boolean isTheFirstTime = true;
    DatabaseReference groceryReference;
    Context context;
    GroceryAdapter groceryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        getSupportActionBar().hide();
        context = this;
        rvGroceryList = findViewById(R.id.rvGroceryList);
        etAdd = findViewById(R.id.etAddGrocery);
        etSearch = findViewById(R.id.etSearchGrocery);
        addGrocery = (Button) findViewById(R.id.btnAddGrocery);

        groceryReference = FirebaseDatabase.getInstance().getReference("grocery");
        groceryReference.addValueEventListener(new ValueEventListener() {
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

                rvGroceryList.setLayoutManager(new LinearLayoutManager(context));
                groceryAdapter = new GroceryAdapter(dataSet, keys, context);
                rvGroceryList.setAdapter(groceryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (!s.toString().trim().isEmpty()) {
                        String textSearch = s.toString();
                        search(textSearch);
                    }
                } else {
                    rvGroceryList.setAdapter(groceryAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addGrocery.setOnClickListener(v -> {
            String strName = etAdd.getText().toString();
            if (!strName.isEmpty()) {
                Grocery item = new Grocery(strName);
                groceryReference = FirebaseDatabase.getInstance().getReference("grocery");
                groceryReference.child(String.valueOf(maxKey + 1)).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etAdd.setText("");
                        etAdd.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    }
                });
            } else {
                Toast.makeText(context, "Please input grocery name", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void search(String textSearch) {
        if (textSearch.isEmpty()) {
            rvGroceryList.setAdapter(new GroceryAdapter(dataSet, keys, context));
            return;
        }
        ArrayList<Grocery> dataInRecyclerView = new ArrayList<>();
        ArrayList<Integer> keysInRecyclerView = new ArrayList<>();
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getName().toLowerCase().contains(textSearch.toLowerCase())) {
                dataInRecyclerView.add(dataSet.get(i));
                keysInRecyclerView.add(keys.get(i));
            }
        }
        rvGroceryList.setAdapter(new GroceryAdapter(dataInRecyclerView, keysInRecyclerView, context));
    }


}