package com.example.whatchadoin.ui.grocery;

import android.os.Bundle;
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

    private RecyclerView rvGroceryList;//recycler view mà đặt là txt à
    private EditText etAdd, etSearch; //edit text đặt là et
    private int maxKey;
    private ValueEventListener defaultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        rvGroceryList = findViewById(R.id.rvGroceryList);
        etAdd = findViewById(R.id.edtAdd);
        etSearch = findViewById(R.id.edtSearch);
        rvGroceryList.setLayoutManager(new LinearLayoutManager(this));
        defaultListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Logger.getLogger("DEBUG").warning("fired!");
                ArrayList<Grocery> groceries = new ArrayList<>();
                ArrayList<Integer> keys = new ArrayList<>();
                maxKey = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    groceries.add(data.getValue(Grocery.class));
                    int key = Integer.parseInt(data.getKey());
                    keys.add(key);
                    maxKey = key > maxKey ? key : maxKey;
                }
                rvGroceryList.setAdapter(new GroceryAdapter(groceries, keys));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroceryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        db.getReference("grocery")
                .addValueEventListener(defaultListener);
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String textSearch = etSearch.getText().toString();
            if (!textSearch.isEmpty()) {
                db.getReference("grocery")
                        .removeEventListener(defaultListener);
                ValueEventListener temp = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Grocery> groceries = new ArrayList<>();
                        ArrayList<Integer> keys = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            groceries.add(data.getValue(Grocery.class));
                            int key = Integer.parseInt(data.getKey());
                            keys.add(key);
                        }
                        rvGroceryList.setAdapter(new GroceryAdapter(groceries, keys));
                        db.getReference("grocery")
                                .removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GroceryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                };
                db.getReference("grocery")
                        .orderByChild("name")
                        .equalTo(textSearch)
                        .addValueEventListener(temp);
            } else {
                db.getReference("grocery")
                        .addValueEventListener(defaultListener);
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            String strName = etAdd.getText().toString();
            if (!strName.isEmpty()) {
                Grocery item = new Grocery(strName);
                db.getReference("grocery")
                        .child(String.valueOf(maxKey + 1))
                        .setValue(item);
            }
        });
    }
}