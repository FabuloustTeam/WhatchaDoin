package com.example.whatchadoin.ui.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ItemActivity extends AppCompatActivity implements ItemAdapter.OnItemListener {

    TextView groceryName;
    RecyclerView recyclerItem;
    EditText searchItem, addNameItem;
    ImageButton addItem;

    DatabaseReference reference;
    ArrayList<Item> listItems = new ArrayList<Item>();
    ItemAdapter itemAdapter;
    int max;
    Context context;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        ((AppCompatActivity) this).getSupportActionBar().hide();
        context = this;
        getElementsReady();

        key = getIntent().getStringExtra("GROCERY_KEY");
        String name = getIntent().getStringExtra("GROCERY_NAME");
        groceryName.setText(name);

        init();

    }

    private void getElementsReady() {
        groceryName = (TextView) findViewById(R.id.tvGroceryName);
        recyclerItem = (RecyclerView) findViewById(R.id.rvListItem);
        searchItem = (EditText) findViewById(R.id.etSearchItem);
        addNameItem = (EditText) findViewById(R.id.etAddNameItem);
        addItem = (ImageButton) findViewById(R.id.btnAddItem);
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        this.v = view;
//        init();
//
//    }


    public void init() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerItem.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference().child("item");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item itemReceived = dataSnapshot.getValue(Item.class);
                    if(itemReceived.getGrocery() == Integer.parseInt(key)) {
                        Item itemParsed = new Item();

                        String id = dataSnapshot.getKey();
                        String itemName = itemReceived.getName();
                        int groceryId = itemReceived.getGrocery();
                        boolean itemDone = itemReceived.isDone();

                        itemParsed.setId(id);
                        itemParsed.setName(itemName);
                        itemParsed.setDone(itemDone);
                        itemParsed.setGrocery(groceryId);

                        listItems.add(itemParsed);
                    }
                }
                recyclerItem.setLayoutManager(new LinearLayoutManager(context));
                itemAdapter = new ItemAdapter(context, listItems, ItemActivity.this::onItemClick);
                recyclerItem.setAdapter(itemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "No data", Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ItemUpdateActivity.class);
        intent.putExtra("KEY", String.valueOf(listItems.get(position).getId()));
        startActivity(intent);
    }

}
