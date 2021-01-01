package com.example.whatchadoin.ui.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.ui.tag.TagActivity;
import com.example.whatchadoin.ui.task.AddTaskActivity;
import com.example.whatchadoin.ui.task.UpdateTaskActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.logging.Logger;

public class HomeFragment extends Fragment implements TaskAdapter.OnTaskListener {

    private HomeViewModel homeViewModel;
    View v;
    View root;
    DatabaseReference reference;
    RecyclerView recyViewTasks;
    TaskAdapter taskAdapter;
    Button add, today, important;
    EditText searchTask;
    Spinner typeTask;
    ArrayList<Task> listTasks = new ArrayList<Task>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        add = (Button) root.findViewById(R.id.btnAdd);
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
                }
                recyViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                taskAdapter = new TaskAdapter(getContext(), listTasks, HomeFragment.this::onTaskClick);
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
            recyViewTasks.setAdapter(taskAdapter);
        } else {
            ArrayList<Task> listSearchResult = new ArrayList<>();
            for(int i = 0; i < listTasks.size(); i++) {
                Task temp = listTasks.get(i);
                if(temp.getName().toLowerCase().contains(searching.toLowerCase()) ||
                    temp.getDate().contains(searching)) {
                    listSearchResult.add(listTasks.get(i));
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