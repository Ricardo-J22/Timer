package com.example.timer.ui.notifications;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.timer.databinding.FragmentNotificationsBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.content.Context.USAGE_STATS_SERVICE;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private PieChart chart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initChart();
        setData();
        chart.animateY(1400, Easing.EaseInOutQuad);
        getUsageTime();
        return root;
    }

    private void initChart(){

//        seekBarX.setOnSeekBarChangeListener(this);
//        seekBarY.setOnSeekBarChangeListener(this);

        chart = binding.chart1;
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

//        chart.setCenterTextTypeface(tfLight); ????????????
        chart.setCenterText("????????????");
        chart.setCenterTextSize(30f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);


        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" ???");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
//        chart.setOnChartValueSelectedListener(this);



//        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(10f);
        l.setFormSize(10f);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(30f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
//        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

    }


    private void setData() {
        ArrayList<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#CCCCFF"));
        colors.add(Color.parseColor("#33ccff"));
        colors.add(Color.parseColor("#33ff99"));
        colors.add(Color.parseColor("#99FFFF"));
        colors.add(Color.parseColor("#00CCCC"));
        ArrayList<UsageStatistics> usageStatistics = getUsageTime();
        float sum = 0;
        for (UsageStatistics statistic : usageStatistics) {
            sum += statistic.getUsageTime();
        }
        int minute = (int)sum % 60;
        int hour = (int)sum/60;
        chart.setCenterText("???????????????\n" + hour + "??????" + minute + "??????");
        for (int i = 0; i < usageStatistics.size(); i++) {
            PieEntry pieEntry = new PieEntry(usageStatistics.get(i).getUsageTime() / sum
                    ,usageStatistics.get(i).getPackageName() +"\t" +usageStatistics.get(i).getUsageTime() +"??????");
            pieEntryList.add(pieEntry);
            if(i == 4){
                break;
            }
        }

        //???????????? PieEntry
//        PieEntry CashBalance = new PieEntry(0.5f, "???????????? 1500");
//        PieEntry ConsumptionBalance = new PieEntry(0.3f, "???????????? 768");
//        PieEntry name3 = new PieEntry(0.15f, "???????????? 768");
//        PieEntry name4 = new PieEntry(0.2f, "???????????? 768");
//        PieEntry name5 = new PieEntry(0.2f, "768");
//        pieEntryList.add(CashBalance);
//        pieEntryList.add(ConsumptionBalance);
//        pieEntryList.add(name3);
//        pieEntryList.add(name4);
//        pieEntryList.add(name5);
        //?????????????????? PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "????????????");
        pieDataSet.setSliceSpace(3f);           //????????????Item???????????????
        pieDataSet.setSelectionShift(10f);      //????????????Item???????????????????????????
        pieDataSet.setColors(colors);           //???DataSet??????????????????????????????(??????Item??????)
        //???????????? PieData


        PieData pieData = new PieData(pieDataSet);
//        pieData.setDrawValues(true);            //??????????????????????????????(????????????true:????????????????????????)
            pieData.setDrawValues(false);
//            pieData.setValueTextColor(Color.parseColor("#FFFFFF"));
////        pieData.setValueTextColor(Color.BLUE);  //????????????DataSet?????????????????????????????????????????????
//        pieData.setValueTextSize(12f);          //????????????DataSet???????????????????????????????????????????????????
////        pieData.setValueTypeface(mTfLight);     //????????????DataSet???????????????????????????????????????????????????
//        pieData.setValueFormatter(new PercentFormatter());//????????????DataSet???????????????????????????????????????????????????
        chart.setData(pieData);
        chart.highlightValues(null);
        chart.setEntryLabelColor(Color.BLACK);
        chart.invalidate();                    //????????????????????????????????????????????????
    }

    public boolean isGrantedUsagePermission(Context context) {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (context.checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS)
                    == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return granted;

    }

    private ArrayList<UsageStatistics> getUsageTime() {
        if(!isGrantedUsagePermission(getContext())){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(beginCal.get(Calendar.YEAR), beginCal.get(Calendar.MONTH), beginCal.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Log.e("beginCal", beginCal.getTime().toString());
        Calendar endCal = Calendar.getInstance();
        Log.e("endCal", endCal.getTime().toString());
        UsageStatsManager manager = (UsageStatsManager) getActivity().getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(),  System.currentTimeMillis());
        ArrayList<UsageStatistics> usageStatistics = new ArrayList<>();
        for (UsageStats us : stats) {
            try {
                PackageManager pm = getActivity().getApplicationContext().getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo(us.getPackageName(), PackageManager.GET_META_DATA);
                if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) <= 0) {
//                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//                    String t=format.format(new Date(us.getLastTimeUsed()));
                    if (us.getTotalTimeInForeground() < 60000 || pm.getApplicationLabel(applicationInfo).toString().equals("Timer")) {
                        continue;
                    }

//                    sb.append(pm.getApplicationLabel(applicationInfo)+"\t"+"\t"+getTimeFromInt(us.getTotalTimeInForeground())+"\n");
                    UsageStatistics usage = new UsageStatistics(pm.getApplicationLabel(applicationInfo).toString(),
                            getTimeFromInt(us.getTotalTimeInForeground()));
                    usageStatistics.add(usage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(usageStatistics);
//        return usage;

        Log.e("msg", usageStatistics.toString());
        return usageStatistics;

    }

    private int getTimeFromInt(long totalTimeInForeground) {
        return (int)totalTimeInForeground / 1000 / 60;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}