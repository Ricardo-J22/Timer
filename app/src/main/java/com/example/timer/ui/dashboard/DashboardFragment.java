package com.example.timer.ui.dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.timer.LockActivity;
import com.example.timer.R;
import com.example.timer.databinding.FragmentDashboardBinding;

import java.util.Calendar;

import tools.DatabaseHelper;
import tools.SharedPreferencesUtils;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private Button lockStart;
    private TextView todayLockTime;
    private Button oneMinute;
    private Button fiveMinute;
    private Button twentyfiveMinute;
    private Button thirtyMinute;
    private Button oneHour;
    private Button twoHour;
    private Switch forcePattern;

    //设置锁机的时长
    private int minute = 0;

    //锁机总时间统计
    private int totalTime = 0;

    //获取时间对象
    private Calendar calendar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lockStart = binding.lockStart;
        lockStart.setOnClickListener(new lockStartClickListener());
        todayLockTime = binding.todayLockTime;
        oneMinute = binding.oneMinute;
        fiveMinute = binding.fiveMinute;
        twentyfiveMinute = binding.twentyfiveMinute;
        thirtyMinute = binding.thirtyMinute;
        oneHour = binding.oneHour;
        twoHour = binding.twoHour;
        forcePattern = binding.forcePattern;

        //设置时长按钮
        oneMinute.setOnClickListener(new oneMinuteClickListener());
        fiveMinute.setOnClickListener(new fiveMinuteClickListener());
        twentyfiveMinute.setOnClickListener(new twentyfiveMinuteClickListener());
        thirtyMinute.setOnClickListener(new thirtyMinuteClickListener());
        oneHour.setOnClickListener(new oneHourClickListener());
        twoHour.setOnClickListener(new twoHourClickListener());

        //更新锁机时间
        //SharedPreferencesUtils updateTime = new SharedPreferencesUtils(getActivity(),"totallocktime");
        //totalTime = updateTime.getInt("oneDayTime");

        totalTime = queryData();
        int lockHour = totalTime / 24;
        int lockMinute = totalTime - lockHour*60;
        todayLockTime.setText("今日自习共"+lockHour+"小时"+lockMinute+"分钟");

        return root;
    }

    //查询数据库
    public int queryData(){
        DatabaseHelper sqLiteDatabase = new DatabaseHelper(this.getContext(),"timeRecord.db",null,1);
        SQLiteDatabase db = sqLiteDatabase.getReadableDatabase();
        int time=0;
        //得到当前日期
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentdate = year + "-" + month + "-" + day;
        //查询数据库
        Cursor cursor = db.query("timeRecord",new String[]{"everydayTime"},"date=?",new String[]{currentdate}
                ,null,null,null);
        if(cursor != null && cursor.moveToFirst()) {
            time = cursor.getInt(cursor.getColumnIndex("everydayTime"));
            cursor.close();
        }
        db.close();
        return time;
    }
    //设置监听外部类
    //开始锁机
    class lockStartClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(getContext(), LockActivity.class);
            intent.putExtra("minute", minute +"");
            //检查强制开关是否打开
            if(forcePattern.isChecked()){
                intent.putExtra("forceSwitch",true);
            }
            else{
                intent.putExtra("forceSwitch",false);
            }
            getContext().startActivity(intent);
            getActivity().finish();

        }
    }
    //1分钟
    class oneMinuteClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            minute = 1;
            setUniformColor(0);
            setUniformColor(1);
        }
    }
    //5分钟
    class fiveMinuteClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            minute = 5;
            setUniformColor(0);
            setUniformColor(2);
        }
    }
    //25分钟
    class twentyfiveMinuteClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            minute = 25;
            setUniformColor(0);
            setUniformColor(3);
        }
    }
    //30分钟
    class thirtyMinuteClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            minute = 30;
            setUniformColor(0);
            setUniformColor(4);
        }
    }
    //60分钟
    class oneHourClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            minute = 60;
            setUniformColor(0);
            setUniformColor(5);
        }
    }
    //120分钟
    class twoHourClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            minute = 120;
            setUniformColor(0);
            setUniformColor(6);
        }
    }


    //设置统一的按钮颜色
    public void setUniformColor(int choice){

        switch (choice){
            case 0:
                oneMinute.setBackground(getResources().getDrawable(R.drawable.new_button_style));
                fiveMinute.setBackground(getResources().getDrawable(R.drawable.new_button_style));
                twentyfiveMinute.setBackground(getResources().getDrawable(R.drawable.new_button_style));
                thirtyMinute.setBackground(getResources().getDrawable(R.drawable.new_button_style));
                oneHour.setBackground(getResources().getDrawable(R.drawable.new_button_style));
                twoHour.setBackground(getResources().getDrawable(R.drawable.new_button_style));

                oneMinute.setTextColor(getResources().getColor(R.color.black));
                fiveMinute.setTextColor(getResources().getColor(R.color.black));
                twentyfiveMinute.setTextColor(getResources().getColor(R.color.black));
                thirtyMinute.setTextColor(getResources().getColor(R.color.black));
                oneHour.setTextColor(getResources().getColor(R.color.black));
                twoHour.setTextColor(getResources().getColor(R.color.black));
                break;
            case 1:
                oneMinute.setBackground(getResources().getDrawable(R.drawable.press_button_style));
                oneMinute.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                fiveMinute.setBackground(getResources().getDrawable(R.drawable.press_button_style));
                fiveMinute.setTextColor(getResources().getColor(R.color.white));
                break;
            case 3:
                twentyfiveMinute.setBackground(getResources().getDrawable(R.drawable.press_button_style));
                twentyfiveMinute.setTextColor(getResources().getColor(R.color.white));
                break;
            case 4:
                thirtyMinute.setBackground(getResources().getDrawable(R.drawable.press_button_style));
                thirtyMinute.setTextColor(getResources().getColor(R.color.white));
                break;
            case 5:
                oneHour.setBackground(getResources().getDrawable(R.drawable.press_button_style));
                oneHour.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                twoHour.setBackground(getResources().getDrawable(R.drawable.press_button_style));
                twoHour.setTextColor(getResources().getColor(R.color.white));
                break;
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}