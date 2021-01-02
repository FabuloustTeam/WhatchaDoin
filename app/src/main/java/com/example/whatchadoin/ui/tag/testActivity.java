package com.example.whatchadoin.ui.tag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.ui.home.TaskAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class testActivity extends AppCompatActivity {

    Context context;
    RecyclerView recyTask;
    DatabaseReference reference;
    ArrayList<Task> dataTask = new ArrayList<>();
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;

        recyTask = (RecyclerView) findViewById(R.id.recyclerTest);



    }

    private void loadTaskByTag() {
        reference = FirebaseDatabase.getInstance().getReference("task");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Dref = FirebaseDatabase.getInstance().getReference().child("task").child("tag");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task taskReceived = snapshot.getValue(Task.class);
                    if(taskReceived.getTag() != null) {
                        if (taskReceived.getTag().contains(Integer.parseInt(key))) {
                            dataTask.add(taskReceived);
                        }
                    }
                }
//                recyView.setLayoutManager(new LinearLayoutManager(getContext()));
//                taskAdapter = new TaskAdapter(this, dataTask, this);
//                recyView.setAdapter(taskAdapter);

                recyTask.setLayoutManager(new LinearLayoutManager(context));
//                taskAdapter = new TaskAdapter(context, dataTask, UpdateTagActivity.this::onTaskClick);
//                recyTask.setAdapter(taskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}