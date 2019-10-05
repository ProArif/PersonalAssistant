package com.meraz.personalassistant.dailyexpenses;

public class DailyExpenses {
    String exp_title;
    String exp_amount;
    String exp_date;
    String exp_id;

    public DailyExpenses(String exp_id, String exp_title, String exp_amount, String exp_date) {
        this.exp_title = exp_title;
        this.exp_amount = exp_amount;
        this.exp_date = exp_date;
        this.exp_id = exp_id;
    }

    public DailyExpenses() {
    }

    public String getExp_id() {
        return exp_id;
    }

    public void setExp_id(String exp_id) {
        this.exp_id = exp_id;
    }

    public String getExp_title() {
        return exp_title;
    }

    public void setExp_title(String exp_title) {
        this.exp_title = exp_title;
    }

    public String getExp_amount() {
        return exp_amount;
    }

    public void setExp_amount(String exp_amount) {
        this.exp_amount = exp_amount;
    }

    public String getExp_date() {
        return exp_date;
    }

    public void setExp_date(String exp_date) {
        this.exp_date = exp_date;
    }
}
