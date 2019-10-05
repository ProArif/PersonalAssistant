package com.meraz.personalassistant.task_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.meraz.personalassistant.R;
import com.meraz.personalassistant.adapters.ExpenseAdapter;
import com.meraz.personalassistant.adapters.ToDoTaskHelper;
import com.meraz.personalassistant.dailyexpenses.DailyExpenses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class ExpensesFragment extends Fragment {

    private Context context;
    private DailyExpenses exp_helper;
    private ExpenseAdapter adapter;
    private List<DailyExpenses> mData;
    private RecyclerView exp_recycler;
    private FirebaseFirestore db ;
    private int year, month, day;
    private Calendar calendar;
    private String title,date,amount;


//    private OnFragmentInteractionListener mListener;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    public ExpensesFragment(List<DailyExpenses> mData) {
        this.mData = mData;
    }

    public static ExpensesFragment newInstance(String param1, String param2) {
        ExpensesFragment fragment = new ExpensesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exp_helper = new DailyExpenses();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        setHasOptionsMenu(true);

        context = getContext();
        db = FirebaseFirestore.getInstance();
        exp_recycler = view.findViewById(R.id.expRecycler);
        mData = new ArrayList<>();
        adapter = new ExpenseAdapter(mData);
        exp_recycler.setHasFixedSize(true);
        exp_recycler.setLayoutManager(new LinearLayoutManager(context));
        exp_recycler.setAdapter(adapter);

        db.collection("expenses").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        DailyExpenses helper = documentChange.getDocument().toObject(DailyExpenses.class);
                        mData.add(helper);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_expense,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.addExpense:
                showAddExpenseDialog();
        }

        return super.onOptionsItemSelected(item);

    }

    private void showAddExpenseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.expense_dialog,null);
        builder.setView(dialogView);

        final EditText et_exp_title =  dialogView.findViewById(R.id.et_expense_title);
        final EditText et_exp_date = dialogView.findViewById(R.id.et_expenseDate);
        final EditText et_exp_amount = dialogView.findViewById(R.id.et_expenseAmount);
        Button btn_add_exp = dialogView.findViewById(R.id.btn_addExpense);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final Calendar myCalendar = Calendar.getInstance();

//        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
//
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, month);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy", Locale.US);
//                String finalDate = sdf.format(myCalendar.getTime());
//                et_exp_date.setText(finalDate);
//                date = et_exp_date.getText().toString(); //store date
//                exp_helper.setExp_date(date);
//            }
//        };


        et_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new DatePickerDialog(context, dateSetListener, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                et_exp_date.setText(dateFormat.format(new Date()));
                date = et_exp_date.getText().toString();
                exp_helper.setExp_date(date);

            }
        });

        btn_add_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = et_exp_title.getText().toString();
                exp_helper.setExp_title(title);
                amount = et_exp_amount.getText().toString();
                exp_helper.setExp_amount(amount);

                DailyExpenses exp = new DailyExpenses(exp_helper.getExp_id(),exp_helper.getExp_title(),exp_helper.getExp_amount(),exp_helper.getExp_date());
                db.collection("expenses").add(exp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        alertDialog.dismiss();
                        Toast.makeText(context, "Expense Added.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            }
        });




    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
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
