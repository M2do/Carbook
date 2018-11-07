package com.example.bitmani.carbook;

public class DateData {
    private int year;
    private int month;
    private int dayOfMonth;

    public DateData() {
    }

    public DateData(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }
}
