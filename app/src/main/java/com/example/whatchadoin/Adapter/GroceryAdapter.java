package com.example.whatchadoin.Adapter;

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
import com.example.whatchadoin.ui.grocery.EditGroceryActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {
    private ArrayList<Grocery> dataSet;
    private ArrayList<Integer> keys;

    public GroceryAdapter(ArrayList<Grocery> dataSet, ArrayList<Integer> keys) {
        this.dataSet = dataSet;
        this.keys = keys;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View itemView = inflater.inflate(R.layout.line_item_grocery, parent, false);
        return new GroceryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        holder.tvName.setText(dataSet.get(position).getName());
        String key = String.valueOf(keys.get(position));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditGroceryActivity.class);
            intent.putExtra("GROCERY_KEY", key).putExtra("GROCERY_NAME", dataSet.get(position).getName());
            holder.itemView.getContext().startActivity(intent);
        });
        holder.btnXoa.setOnClickListener(v -> {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("grocery")
                    .child(key)
                    .setValue(null)
                    .addOnSuccessListener(i -> {
                        Toast.makeText(holder.itemView.getContext(), "Xoa thanh cong", Toast.LENGTH_LONG).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class GroceryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private Button btnXoa;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.line_item_grocery_name);
            btnXoa = itemView.findViewById(R.id.line_item_grocery_btnXoa);
        }
    }

    public ArrayList<Grocery> getDataSet() {
        return dataSet;
    }
}