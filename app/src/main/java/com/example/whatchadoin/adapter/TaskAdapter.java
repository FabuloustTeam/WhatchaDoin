package com.example.whatchadoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    Context context;
    ArrayList<Task> tasks;
    private OnTaskListener mOnTaskListener;

    public TaskAdapter(Context context, ArrayList<Task> tasks, OnTaskListener onTaskListener) {
        this.context = context;
        this.tasks = tasks;
        this.mOnTaskListener = onTaskListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false), mOnTaskListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.complete.setChecked(tasks.get(position).isCompletion());
        holder.date.setText(tasks.get(position).getDate());
        holder.nameTask.setText(tasks.get(position).getName());

        if (tasks.get(position).isImportant()) {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(holder.important.getDrawable()),
                    ContextCompat.getColor(context, R.color.important)
            );
        } else {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(holder.important.getDrawable()),
                    ContextCompat.getColor(context, R.color.notimportant));
        }
        holder.complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int id = tasks.get(position).getId();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("task");
                if(isChecked) {
                    reference.child(String.valueOf(id)).child("completion").setValue(true);
                } else {
                    reference.child(String.valueOf(id)).child("completion").setValue(false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date, nameTask;
        CheckBox complete;
        ImageView important;
        OnTaskListener onTaskListener;

        public ViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            complete = (CheckBox) itemView.findViewById(R.id.chkComplete);
            date = (TextView) itemView.findViewById(R.id.tvDate);
            nameTask = (TextView) itemView.findViewById(R.id.tvNameTask);
            important = (ImageView) itemView.findViewById(R.id.ivImportant);
            this.onTaskListener = onTaskListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTaskListener.onTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskListener {
        void onTaskClick(int position);
    }

}