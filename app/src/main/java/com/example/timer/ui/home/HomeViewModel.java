package com.example.timer.ui.home;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timer.MainActivity;

import java.util.ArrayList;

import tools.SharedPreferencesUtils;

public class HomeViewModel extends androidx.lifecycle.AndroidViewModel {

    private MutableLiveData<ArrayList<Task>> liveTodoTasks;
    private MutableLiveData<ArrayList<Task>> liveDoneTasks;
    private SharedPreferencesUtils utils;
    private Application application;
    public HomeViewModel(Application application) {
        super(application);
        liveTodoTasks = new MutableLiveData<>();
        liveDoneTasks = new MutableLiveData<>();
        this.application = application;
        initData();
    }


    public LiveData<ArrayList<Task>> getLiveTodoTasks() {
        return liveTodoTasks;
    }

    public MutableLiveData<ArrayList<Task>> getLiveDoneTasks() {
        return liveDoneTasks;
    }


    public void insertToUndo(Task task){
        ArrayList<Task> temp = liveTodoTasks.getValue();
        temp.add(task);
        liveTodoTasks.setValue(temp);
        saveToLocal();
    }

    public void saveToLocal(){
        utils.putValues(new SharedPreferencesUtils.ContentValue("todo", liveTodoTasks
                .getValue()));
        utils.putValues(new SharedPreferencesUtils.ContentValue("done", liveDoneTasks
                .getValue()));
    }
    public void insertToDone(Task task){
        ArrayList<Task> temp =liveDoneTasks.getValue();
        temp.add(task);
        liveDoneTasks.setValue(temp);
        saveToLocal();
    }
    private void initData() {
        utils = new SharedPreferencesUtils(application,"tasks");
        if(utils.getArrayList("todo") == null){
            ArrayList<Task> todoTasks = new ArrayList<>();
            Task a = new Task("点击右上角加号添加任务", "10");
            Task b = new Task("点击右方开始键开始专注学习", "10");
            Task c = new Task("长按任务删除", "10");
            Task d = new Task("也可点击左方直接完成任务", "10");
            Task e = new Task("已完成的任务会进入已完成下拉栏", "10");
            todoTasks.add(a);
            todoTasks.add(b);
            todoTasks.add(c);
            todoTasks.add(d);
            todoTasks.add(e);
            liveTodoTasks.setValue(todoTasks);
            ArrayList<Task> doneTasks = new ArrayList<>();
            liveDoneTasks.setValue(doneTasks);
            saveToLocal();
        }
        else
            liveTodoTasks.setValue(utils.getArrayList("todo"));
            liveDoneTasks.setValue(utils.getArrayList("done"));
    }
}