package com.example.whatchadoin.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.adapter.TaskAdapter;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.ui.task.AddTaskActivity;
import com.example.whatchadoin.ui.task.UpdateTaskActivity;
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

public class HomeFragment extends Fragment implements TaskAdapter.OnTaskListener {

    private HomeViewModel homeViewModel;
    View v;
    View root;
    DatabaseReference reference;
    RecyclerView recyViewTasks;
    TaskAdapter taskAdapter, todayTaskAdapter, importantTaskAdapter;
    Button add;
    EditText searchTask;
    Spinner typeTask;
    ArrayList<Task> listTasks = new ArrayList<Task>();
    ArrayList<Task> listTodayTasks = new ArrayList<Task>();
    ArrayList<Task> listImportantTasks = new ArrayList<Task>();
    String selectedTypeTask = "All tasks";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        add = (Button) root.findViewById(R.id.btnAddGrocery);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                startActivity(intent);
            }
        });

        searchTask = (EditText) root.findViewById(R.id.etSearchTask);
        searchTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchTask(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        typeTask = (Spinner) root.findViewById(R.id.spinnerTypeTask);
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.type_task));
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeTask.setAdapter(spnAdapter);
        typeTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedTypeTask = adapterView.getItemAtPosition(position).toString();
                if(selectedTypeTask.equals("All tasks")) {
                    recyViewTasks.setAdapter(taskAdapter);
                } else if (selectedTypeTask.equals("Today")) {
                    recyViewTasks.setAdapter(todayTaskAdapter);
                } else if(selectedTypeTask.equals("Important")) {
                    recyViewTasks.setAdapter(importantTaskAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v = view;
        init();

    }


    public void init() {

        recyViewTasks = (RecyclerView) root.findViewById(R.id.recyclerViewTasks);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyViewTasks.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference().child("task");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTasks.clear();
                listTodayTasks.clear();
                listImportantTasks.clear();
                Date dateTask;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date today = calendar.getTime();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task taskReceived = dataSnapshot.getValue(Task.class);
                    Task taskParsed = new Task();
                    int id = Integer.parseInt(dataSnapshot.getKey());
                    String taskName = taskReceived.getName();
                    boolean taskComplete = taskReceived.isCompletion();
                    String taskDate = taskReceived.getDate();
                    ArrayList<Integer> taskTag = taskReceived.getTag();
                    boolean taskImportant = taskReceived.isImportant();
                    taskParsed.setId(id);
                    taskParsed.setName(taskName);
                    taskParsed.setCompletion(taskComplete);
                    taskParsed.setDate(taskDate);
                    taskParsed.setTag(taskTag);
                    taskParsed.setImportant(taskImportant);
                    listTasks.add(taskParsed);
                    if(taskParsed.isImportant()) {
                        listImportantTasks.add(taskParsed);
                    }
                    try {
                        dateTask = new SimpleDateFormat("dd/MM/yyyy").parse(taskParsed.getDate());
                        if(!dateTask.before(today) && !dateTask.after(today)) {
                            listTodayTasks.add(taskParsed);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                recyViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                taskAdapter = new TaskAdapter(getContext(), listTasks, HomeFragment.this::onTaskClick);
                todayTaskAdapter = new TaskAdapter(getContext(), listTodayTasks, HomeFragment.this::onTaskClick);
                importantTaskAdapter = new TaskAdapter(getContext(), listImportantTasks, HomeFragment.this::onTaskClick);
                recyViewTasks.setAdapter(taskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "No data", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void searchTask(String searching) {
        if(searching.isEmpty()) {
            if(selectedTypeTask.equals("All tasks")) {
                recyViewTasks.setAdapter(taskAdapter);
            } else if (selectedTypeTask.equals("Today")) {
                recyViewTasks.setAdapter(todayTaskAdapter);
            } else if(selectedTypeTask.equals("Important")) {
                recyViewTasks.setAdapter(importantTaskAdapter);
            }
        } else {
            ArrayList<Task> listSearchResult = new ArrayList<>();
            if(selectedTypeTask.equals("All tasks")) {
                for(int i = 0; i < listTasks.size(); i++) {
                    Task temp = listTasks.get(i);
                    if(temp.getName().toLowerCase().contains(searching.toLowerCase()) ||
                            temp.getDate().contains(searching)) {
                        listSearchResult.add(listTasks.get(i));
                    }
                }
            } else if (selectedTypeTask.equals("Today")) {
                for(int i = 0; i < listTodayTasks.size(); i++) {
                    Task temp = listTodayTasks.get(i);
                    if(temp.getName().toLowerCase().contains(searching.toLowerCase()) ||
                            temp.getDate().contains(searching)) {
                        listSearchResult.add(listTodayTasks.get(i));
                    }
                }
            } else if(selectedTypeTask.equals("Important")) {
                for(int i = 0; i < listImportantTasks.size(); i++) {
                    Task temp = listImportantTasks.get(i);
                    if(temp.getName().toLowerCase().contains(searching.toLowerCase()) ||
                            temp.getDate().contains(searching)) {
                        listSearchResult.add(listImportantTasks.get(i));
                    }
                }
            }

            TaskAdapter searchResultAdapter = new TaskAdapter(getContext(), listSearchResult, HomeFragment.this::onTaskClick);
            recyViewTasks.setAdapter(searchResultAdapter);
        }
    }


    @Override
    public void onTaskClick(int position) {
        Intent intent = new Intent(getActivity(), UpdateTaskActivity.class);
        intent.putExtra("KEY", String.valueOf(listTasks.get(position).getId()));
//        Log.d("KEY", "onTaskClicked: "+position);
//        Log.d("KEY", "onTaskClicked id: "+listTasks.get(position).getId());
        startActivity(intent);
    }
}