package com.example.timer.ui.home;

public class Task {
    private String target;
    private String duration;

    public Task(String target, String duration) {
        this.target = target;
        this.duration = duration;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
