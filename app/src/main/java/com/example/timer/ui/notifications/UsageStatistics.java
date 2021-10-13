package com.example.timer.ui.notifications;

public class UsageStatistics implements Comparable<UsageStatistics>{
    private String packageName;
    private int usageTime;

    public UsageStatistics(String packageName, int usageTime) {
        this.packageName = packageName;
        this.usageTime = usageTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getUsageTime() {
        return usageTime;
    }

    @Override
    public int compareTo(UsageStatistics o) {
        if(this.usageTime > o.usageTime){
            return -1;
        }
        else if(this.usageTime< o.usageTime){
            return  1;
        }
        else
            return 0;
    }

    @Override
    public String toString() {
        return "UsageStatistics{" +
                "packageName='" + packageName + '\'' +
                ", usageTime=" + usageTime +
                '}';
    }
}
