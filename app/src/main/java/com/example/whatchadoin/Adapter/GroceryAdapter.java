package com.example.whatchadoin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Grocery;
import com.example.whatchadoin.models.Item;
import com.example.whatchadoin.ui.grocery.EditGroceryActivity;
import com.example.whatchadoin.ui.item.ItemActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {
    private ArrayList<Grocery> dataSet;
    private ArrayList<Integer> keys;
    private Context context;
    private ArrayList<String> keyItems;

    public GroceryAdapter(ArrayList<Grocery> dataSet, ArrayList<Integer> keys, Context context) {
        this.dataSet = dataSet;
        this.keys = keys;
        this.context = context;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View itemView = inflater.inflate(R.layout.grocery, parent, false);
        return new GroceryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        holder.tvName.setText(dataSet.get(position).getName());
        String key = String.valueOf(keys.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ItemActivity.class);
                intent.putExtra("GROCERY_KEY", key).putExtra("GROCERY_NAME", dataSet.get(position).getName());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditGroceryActivity.class);
            intent.putExtra("GROCERY_KEY", key).putExtra("GROCERY_NAME", dataSet.get(position).getName());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this grocery?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference("item");
                                itemReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Item item = dataSnapshot.getValue(Item.class);
                                            if (item.getGrocery() == Integer.parseInt(key)) {
                                                itemReference.child(dataSnapshot.getKey()).setValue(null);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                DatabaseReference groceryReference = FirebaseDatabase.getInstance().getReference("grocery");
                                groceryReference.child(key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Delete grocery successfully", Toast.LENGTH_LONG).show();
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
                myQuittingDialogBox.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private Button btnDelete, btnEdit;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGroceryName);
            btnDelete = itemView.findViewById(R.id.btnDeleteGrocery);
            btnEdit = itemView.findViewById(R.id.btnEditGrocery);
        }
    }

    public ArrayList<Grocery> getDataSet() {
        return dataSet;
    }
}
