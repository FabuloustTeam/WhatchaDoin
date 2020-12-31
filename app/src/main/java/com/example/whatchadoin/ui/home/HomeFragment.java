package com.example.whatchadoin.ui.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.ui.tag.TagActivity;
import com.example.whatchadoin.ui.task.AddTaskActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.logging.Logger;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View v;
    View root;
    DatabaseReference reference;
    RecyclerView recyViewTasks;
    TaskAdapter taskAdapter;
    Button add, today, important;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        add = (Button) root.findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                intent.putExtra("", "add task");
                startActivity(intent);
            }
        });



        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        this.v=view;
        init();

    }


    public void init(){

        recyViewTasks = (RecyclerView) root.findViewById(R.id.recyclerViewTasks);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyViewTasks.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference().child("task");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Task> listTasks = new ArrayList<Task>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Task taskReceived = dataSnapshot.getValue(Task.class);
                    Task taskParsed = new Task();
                    String taskName = taskReceived.getName();
                    boolean taskComplete = taskReceived.isCompletion();
                    String taskDate = taskReceived.getDate();
                    ArrayList<Integer> taskTag = taskReceived.getTag();
                    boolean taskImportant = taskReceived.isImportant();
                    taskParsed.setName(taskName);
                    taskParsed.setCompletion(taskComplete);
                    taskParsed.setDate(taskDate);
                    taskParsed.setTag(taskTag);
                    taskParsed.setImportant(taskImportant);
                    listTasks.add(taskParsed);
                }
                recyViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                taskAdapter = new TaskAdapter(getContext(),listTasks);
                recyViewTasks.setAdapter(taskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "No data", Toast.LENGTH_LONG).show();
            }
        });

    }

}