package com.example.whatchadoin.ui.item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.adapter.ItemAdapter;
import com.example.whatchadoin.models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity implements ItemAdapter.OnItemListener {

    TextView groceryName;
    RecyclerView recyclerItem;
    EditText searchItem, addNameItem;
    ImageButton addItem;

    DatabaseReference reference;
    ArrayList<Item> listItems = new ArrayList<Item>();
    ItemAdapter itemAdapter;
    int max = 0;
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

        loadItems();

        searchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyWords = s.toString();
                if (keyWords.isEmpty()) {
                    recyclerItem.setAdapter(itemAdapter);
                } else {
                    ArrayList<Item> searchResults = new ArrayList<Item>();
                    for (int i = 0; i < listItems.size(); i++) {
                        if (listItems.get(i).getName().toLowerCase().contains(keyWords)) {
                            searchResults.add(listItems.get(i));
                        }
                    }
                    ItemAdapter searchResultAdapter = new ItemAdapter(context, searchResults, ItemActivity.this::onItemClick);
                    recyclerItem.setAdapter(searchResultAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addNameItem.getText().toString().isEmpty()) {
                    if (addNameItem.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Please input item name", Toast.LENGTH_SHORT).show();
                    } else {
                        String newItemName = addNameItem.getText().toString();
                        Item newItem = new Item();
                        newItem.setName(newItemName);
                        newItem.setGrocery(Integer.parseInt(key));
                        newItem.setDone(false);

                        reference = FirebaseDatabase.getInstance().getReference("item");
                        reference.child(String.valueOf(max + 1)).setValue(newItem);

                        addNameItem.setText("");
                        closeKeyboard();
                    }
                } else {
                    Toast.makeText(context, "Please input item name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    private void getElementsReady() {
        groceryName = (TextView) findViewById(R.id.tvGroceryName);
        recyclerItem = (RecyclerView) findViewById(R.id.rvListItem);
        searchItem = (EditText) findViewById(R.id.etSearchItem);
        addNameItem = (EditText) findViewById(R.id.etAddNameItem);
        addItem = (ImageButton) findViewById(R.id.btnAddItem);
    }

    public void loadItems() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerItem.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference().child("item");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (max < Integer.parseInt(dataSnapshot.getKey())) {
                        max = Integer.parseInt(dataSnapshot.getKey());
                    }
                    Item itemReceived = dataSnapshot.getValue(Item.class);
                    if (itemReceived.getGrocery() == Integer.parseInt(key)) {
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
