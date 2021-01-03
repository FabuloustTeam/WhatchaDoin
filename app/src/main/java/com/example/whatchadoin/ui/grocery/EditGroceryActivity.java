package com.example.whatchadoin.ui.grocery;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Grocery;
import com.google.firebase.database.FirebaseDatabase;

public class EditGroceryActivity extends AppCompatActivity {

    private EditText edtEditGrocery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grocery);

        getSupportActionBar().hide();
        Context context = this;

        String key = getIntent().getStringExtra("GROCERY_KEY");
        String name = getIntent().getStringExtra("GROCERY_NAME");
        edtEditGrocery = findViewById(R.id.etEditGrocery);
        edtEditGrocery.setText(name);
        findViewById(R.id.btnSaveGrocery).setOnClickListener(v -> {
            String newName = edtEditGrocery.getText().toString();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("grocery").child(key).setValue(new Grocery(newName)).addOnSuccessListener(i -> {
                Toast.makeText(context, "Edit grocery successfully", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }
}
