package com.meraz.personalassistant.adapters;

public class ToDoTaskHelper {

    private String taskDesc;
    private String taskDate;
    private String taskTime;

    public ToDoTaskHelper(String taskDesc, String taskDate, String taskTime) {
        this.taskDesc = taskDesc;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
    }

    public ToDoTaskHelper() {
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }
}
