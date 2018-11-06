package com.example.bitmani.carbook;

public class TimeData {
    private int hourOfDay;
    private int minute;

    public TimeData(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }
}
