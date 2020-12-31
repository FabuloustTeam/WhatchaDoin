package com.example.whatchadoin.ui.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatchadoin.Adapter.GroceryAdapter;
import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Grocery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroceryActivity extends AppCompatActivity {

    private RecyclerView txtShow;
    private EditText edtAdd;
    private int maxKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        txtShow = findViewById(R.id.txtShow);
        edtAdd = findViewById(R.id.edtAdd);
        txtShow.setLayoutManager(new LinearLayoutManager(this));
        db.getReference("grocery")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Grocery> groceries = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            groceries.add(data.getValue(Grocery.class));
                        }
                        txtShow.setAdapter(new GroceryAdapter(groceries));
                        txtShow.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GroceryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            String strName = edtAdd.getText().toString();
            if (!strName.isEmpty()) {
                Grocery item = new Grocery(strName);
                db.getReference("grocery")
                        .child(String.valueOf(maxKey + 1))
                        .setValue(item);
            }
        });
    }
}