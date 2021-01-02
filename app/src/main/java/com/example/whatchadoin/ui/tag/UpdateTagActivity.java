package com.example.whatchadoin.ui.tag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.ui.home.TaskAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;

public class UpdateTagActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {
    EditText tagname;
    Button update, delete;
    String key;
    ArrayList<Task> dataTask = new ArrayList<>();
    RecyclerView recyViewTask;
    Task listTasks;
    TaskAdapter taskAdapter;
    DatabaseReference myRef;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tag);
        context = this;
        tagname = (EditText) findViewById(R.id.txtName);
        update = (Button) findViewById(R.id.btnUpdate);
        delete = (Button) findViewById(R.id.btnDelete);
        recyViewTask = (RecyclerView) findViewById(R.id.recycleViewTasksByTag);

        Intent intent = getIntent();
        key = intent.getStringExtra("KEY");

        loadTaskByTag();

        showTagName();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTag();
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("tag");

                String Id = key;
                String sname = tagname.getText().toString();
                myRef.child(Id).child("name").setValue(sname);
                finish();
            }
        });
    }

    private void loadTaskByTag() {
        myRef = FirebaseDatabase.getInstance().getReference("task");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataTask.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task taskReceived = snapshot.getValue(Task.class);
                    if (taskReceived.getTag() != null) {
                        if (taskReceived.getTag().contains(Integer.parseInt(key))) {
                            dataTask.add(taskReceived);
                        }
                    }
                }
                recyViewTask.setLayoutManager(new LinearLayoutManager(context));
                taskAdapter = new TaskAdapter(context, dataTask, UpdateTagActivity.this::onTaskClick);
                recyViewTask.setAdapter(taskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteTag() {
        Intent intent = getIntent();
        DatabaseReference taskReference = FirebaseDatabase.getInstance().getReference("task");
        taskReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task tmpTask = snapshot.getValue(Task.class);
                    if (tmpTask.getTag() != null) {
                        if (tmpTask.getTag().contains(Integer.parseInt(key))) {
                            ArrayList<Integer> listTagId = tmpTask.getTag();
                            listTagId.remove(tmpTask.getTag().indexOf(Integer.parseInt(key)));
                            tmpTask.setId(Integer.parseInt(snapshot.getKey()));
                            tmpTask.setTag(listTagId);
//                            Task updatedTask = new Task();
//                            updatedTask.setTag(listTagId);
//
                            taskReference.child(String.valueOf(tmpTask.getId())).setValue(tmpTask);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference tagReference = FirebaseDatabase.getInstance().getReference("tag").child(key);
        tagReference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Delete tag successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTagName() {
        Intent intent = getIntent();
        final String key = intent.getStringExtra("KEY");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tag");
        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    tagname.setText(hashMap.get("name").toString());
                } catch (Exception e) {
                    Log.e("LOI_JSON", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LOI_CHITIET", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    public void onTaskClick(int position) {
    }
}