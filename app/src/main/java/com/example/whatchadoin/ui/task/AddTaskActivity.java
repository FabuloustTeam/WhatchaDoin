package com.example.whatchadoin.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Tag;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.ui.home.TaskAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText taskName;
    TextView chooseDate;
    TextView selectedTags;
    DatabaseReference reference;
    Context context;
    Button chooseTag;
//    ArrayList<String> listTags;
    boolean[] checkedTags;
    ArrayList<Integer> mUserTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        context = this;
        taskName = (EditText) findViewById(R.id.etTaskName);
        chooseDate = (TextView) findViewById(R.id.tvChooseDate);
        chooseTag = (Button) findViewById(R.id.btnChooseTag);
        selectedTags = (TextView) findViewById(R.id.tvSelectedTags);

//        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(AddTaskActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(horizontalLayoutManagaer);
//        getTags();
        ArrayList<String> listTags = new ArrayList<String>();
//        getTags();

        reference = FirebaseDatabase.getInstance().getReference().child("tag");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Tag tagReceived = dataSnapshot.getValue(Tag.class);
                    Tag tagParsed = new Tag();
                    String taskName = tagReceived.getName();
                    tagParsed.setName(taskName);
                    listTags.add(tagParsed.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddTaskActivity.this, "No data", Toast.LENGTH_LONG).show();
            }
        });

        checkedTags = new boolean[listTags.size()];
        String[] listNameTags = new String[listTags.size()];
        for (int i = 0; i < listTags.size(); i++) {
            listNameTags[i] = listTags.get(i);
        }

        chooseTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddTaskActivity.this);
                mBuilder.setTitle(R.string.dialog_choose_tag_title);
                mBuilder.setMultiChoiceItems(listNameTags, checkedTags, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserTags.contains(position)) {
                                mUserTags.add(position);
                            } else {
                                mUserTags.remove(position);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.OK_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String tag = "";
                        for (int i = 0; i < mUserTags.size(); i++) {
                            tag = tag + "#" + listNameTags[mUserTags.get(i)];
                            if (i != mUserTags.size() - 1) {
                                tag = tag + ", ";
                            }
                        }
                        selectedTags.setText(tag);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedTags.length; i++) {
                            checkedTags[i] = false;
                            mUserTags.clear();
                            selectedTags.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = "  " + day + "/" + (month + 1) + "/" + year;
        chooseDate.setText(date);
    }

//    private void getTags() {
//        reference = FirebaseDatabase.getInstance().getReference().child("tag");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    Tag tagReceived = dataSnapshot.getValue(Tag.class);
//                    Tag tagParsed = new Tag();
//                    String taskName = tagReceived.getName();
//                    tagParsed.setName(taskName);
//                    listTags.add(tagParsed.getName());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(AddTaskActivity.this, "No data", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void getTags() {
//
//        reference = FirebaseDatabase.getInstance().getReference().child("tag");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<Tag> listTags = new ArrayList<Tag>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//
//                    Tag tagReceived = dataSnapshot.getValue(Tag.class);
//                    Tag tagParsed = new Tag();
//                    String tagName = tagReceived.getName();
//                    tagParsed.setName(tagName);
//                    tagParsed.setId(Integer.parseInt(dataSnapshot.getKey()));
//                    listTags.add(tagParsed);
//                }
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                tagAdapter = new TagAdapter(context, listTags);
//                recyclerView.setAdapter(tagAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

}