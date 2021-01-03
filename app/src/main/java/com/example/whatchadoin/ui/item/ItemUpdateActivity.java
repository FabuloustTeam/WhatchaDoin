package com.example.whatchadoin.ui.item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemUpdateActivity extends AppCompatActivity {

    private EditText itemName;
    Button updateItem;
    DatabaseReference reference;
    Item itemParsed;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);
        getSupportActionBar().hide();
        context = this;
        String key = getIntent().getStringExtra("KEY");
        itemName = (EditText) findViewById(R.id.etItemName);
        updateItem = (Button) findViewById(R.id.btnUpdateItem);

        reference = FirebaseDatabase.getInstance().getReference("item");
        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Item itemReceived = snapshot.getValue(Item.class);
                itemParsed = new Item();
                String name = itemReceived.getName();
                Boolean done = itemReceived.isDone();
                int grocery = itemReceived.getGrocery();

                itemParsed.setName(name);
                itemParsed.setDone(done);
                itemParsed.setGrocery(grocery);

                itemName.setText(itemParsed.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemName.getText().toString().isEmpty()) {
                    if(itemName.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Please input item name", Toast.LENGTH_SHORT).show();
                    } else {
                        itemParsed.setName(itemName.getText().toString().trim());
                        reference.child(key).setValue(itemParsed).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Update item successfully", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Please input item name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}