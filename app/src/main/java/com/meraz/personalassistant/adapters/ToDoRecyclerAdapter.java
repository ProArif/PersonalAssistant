package com.meraz.personalassistant.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meraz.personalassistant.R;
import com.meraz.personalassistant.helpers.DailyExpenses;
import com.meraz.personalassistant.helpers.ToDoTaskHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ToDoRecyclerAdapter extends RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoDataViewHolder> {

    public List<ToDoTaskHelper> data;
    private Context context;

    public ToDoRecyclerAdapter(Context context,List<ToDoTaskHelper> data) {
        this.context = context;
        this.data = data;
    }

    private ToDoTaskHelper helper;
    private String date,desc,time,uid;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @NonNull
    @Override
    public ToDoDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_item,parent,false);

        helper = new ToDoTaskHelper();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            uid = mUser.getUid();
            reference = database.getReference("tasks");
            reference.keepSynced(true);
        }
        return new ToDoDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoDataViewHolder holder,final int position) {
        if (data != null) {
            holder.textToDo.setText(data.get(position).getTaskDesc());
            holder.textDate.setText(data.get(position).getTaskDate());
            holder.textClock.setText(data.get(position).getTaskTime());

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    android.widget.PopupMenu popup = new PopupMenu(context, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.update_or_delete_menu, popup.getMenu());
                    popup.setGravity(Gravity.END);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id){
                                case R.id.edit_item:

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View dialogView = inflater.inflate(R.layout.add_task,null);
                                    builder.setView(dialogView);

                                    final EditText textToDo =  dialogView.findViewById(R.id.taskDesc);
                                    final EditText textDate = dialogView.findViewById(R.id.date);
                                    final TextView textClock = dialogView.findViewById(R.id.tv_time);
                                    Button btn_add_task = dialogView.findViewById(R.id.btn_add_task);
                                    Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

                                    final AlertDialog alertDialog = builder.create();
                                    //show existing text to edit
                                    textToDo.setText(data.get(position).getTaskDesc());
                                    textDate.setText(data.get(position).getTaskDate());
                                    textClock.setText(data.get(position).getTaskTime());
                                    alertDialog.show();


//                                    textDate.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
//                                            textDate.setText(dateFormat.format(new Date()));
//                                            date = textDate.getText().toString();
//                                            helper.setTaskDate(date);
//
//                                        }
//                                    });

                                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    btn_add_task.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            desc = textToDo.getText().toString();
                                            helper.setTaskDesc(desc);
                                            date = textDate.getText().toString();
                                            helper.setTaskDate(date);
                                            time = textClock.getText().toString();
                                            helper.setTaskTime(time);

                                            String key = data.get(position).getNodeKey();
                                            ToDoTaskHelper todo = new ToDoTaskHelper(helper.getTaskDesc(),
                                                    helper.getTaskDate(),helper.getTaskTime(),key,uid);

                                            reference.child(data.get(position).getNodeKey()).setValue(todo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        alertDialog.dismiss();
                                                        Toast.makeText(context, "Task Edited", Toast.LENGTH_LONG).show();
                                                    }else {
                                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                        }
                                    });
                                    return true;

                                case R.id.delete_item:
                                    //delete query
                                    reference.child(data.get(position).getNodeKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });

                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.show();
                    return true;
                }
            });

        }

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
