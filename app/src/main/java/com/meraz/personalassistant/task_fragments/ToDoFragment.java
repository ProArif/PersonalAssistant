package com.meraz.personalassistant.task_fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.meraz.personalassistant.R;
import com.meraz.personalassistant.alarm.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class ToDoFragment extends Fragment {



    private OnFragmentInteractionListener mListener;

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

    private FloatingActionButton addTask;
    private View view;
    private int year, month, day;
    private Calendar calendar;
    private Context context;
    private String taskDescr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.todo_fragment, container, false);
        context = getContext();
        addTask = view.findViewById(R.id.addTask);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddTaskDialog();


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
        Button btn_add_task = dialogView.findViewById(R.id.btn_add_task);
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        ImageButton btn_alarm = dialogView.findViewById(R.id.setAlarm);
        final TextView txtTime = dialogView.findViewById(R.id.txtTime);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtTime.setText(hourOfDay + " : " + minute);
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE,minute);

                                Intent intent = new Intent(context, AlarmReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);

                                AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);

                                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
                                Toast.makeText(context, "Alarm Set.", Toast.LENGTH_LONG).show();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }

        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy");
                        calendar.set(year,month,dayOfMonth);
                        String finalDate = sdf.format(calendar.getTime());
                        et_date.setText(finalDate);
                    }
                }, year, month, day
                );
                datePickerDialog.show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
