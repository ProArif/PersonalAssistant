package com.meraz.personalassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meraz.personalassistant.R;

public class ToDoRecyclerAdapter extends RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoDataViewHolder> {


    @NonNull
    @Override
    public ToDoDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_item,parent,false);

        return new ToDoDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoDataViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ToDoDataViewHolder extends RecyclerView.ViewHolder{

        private TextView textToDo,textDate;
        private TextClock textClock;

        public ToDoDataViewHolder(@NonNull View itemView) {
            super(itemView);
            textToDo = itemView.findViewById(R.id.toDoText);
            textDate = itemView.findViewById(R.id.date);
            textClock = itemView.findViewById(R.id.toDoClock);

        }
    }
}
