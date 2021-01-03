package com.example.whatchadoin.ui.grocery;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Grocery;
import com.google.firebase.database.FirebaseDatabase;

public class EditGroceryActivity extends AppCompatActivity {

    private EditText editGrocery;
    private Button saveGrocery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grocery);

        getSupportActionBar().hide();
        Context context = this;

        saveGrocery = (Button) findViewById(R.id.btnSaveGrocery);

        String key = getIntent().getStringExtra("GROCERY_KEY");
        String name = getIntent().getStringExtra("GROCERY_NAME");
        editGrocery = findViewById(R.id.etEditGrocery);
        editGrocery.setText(name);
        saveGrocery.setOnClickListener(v -> {
            if(!editGrocery.getText().toString().isEmpty()) {
                if(!editGrocery.getText().toString().trim().isEmpty()) {
                    String newName = editGrocery.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    db.getReference("grocery").child(key).setValue(new Grocery(newName)).addOnSuccessListener(i -> {
                        Toast.makeText(context, "Edit grocery successfully", Toast.LENGTH_LONG).show();
                        saveGrocery.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        finish();
                    });
                } else {
                    Toast.makeText(context, "Please input grocery name", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please input grocery name", Toast.LENGTH_LONG).show();
            }
        });
    }
}
