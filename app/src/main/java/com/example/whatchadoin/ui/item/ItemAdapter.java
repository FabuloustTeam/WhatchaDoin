package com.example.whatchadoin.ui.item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Context context;
    ArrayList<Item> items;
    private OnItemListener mOnItemListener;

    public ItemAdapter(Context context, ArrayList<Item> items, OnItemListener mOnItemListener) {
        this.context = context;
        this.items = items;
        this.mOnItemListener = mOnItemListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false), mOnItemListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.done.setChecked(items.get(position).isDone());
        holder.nameItem.setText(items.get(position).getName());

        holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String id = items.get(position).getId();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("item");
                if (isChecked) {
                    reference.child(id).child("done").setValue(true);
                } else {
                    reference.child(id).child("done").setValue(false);
                }
            }
        });

        holder.renameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUpdateItem = new Intent(holder.itemView.getContext(), ItemUpdateActivity.class);
                intentUpdateItem.putExtra("KEY", items.get(position).getId());
                holder.itemView.getContext().startActivity(intentUpdateItem);
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("item");
                reference.child(items.get(position).getId()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Delete item successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameItem;
        CheckBox done;
        Button renameItem, deleteItem;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            nameItem = (TextView) itemView.findViewById(R.id.tvNameItem);
            done = (CheckBox) itemView.findViewById(R.id.chkDone);
            renameItem = (Button) itemView.findViewById(R.id.btnRenameItem);
            deleteItem = (Button) itemView.findViewById(R.id.btnDeleteItem);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}