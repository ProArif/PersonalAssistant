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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseDataViewHolder> {

    private Context context;
    private List<DailyExpenses> expenses;
    private String title,date,amount,uid;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;


    public ExpenseAdapter(Context context,List<DailyExpenses> expenses) {

        this.context = context;
        this.expenses = expenses;
    }
    private DailyExpenses dailyExpenses;

    @NonNull
    @Override
    public ExpenseDataViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_exp_item,parent,false);
        dailyExpenses = new DailyExpenses();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            reference = database.getReference("expenses");
            reference.keepSynced(true);
        }


        return new ExpenseDataViewHolder(view);
    }

    private void showAddExpenseDialog() {




    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseDataViewHolder holder, final int position) {

        if (expenses !=null){
            holder.tv_expenseTitle.setText(expenses.get(position).getExp_title());
            holder.tv_expenseRank.setText(String.format(Locale.US,"%d.", position + 1));
            holder.tv_expenseDate.setText(expenses.get(position).getExp_date());
            holder.tv_expenseAmount.setText(expenses.get(position).getExp_amount());


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
                                    View dialogView = inflater.inflate(R.layout.expense_dialog,null);
                                    builder.setView(dialogView);

                                    final EditText et_exp_title =  dialogView.findViewById(R.id.et_expense_title);
                                    final EditText et_exp_date = dialogView.findViewById(R.id.et_expenseDate);
                                    final EditText et_exp_amount = dialogView.findViewById(R.id.et_expenseAmount);
                                    Button btn_add_exp = dialogView.findViewById(R.id.btn_addExpense);

                                    final AlertDialog alertDialog = builder.create();
                                    //show existing text to edit
                                    et_exp_title.setText(expenses.get(position).getExp_title());
                                    et_exp_date.setText(expenses.get(position).getExp_date());
                                    et_exp_amount.setText(expenses.get(position).getExp_amount());
                                    alertDialog.show();

                                    final Calendar myCalendar = Calendar.getInstance();

                                    et_exp_date.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                                            et_exp_date.setText(dateFormat.format(new Date()));
                                            date = et_exp_date.getText().toString();
                                            dailyExpenses.setExp_date(date);

                                        }
                                    });

                                    btn_add_exp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            title = et_exp_title.getText().toString();
                                            dailyExpenses.setExp_title(title);
                                            amount = et_exp_amount.getText().toString();
                                            date = et_exp_date.getText().toString();
                                            dailyExpenses.setExp_amount(amount);

                                            String key = expenses.get(position).getNodeKey();
                                            DailyExpenses exp = new DailyExpenses(dailyExpenses.getExp_id(),dailyExpenses.getExp_title(),
                                                    dailyExpenses.getExp_amount(),dailyExpenses.getExp_date(),key,user.getUid());

                                            reference.child(expenses.get(position).getNodeKey()).setValue(exp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        alertDialog.dismiss();
                                                        Toast.makeText(context, "Expense Edited", Toast.LENGTH_LONG).show();
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
                                    reference.child(expenses.get(position).getNodeKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Toast.makeText(context,"deleted Successfully",Toast.LENGTH_LONG).show();
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
        return expenses.size();
    }


    public class ExpenseDataViewHolder  extends RecyclerView.ViewHolder{

        private TextView tv_expenseTitle,tv_expenseDate,tv_expenseAmount,tv_expenseRank;
        public ExpenseDataViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_expenseTitle = itemView.findViewById(R.id.tv_expenseTitle);
            tv_expenseDate = itemView.findViewById(R.id.tv_expenseDate);
            tv_expenseAmount = itemView.findViewById(R.id.tv_expenseAmount);
            tv_expenseRank = itemView.findViewById(R.id.expenseRankTV);
        }
    }

}
