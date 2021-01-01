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
    Button updateTask;
    DatabaseReference reference;
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

        loadTags();
        getTaskDetails();

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
                if(listNameTags == null) {
                    listNameTags = new String[listTags.size()];
                    for (int i = 0; i < listTags.size(); i++) {
                        listNameTags[i] = listTags.get(i).getName();
                    }
                }
                if(checkedTags == null) {
                    checkedTags = new boolean[listTags.size()];
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
        reference = FirebaseDatabase.getInstance().getReference("task");

        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    taskNameUpdate.setText(taskParsed.getName());

                    chooseDateUpdate.setText("  " + taskParsed.getDate());
                    importantUpdate.setChecked(taskParsed.isCompletion());

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

    private String getTagsOfTask(ArrayList<Tag> allTags) {
        String tags = "";


        return tags;
    }
}