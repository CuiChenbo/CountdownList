package com.cuichen.countdownlist;

public class TimeBean {
    public TimeBean(String str, int time) {
        this.str = str;
        this.time = time;
    }

    private String str;
    private int time;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
