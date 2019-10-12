package com.meraz.personalassistant.helpers;

public class ToDoTaskHelper {

    private String taskDesc;
    private String taskDate;
    private String taskTime;
    private String nodeKey;
    private String user_id;

    public ToDoTaskHelper(String taskDesc, String taskDate, String taskTime,String nodeKey,String user_id) {
        this.taskDesc = taskDesc;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.nodeKey = nodeKey;
        this.user_id = user_id;

    }

    public ToDoTaskHelper() {
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
