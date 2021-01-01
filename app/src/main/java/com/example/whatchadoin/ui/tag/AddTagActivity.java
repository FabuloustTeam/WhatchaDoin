package com.example.whatchadoin.ui.tag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTagActivity extends AppCompatActivity {
    EditText mtag;
    Button add;
    DatabaseReference myRef;
    Tag tag;
    int maxid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        mtag = (EditText)findViewById(R.id.txtTagname);
        add = (Button)findViewById(R.id.btnAdd);
        tag = new Tag();
        myRef= FirebaseDatabase.getInstance().getReference().child("tag");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid= (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag.setName(mtag.getText().toString().trim());
                myRef.child(String.valueOf(maxid+1)).setValue(tag);

                Toast.makeText(AddTagActivity.this, "Data inserted succesfully", Toast.LENGTH_LONG).show();
            }
        });
    }
}