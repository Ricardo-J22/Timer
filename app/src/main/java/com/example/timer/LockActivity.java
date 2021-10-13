package com.example.timer;

import android.app.StatusBarManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timer.ui.home.Task;

import java.util.ArrayList;
import java.util.Calendar;

import tools.DatabaseHelper;
import tools.SharedPreferencesUtils;


public class LockActivity extends AppCompatActivity  {
    private Button finishButton;
    private Button continueButton;
    private TextView clock;
    private TextView eventText;
    private TextView lockTime;
    private CountDownTime countDownTime;
    private CountDownView cdv;
    private int taskNumber;
    private int minute;
    private int oneDayTime = 0;
    //当前时间
    private long currentTime;
    //信息保存在本地
    private SharedPreferencesUtils sharedPreferencesUtils;

    //当前时间
    private long currentHour=0;
    private long currentMinute=0;
    //完成时间
    private long finishHour=0;
    private long finishMinute=0;
    //获取时间对象
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        Bundle bundle = getIntent().getExtras();
        minute = Integer.parseInt((String) bundle.get("minute"));
        boolean forceSwitch = bundle.getBoolean("forceSwitch");
        String taskTarget = bundle.getString("taskTarget");
        taskNumber = bundle.getInt("task", -1);
        finishButton = (Button) findViewById(R.id.finishButton);
        continueButton = (Button)findViewById(R.id.continueButton);
        lockTime = (TextView)findViewById(R.id.lockTime);

        eventText = findViewById(R.id.event_text);
        if(taskTarget == null){
            eventText.setText("专注中");
        }
        else
            eventText.setText(taskTarget);

        //使用按钮
        finishButton.setOnClickListener(new finishClickListener());
        continueButton.setOnClickListener(new continueClickListener());

        //文本
        clock = (TextView) findViewById(R.id.clock);

        //倒计时动画
        cdv = (CountDownView) findViewById(R.id.tv_red_skip);

        cdv.setAddCountDownListener(new CountDownView.OnCountDownFinishListener() {
            @Override
            public void countDownFinished() {
                //时间完了 干的事情
            }
        });
        cdv.setCountdownTime(minute*60); //设置初始时间
        //创建时钟
        countDownTime = new CountDownTime(minute*60*1000,1000);

        //隐藏状态栏

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //开始锁机
        countDownTime.start();
        cdv.startCountDown();

        //根据switch是否屏蔽完成计划按钮
        if(forceSwitch){
            finishButton.setVisibility(View.INVISIBLE);
        }
        else{
            finishButton.setVisibility(View.VISIBLE);
        }

        //保存到本地
        sharedPreferencesUtils = new SharedPreferencesUtils(this,"totallocktime");

        //得到当前时间和完成时间
        getCurrentTime();
        getFinishTime();
        //设置时间
        setLockTime();

