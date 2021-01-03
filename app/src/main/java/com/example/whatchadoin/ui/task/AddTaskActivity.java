package com.example.whatchadoin.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Tag;
import com.example.whatchadoin.models.Task;
import com.example.whatchadoin.models.UpdatedTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText taskName;
    TextView chooseDate;
    TextView selectedTags;
    CheckBox important;
    Button addTask;
    DatabaseReference reference;
    Context context;
    Button chooseTag;
    boolean[] checkedTags;
    ArrayList<Integer> mUserTags = new ArrayList<>();
    ArrayList<Tag> listTags = new ArrayList<>();
    long maxId;
    String[] listNameTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        context = this;
        taskName = (EditText) findViewById(R.id.etTaskName);
        chooseDate = (TextView) findViewById(R.id.tvChooseDate);
        chooseTag = (Button) findViewById(R.id.btnChooseTag);
        important = (CheckBox) findViewById(R.id.chkImportant);
        selectedTags = (TextView) findViewById(R.id.tvSelectedTags);
        addTask = (Button) findViewById(R.id.btnAddTask);

        loadTags();


        taskName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listTags.size() < 3) {
                    loadTags();
                } else {

                }

            }
        });


        chooseTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.getLogger("debug000").warning(String.valueOf(listTags.size() + "After loadTags"));

                if (listNameTags == null) {
                    listNameTags = new String[listTags.size()];
                    for (int i = 0; i < listTags.size(); i++) {
                        listNameTags[i] = listTags.get(i).getName();
                    }
                }
                if (checkedTags == null) {
                    checkedTags = new boolean[listTags.size()];
                }

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddTaskActivity.this);
                mBuilder.setTitle(R.string.dialog_choose_tag_title);
                mBuilder.setMultiChoiceItems(listNameTags, checkedTags, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        checkedTags[position] = isChecked;
                        if (isChecked) {
                            if (!mUserTags.contains(position)) {
                                mUserTags.add(position);
                            }
                        } else {
                            if (mUserTags.contains(position)) {
                                mUserTags.remove(mUserTags.indexOf(position));
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
                                tag = tag + "  ";
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

        reference = FirebaseDatabase.getInstance().getReference().child("task");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (maxId < Integer.parseInt(dataSnapshot.getKey())) {
                        maxId = Integer.parseInt(dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskName.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please input name", Toast.LENGTH_LONG).show();
                } else if (chooseDate.getText().toString().equals("  Choose date")) {
                    Toast.makeText(context, "Please choose date", Toast.LENGTH_LONG).show();
                } else {
                    addNewTask();
                    finish();
                }
            }
        });
    }


    private void addNewTask() {
        UpdatedTask task = new UpdatedTask();
        task.setName(taskName.getText().toString());
        task.setDate(chooseDate.getText().toString().trim());
        task.setImportant(important.isChecked());

        ArrayList<Integer> listTagOfTask = new ArrayList<Integer>();
        if (!selectedTags.getText().toString().isEmpty()) {
            String[] tags = selectedTags.getText().toString().split("#");
            for (int i = 0; i < tags.length; i++) {
                for (int j = 0; j < listTags.size(); j++) {
                    if (listTags.get(j).getName().equals(tags[i].trim())) {
                        listTagOfTask.add(listTags.get(j).getId());
                    }
                }
            }
        }

        task.setTag(listTagOfTask);

        reference = FirebaseDatabase.getInstance().getReference().child("task");
        reference.child(String.valueOf(maxId + 1)).setValue(task);
    }


    public void loadTags() {
        reference = FirebaseDatabase.getInstance().getReference().child("tag");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTags.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tag tagReceived = dataSnapshot.getValue(Tag.class);
                    Tag tagParsed = new Tag();
                    String taskName = tagReceived.getName();
                    tagParsed.setId(Integer.parseInt(dataSnapshot.getKey()));
                    tagParsed.setName(taskName);
                    listTags.add(tagParsed);
                }
                Logger.getLogger("debug000").warning(String.valueOf(listTags.size() + "while at loagTags"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddTaskActivity.this, "No data", Toast.LENGTH_LONG).show();
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


}