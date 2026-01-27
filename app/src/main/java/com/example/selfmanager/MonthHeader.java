package com.example.selfmanager;

public class MonthHeader implements DisplayItem {

    private String monthAndYear;

    public MonthHeader(String monthAndYear) {
        this.monthAndYear = monthAndYear;
    }

    public String getMonthAndYear() {
        return monthAndYear;
    }

    @Override
    public int getViewType() {
        return DisplayItem.VIEW_TYPE_HEADER;
    }
}
