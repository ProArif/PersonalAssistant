package com.meraz.personalassistant.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meraz.personalassistant.R;
import com.meraz.personalassistant.dailyexpenses.DailyExpenses;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseDataViewHolder> {

    private Context context;
    private List<DailyExpenses> expenses;
    private DailyExpenses dailyExpenses;

    public ExpenseAdapter(List<DailyExpenses> expenses) {

        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_exp_item,parent,false);
        dailyExpenses = new DailyExpenses();

        return new ExpenseDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseDataViewHolder holder, int position) {

        holder.tv_expenseTitle.setText(expenses.get(position).getExp_title());
        holder.tv_expenseRank.setText(String.format(Locale.US,"%d.", position + 1));
        holder.tv_expenseDate.setText(expenses.get(position).getExp_date());
        holder.tv_expenseAmount.setText(expenses.get(position).getExp_amount());
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
