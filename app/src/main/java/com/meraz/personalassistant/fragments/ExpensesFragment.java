package com.meraz.personalassistant.fragments;

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
import android.widget.EditText;
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
import com.meraz.personalassistant.adapters.ExpenseAdapter;
import com.meraz.personalassistant.helpers.DailyExpenses;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ExpensesFragment extends Fragment {

    private Context context;
    private DailyExpenses exp_helper;
    private ExpenseAdapter adapter;
    private List<DailyExpenses> mData;
    private RecyclerView exp_recycler;
    private int year, month, day;
    private Calendar calendar;
    private String title,date,amount,uid;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private float initialX, initialY;
    private  View view;
    private FloatingActionButton addExp;



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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            setHasOptionsMenu(true);

            exp_helper = new DailyExpenses();
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            user = mAuth.getCurrentUser();
            if (user != null) {
                uid = user.getUid();
                reference = database.getReference("expenses");
                reference.keepSynced(true);
            }
        // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_expenses, container, false);
            context = getContext();
            exp_recycler = view.findViewById(R.id.expRecycler);
            addExp = view.findViewById(R.id.addExp);

            addExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddExpenseDialog();
                }
            });

            mData = new ArrayList<>();
            adapter = new ExpenseAdapter(context,mData);
            exp_recycler.setHasFixedSize(true);
            exp_recycler.setLayoutManager(new LinearLayoutManager(context));
            adapter.notifyDataSetChanged();
            exp_recycler.setAdapter(adapter);
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
            Query query = reference1.child("expenses")
                    .orderByChild("user_id").equalTo(user.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     mData.clear();

                    for (DataSnapshot items : dataSnapshot.getChildren()) {
                        DailyExpenses expenses = items.getValue(DailyExpenses.class);
                        mData.add(expenses);
                        adapter.notifyDataSetChanged();
                        Log.e("items found",items.toString());
                        Log.e("data found", String.valueOf(adapter.getItemCount()));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
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

                String key = reference.push().getKey();
                DailyExpenses exp = new DailyExpenses(exp_helper.getExp_id(),exp_helper.getExp_title(),
                        exp_helper.getExp_amount(),exp_helper.getExp_date(),key,user.getUid());
//                db.collection("expenses").document(uid).set(exp).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        alertDialog.dismiss();
//                        Toast.makeText(context, "Expense Added.", Toast.LENGTH_LONG).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
//
//                    }
//                });
                reference.child(key).setValue(exp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            alertDialog.dismiss();
                            Toast.makeText(context, "Expense Added", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
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