        //更新oneDayTime
        updateOneDayTime();

    }

    /*
    每天更新oneDayTime
     */
    public void updateOneDayTime() {
        DatabaseHelper sqLiteDatabase = new DatabaseHelper(LockActivity.this, "timeRecord.db", null, 1);
        SQLiteDatabase db = sqLiteDatabase.getReadableDatabase();   //读取

        ContentValues values = new ContentValues();
        //得到当前日期
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentdate = year + "-" + month + "-" + day;
        oneDayTime = sharedPreferencesUtils.getInt("oneDayTime");
        //查询数据库
        Cursor cursor = db.query("timeRecord", new String[]{"date"}, "date=?", new String[]{currentdate}
                , null, null, null);
        if (cursor.getCount() != 0) {       //第二天
            oneDayTime = 0;
        }
        cursor.close();
        db.close();
    }
    /*
    添加数据
     */
    public void addData(){
        DatabaseHelper sqLiteDatabase = new DatabaseHelper(LockActivity.this,"timeRecord.db",null,1);
        SQLiteDatabase db = sqLiteDatabase.getWritableDatabase();   //写入

        ContentValues values = new ContentValues();
        //得到当前日期
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentdate = year + "-" + month + "-" + day;
        oneDayTime = sharedPreferencesUtils.getInt("oneDayTime");
        //查询数据库
        Cursor cursor = db.query("timeRecord",new String[]{"date"},"date=?",new String[]{currentdate}
                ,null,null,null);
        if(cursor.getCount()==0){
            values.put("date",currentdate);
            values.put("everydayTime",oneDayTime);
            db.insert("timeRecord",null,values);
        }
        //更新数据
        else{
            values.put("everydayTime",oneDayTime);
            db.update("timeRecord",values,"date=?",new String[]{currentdate});
        }
        cursor.close();
        db.close();
    }

    /*
    得到当前时间
     */
    public void getCurrentTime(){
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
    }
    /*
    得到完成时间
     */
    public void getFinishTime(){
        long addHour = minute / 60;
        long addMinute = (minute - addHour*60);
        finishMinute = currentMinute + addMinute;
        finishHour = currentHour + addHour;
        if(finishMinute>=60){
            finishMinute = finishMinute - 60;
            finishHour +=1;
        }
        if(finishHour>24){
            finishHour = finishHour-24;
        }
    }
    /*
    设置锁机时间显示
     */
    public void setLockTime(){
        //补0
        String currentHour_str = String.format("%02d",currentHour);
        String currentMinute_str = String.format("%02d", currentMinute);
        String finishHour_str = String.format("%02d",finishHour);
        String finishMinute_str = String.format("%02d",finishMinute);

        String result = currentHour_str+":"+currentMinute_str+"-"+ finishHour_str+":"+finishMinute_str;
        lockTime.setText("锁机时间："+result);
    }


    /*
    重写back按键方法，屏蔽back按键
     */
    @Override
    public void onBackPressed() {
        // 屏蔽Back键
    }

    /*
    屏蔽recent按键 （这个对悬浮导航栏有效）
     */
    @Override
    protected void onPause() {
        super.onPause();
        // 屏蔽recent键
        android.app.ActivityManager activityManager = (android.app.ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }



    //设置监听外部类
    //完成计划
    class finishClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            clock.setText("00:00");
            AlertDialog.Builder builder = new AlertDialog.Builder(LockActivity.this);
            builder.setTitle("计时未完成").setMessage("中途退出，任务失败").setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            countDownTime.cancel();
                            countDownTime = null;
                            Intent intent = new Intent(LockActivity.this, MainActivity.class);
                            dialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.create().show();
        }
    }



    //继续
    class continueClickListener implements  View.OnClickListener{
        @Override
        public void onClick(View v){
            if(continueButton.isSelected()){
                continueButton.setSelected(false);
                countDownTime = new CountDownTime(currentTime,1000);//重新创建暂停时的计时
                countDownTime.start();
                cdv.continueCountDown(currentTime);     //调用动画continueCountDown方法，设置当前已经完成的进度
                continueButton.setText("暂停");          //点击继续后变为暂停
            }
            else{
                continueButton.setSelected(true);

                countDownTime.cancel();     //取消当前倒计时
                cdv.pauseCountDown();
                continueButton.setText("继续");          //点击后变为继续
            }

        }
    }
    class CountDownTime extends CountDownTimer {
        public CountDownTime(long millisInFuture,long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            currentTime = millisUntilFinished;
            //单位分
            long minute = (millisUntilFinished ) / (1000 * 60);
            //单位秒
            long second = (millisUntilFinished  - minute * (1000 * 60)) / 1000;
            //判断分钟来补0
            if(minute<10 && second >=10){
                clock.setText("0"+minute+":"+second);
            }
            else if(minute>=10 && second <10){
                clock.setText(minute+":0"+second);
            }
            else if(minute<10 && second <10){
                clock.setText("0"+minute+":0"+second);
            }
            else{
                clock.setText( minute + ":" + second );
            }

        }

        /**
         *倒计时结束后调用的
         */
        @Override
        public void onFinish() {
            clock.setText("00:00");
            AlertDialog.Builder builder = new AlertDialog.Builder(LockActivity.this);
            builder.setTitle("计时完成").setMessage("已完成事项").setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(taskNumber != -1){
                                SharedPreferencesUtils utils = new SharedPreferencesUtils(getApplicationContext(), "tasks");
                                ArrayList<Task> toDo = utils.getArrayList("todo");
                                Task temp = toDo.remove(taskNumber);
                                ArrayList<Task> done = utils.getArrayList("done");
                                done.add(temp);
                                utils.putValues(new SharedPreferencesUtils.ContentValue("todo", toDo));
                                utils.putValues(new SharedPreferencesUtils.ContentValue("done", done));

                            }
                            oneDayTime = sharedPreferencesUtils.getInt("oneDayTime");
                            oneDayTime += minute;
                            sharedPreferencesUtils.putValues(new SharedPreferencesUtils.ContentValue("oneDayTime",oneDayTime));
                            //添加数据
                            addData();

                            Intent intent = new Intent(LockActivity.this, MainActivity.class);
                            dialog.dismiss();
                            startActivity(intent);
                            finish();

                        }
                    });
            builder.create().show();

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTime != null){
            countDownTime.cancel();
            countDownTime = null;
        }
        cdv.invalidate();
    }
}