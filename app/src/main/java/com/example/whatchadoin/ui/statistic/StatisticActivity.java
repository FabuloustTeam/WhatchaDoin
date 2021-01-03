package com.example.whatchadoin.ui.statistic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.adapter.TaskStatsAdapter;
import com.example.whatchadoin.models.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatisticActivity extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView recyclerCompleteTask, recyclerIncompleteTask, recyclerLateTask;
    TaskStatsAdapter adapterComplete, adapterIncomplete, adapterLate;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        context = this;
        recyclerCompleteTask = (RecyclerView) findViewById(R.id.recyclerComletedTasks);
        recyclerIncompleteTask = (RecyclerView) findViewById(R.id.recyclerIncomletedTasks);
        recyclerLateTask = (RecyclerView) findViewById(R.id.recyclerLateTasks);

        reference = FirebaseDatabase.getInstance().getReference().child("task");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Task> listCompleteTasks = new ArrayList<Task>();
                ArrayList<Task> listIncompleteTasks = new ArrayList<Task>();
                ArrayList<Task> listLateTasks = new ArrayList<Task>();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Task taskReceived = dataSnapshot.getValue(Task.class);
                    Task taskParsed = new Task();
                    String taskName = taskReceived.getName();
                    boolean taskComplete = taskReceived.isCompletion();
                    String taskDate = taskReceived.getDate();
                    taskParsed.setName(taskName);
                    taskParsed.setCompletion(taskComplete);
                    taskParsed.setDate(taskDate);
                    if(taskParsed.isCompletion()) {
                        listCompleteTasks.add(taskParsed);
                    } else {
                        try {
                            Date dateTask;
                            Date today = Calendar.getInstance().getTime();
                            dateTask = new SimpleDateFormat("dd/MM/yyyy").parse(taskParsed.getDate());
                            if(dateTask.before(today)) {
                                listLateTasks.add(taskParsed);
                            } else {
                                listIncompleteTasks.add(taskParsed);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                recyclerCompleteTask.setLayoutManager(new LinearLayoutManager(context));
                adapterComplete = new TaskStatsAdapter(context, listCompleteTasks);
                recyclerCompleteTask.setAdapter(adapterComplete);

                recyclerIncompleteTask.setLayoutManager(new LinearLayoutManager(context));
                adapterIncomplete = new TaskStatsAdapter(context, listIncompleteTasks);
                recyclerIncompleteTask.setAdapter(adapterIncomplete);

                recyclerLateTask.setLayoutManager(new LinearLayoutManager(context));
                adapterLate = new TaskStatsAdapter(context, listLateTasks);
                recyclerLateTask.setAdapter(adapterLate);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatisticActivity.this, "No data", Toast.LENGTH_LONG).show();
            }
        });
    }
}