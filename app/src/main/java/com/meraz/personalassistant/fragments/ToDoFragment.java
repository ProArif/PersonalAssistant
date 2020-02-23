package com.meraz.personalassistant.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.meraz.personalassistant.R;
import com.meraz.personalassistant.activities.LoginActivity;
import com.meraz.personalassistant.activities.MainActivity;
import com.meraz.personalassistant.adapters.ToDoRecyclerAdapter;
import com.meraz.personalassistant.helpers.ToDoTaskHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class ToDoFragment extends Fragment {



    //private OnFragmentInteractionListener mListener;

    public ToDoFragment() {
        // Required empty public constructor
    }


//    // TODO: Rename and change types and number of parameters
//    public static ToDoFragment newInstance(String param1, String param2) {
//        ToDoFragment fragment = new ToDoFragment();
//        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
//        return fragment;
//    }

    private View view;
    private int year, month, day;
    private Calendar calendar;
    private Context context;
    private String taskDescr,date,time,uid;
    private List<ToDoTaskHelper> mData;
    private ToDoRecyclerAdapter adapter;
    private RecyclerView task_recy;
    private ToDoTaskHelper helper;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FloatingActionButton addTodo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        helper = new ToDoTaskHelper();
        database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            reference = database.getReference("tasks");
            reference.keepSynced(true);
        }
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.todo_fragment, container, false);
        context = getContext();

        addTodo = view.findViewById(R.id.addToDo);
        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        task_recy = view.findViewById(R.id.taskRecycler);
        mData = new ArrayList<>();
        adapter = new ToDoRecyclerAdapter(context,mData);

        task_recy.setHasFixedSize(true);
        task_recy.setLayoutManager(new LinearLayoutManager(context));
        task_recy.setAdapter(adapter);

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query = reference1.child("tasks")
                .orderByChild("user_id").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();

                for (DataSnapshot items : dataSnapshot.getChildren()) {

                    ToDoTaskHelper helper = items.getValue(ToDoTaskHelper.class);
                    mData.add(helper);
                    adapter.notifyDataSetChanged();
                    Log.e("items found",items.toString());
                    Log.e("data found", String.valueOf(adapter.getItemCount()));

                }
                Log.e("entered","value event listener entered");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

          return view;
    }

    private void showAddTaskDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_task,null);
        builder.setView(dialogView);

        final EditText et_task_desc =  dialogView.findViewById(R.id.taskDesc);
        final EditText et_date = dialogView.findViewById(R.id.date);
        final Button et_time = dialogView.findViewById(R.id.pickTime);
        Button btn_add_task = dialogView.findViewById(R.id.btn_add_task);
        final TextView txtTime = dialogView.findViewById(R.id.tv_time);
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        taskDescr = et_task_desc.getText().toString();
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy", Locale.US);
                String finalDate = sdf.format(myCalendar.getTime());
                et_date.setText(finalDate);
                date = et_date.getText().toString(); //store date
                helper.setTaskDate(date);
            }
        };

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        context, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
//                        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy", Locale.US);
//                        calendar.set(year,month,dayOfMonth);
//                        String finalDate = sdf.format(calendar.getTime());
//                        et_date.setText(finalDate);
//                    }
//                }, year, month, day
//                );
//                datePickerDialog.show();

                new DatePickerDialog(context, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);


                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtTime.setText(hourOfDay + ":" + minute);
                                DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                                time = txtTime.getText().toString();
                                String finaltime = dateFormat.format(c.getTime());
                                helper.setTaskTime(finaltime);

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btn_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskDescr = et_task_desc.getText().toString();
                helper.setTaskDesc(taskDescr);
                time = txtTime.getText().toString();
                helper.setTaskTime(time);

                String key = reference.push().getKey();
                ToDoTaskHelper taskHelper = new ToDoTaskHelper(helper.getTaskDesc(),
                        helper.getTaskDate(),helper.getTaskTime(),key,user.getUid());


                reference.child(key).setValue(taskHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            alertDialog.dismiss();
                            Toast.makeText(context, "Task Added", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_todo,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addTask:
                showAddTaskDialog();
                break;

            case R.id.logout:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Do you want to Logout?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (user != null){
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("credentials", MODE_PRIVATE).edit();

                                    clearStack();
                                    Intent n = new Intent(getActivity(), LoginActivity.class);
                                    n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    editor.clear();
                                    editor.apply();
                                    startActivity(n);
                                    mAuth.signOut();
                                    //finish();
                                }

                                dialog.cancel();
                                //startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
        }

        return super.onOptionsItemSelected(item);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    public void clearStack() {
        //Here we are clearing back stack fragment entries
        int backStackEntry = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (getActivity().getSupportFragmentManager().getFragments() != null && getActivity().getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getFragments().size(); i++) {
                Fragment mFragment = getActivity().getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
