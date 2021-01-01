package com.example.whatchadoin.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

public class UpdateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    EditText taskNameUpdate;
    TextView chooseDateUpdate;
    TextView selectedTagsUpdate;
    CheckBox importantUpdate;
    Button updateTask, deleteTask;
    DatabaseReference referenceTag;
    DatabaseReference referenceTask;
    Context context;
    Button chooseTagUpdate;
    String[] listNameTags;
    boolean[] checkedTags;
    ArrayList<Integer> mUserTags = new ArrayList<>();
    ArrayList<Tag> listTags = new ArrayList<Tag>();
    Task taskParsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        context = this;
        taskNameUpdate = (EditText) findViewById(R.id.etTaskNameUpdate);
        chooseTagUpdate = (Button) findViewById(R.id.btnChooseTagUpdate);
        selectedTagsUpdate = (TextView) findViewById(R.id.tvSelectedTagsUpdate);
        chooseDateUpdate = (TextView) findViewById(R.id.tvChooseDateUpdate);
        importantUpdate = (CheckBox) findViewById(R.id.chkImportantUpdate);
        updateTask = (Button) findViewById(R.id.btnUpdateTask);
        deleteTask = (Button) findViewById(R.id.btnDeleteTask);

        getReady();


        taskNameUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listTags.size() < 3) {
                    loadTags();
                } else {

                }
            }
        });


        chooseTagUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.getLogger("debug000").warning(String.valueOf(listTags.size() + "After loadTags"));

                if (checkedTags == null) {
                    checkedTags = new boolean[listTags.size()];
                }
                if (listNameTags == null) {
                    listNameTags = new String[listTags.size()];
                    for (int i = 0; i < listTags.size(); i++) {
                        listNameTags[i] = listTags.get(i).getName();
                        if(taskParsed.getTag() != null) {
                            if(taskParsed.getTag().contains(listTags.get(i).getId())) {
                                mUserTags.add(i);
                                checkedTags[i] = true;
                            }
                        }
                    }
                }

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UpdateTaskActivity.this);
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
                        selectedTagsUpdate.setText(tag);
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
                            selectedTagsUpdate.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        chooseDateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        updateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(taskNameUpdate.getText().toString().isEmpty()) {
                    if(taskNameUpdate.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Please input name",Toast.LENGTH_LONG).show();
                    }
                } else if(chooseDateUpdate.getText().toString().equals("  Choose date")) {
                    Toast.makeText(context, "Please input date",Toast.LENGTH_LONG).show();
                } else {
                    UpdatedTask updatedTask = new UpdatedTask();
                    updatedTask.setName(taskNameUpdate.getText().toString().trim());
                    updatedTask.setDate(chooseDateUpdate.getText().toString().trim());
                    updatedTask.setImportant(importantUpdate.isChecked());
                    updatedTask.setCompletion(taskParsed.isCompletion());

                    ArrayList<Integer> listTagOfTaskUpdate = new ArrayList<Integer>();
                    if(!selectedTagsUpdate.getText().toString().isEmpty()) {
                        String[] tags = selectedTagsUpdate.getText().toString().split("#");
                        for (int i = 0; i < tags.length; i++) {
                            for (int j = 0; j < listTags.size(); j++) {
                                if (listTags.get(j).getName().equals(tags[i].trim())) {
                                    listTagOfTaskUpdate.add(listTags.get(j).getId());
                                }
                            }
                        }
                    }
                    updatedTask.setTag(listTagOfTaskUpdate);

                    referenceTask = FirebaseDatabase.getInstance().getReference().child("task");
                    referenceTask.child(String.valueOf(taskParsed.getId())).setValue(updatedTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Update task successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        });

        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete this task?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        referenceTask = FirebaseDatabase.getInstance().getReference().child("task");
                        referenceTask.child(String.valueOf(taskParsed.getId())).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Delete task successfully", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private void getReady() {
        loadTags();
        getTaskDetails();
    }

    public void loadTags() {
        referenceTag = FirebaseDatabase.getInstance().getReference().child("tag");
        referenceTag.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(UpdateTaskActivity.this, "No data", Toast.LENGTH_LONG).show();
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
        chooseDateUpdate.setText(date);
    }

    private void getTaskDetails() {
        Intent intent = getIntent();
        final String key = intent.getStringExtra("KEY");
        referenceTask = FirebaseDatabase.getInstance().getReference("task");

        referenceTask.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Task taskReceived = dataSnapshot.getValue(Task.class);
                    taskParsed = new Task();
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
                    taskParsed.setTag(taskTag);

                    taskNameUpdate.setText(taskParsed.getName());
                    if(taskParsed.getTag().size() != 0) {
                        String tagValue = getTagsOfTask(taskParsed.getTag());
                        selectedTagsUpdate.setText(tagValue);
                    }
                    chooseDateUpdate.setText("  " + taskParsed.getDate());
                    importantUpdate.setChecked(taskParsed.isImportant());

                } catch (Exception ex) {
                    Log.e("Json error", ex.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("detail error", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private String getTagsOfTask(ArrayList<Integer> tagsOfTask) {
        String tags = "";
        for (int i = 0; i < tagsOfTask.size(); i++) {
            String temp = getNameInAllTags(tagsOfTask.get(i));
            tags = tags + "#" + temp;
            if (i != tagsOfTask.size() - 1) {
                tags = tags + "  ";
            }
        }
        return tags;
    }

    private String getNameInAllTags(int id) {
        String name = "";
        for (int j = 0; j < listTags.size(); j++) {
            if (listTags.get(j).getId() == id) {
                name = listTags.get(j).getName();
                break;
            }
        }
        return name;
    }
}