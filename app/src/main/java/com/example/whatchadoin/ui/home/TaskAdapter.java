package com.example.whatchadoin.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    Context context;
    ArrayList<Task>  tasks;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.complete.setChecked(tasks.get(position).isComplete());
        holder.date.setText(tasks.get(position).getDate());
        holder.nameTask.setText(tasks.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, nameTask;
        CheckBox complete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            complete = (CheckBox) itemView.findViewById(R.id.chkComplete);
            date = (TextView) itemView.findViewById(R.id.tvDate);
            nameTask = (TextView) itemView.findViewById(R.id.tvNameTask);
        }
    }
}