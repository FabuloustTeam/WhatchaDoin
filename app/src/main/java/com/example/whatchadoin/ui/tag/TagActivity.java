package com.example.whatchadoin.ui.tag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TagActivity extends AppCompatActivity {
    ListView lvList;
    ArrayAdapter<String> adapter;
    Button add;
    ArrayAdapter<String> adapterSearch;
    EditText inputsearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().hide();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lvList = findViewById(R.id.lvListTag);
        lvList.setAdapter(adapter);
        lvList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        add = findViewById(R.id.btnAdd);
        inputsearch=(EditText)findViewById(R.id.txtSearch);
        adapterSearch = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagActivity.this, AddTagActivity.class);
                startActivity(intent);
            }
        });

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = String.valueOf(adapter.getItem(position));
                String key = data.split(" ")[0];
                Intent intent = new Intent(TagActivity.this, UpdateTagActivity.class);
                intent.putExtra("KEY", key);
                startActivity(intent);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tag");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    Tag tagReceived = data.getValue(Tag.class);
                    String tagName = tagReceived.getName();
                    adapter.add(key + "    #" + tagName);
                    lvList.setFilterText(tagName);
                }
                inputsearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                        //(TagActivity.this).adapter.getFilter().filter(s.toString());
                        if(s.toString().isEmpty()) {
                            lvList.setAdapter(adapter);
                        } else {
                            adapterSearch.clear();
                            for(int i = 0; i < adapter.getCount(); i++) {
                                if(adapter.getItem(i).toLowerCase().contains(s.toString().toLowerCase())) {
                                    adapterSearch.add(adapter.getItem(i));
                                }
                            }
                            lvList.setAdapter(adapterSearch);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}