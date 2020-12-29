package com.example.whatchadoin.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View v;
    View root;
    DatabaseReference reference;
    RecyclerView recyViewTasks;
    ArrayList<Task> listTasks;
    TaskAdapter taskAdapter;

    ListView lvContact;
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

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
        listTasks = new ArrayList<Task>();

        reference = FirebaseDatabase.getInstance().getReference().child("task");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task taskTmp = dataSnapshot.getValue(Task.class);
                    listTasks.add(taskTmp);
                }
                recyViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                taskAdapter = new TaskAdapter(getContext(), listTasks);
                recyViewTasks.setAdapter(taskAdapter);
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "No data", Toast.LENGTH_LONG).show();
            }
        });

//        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
//        lvContact = root.findViewById(R.id.lvContact);
//        lvContact.setAdapter(adapter);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("task");
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                adapter.clear();
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    String key = data.getKey();
//                    String value = data.getValue().toString();
//                    adapter.add(key + "\n" + value);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
//            }
//        });
    }



}