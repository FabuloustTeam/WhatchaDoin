package com.example.whatchadoin.ui.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatchadoin.R;
import com.example.whatchadoin.models.Tag;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    Context context;
    ArrayList<Tag> tags;

    public TagAdapter(Context context, ArrayList<Tag> tags) {
        this.context = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tag, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTag.setText(tags.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTag;
        CheckBox choose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            choose = (CheckBox) itemView.findViewById(R.id.chkChoose);
            nameTag = (TextView) itemView.findViewById(R.id.tvNameTag);
        }
    }


}