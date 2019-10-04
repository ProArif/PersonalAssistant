package com.meraz.personalassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.meraz.personalassistant.R;
import java.util.List;

public class ToDoRecyclerAdapter extends RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoDataViewHolder> {

    public List<ToDoTaskHelper> data;

    public ToDoRecyclerAdapter(List<ToDoTaskHelper> data) {
        this.data = data;
    }

    private ToDoTaskHelper helper;

    @NonNull
    @Override
    public ToDoDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_item,parent,false);

        helper = new ToDoTaskHelper();
        return new ToDoDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoDataViewHolder holder, int position) {

        holder.textToDo.setText(data.get(position).getTaskDesc());
        holder.textDate.setText(data.get(position).getTaskDate());
        holder.textClock.setText(data.get(position).getTaskTime());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ToDoDataViewHolder extends RecyclerView.ViewHolder{

        private TextView textToDo,textDate;
        private TextView textClock;

        public ToDoDataViewHolder(@NonNull View itemView) {
            super(itemView);
            textToDo = itemView.findViewById(R.id.toDoText);
            textDate = itemView.findViewById(R.id.date);
            textClock = itemView.findViewById(R.id.toDoClock);

        }
    }
}
