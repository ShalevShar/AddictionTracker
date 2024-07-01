package com.example.common;

public class RelapseItem {
    protected String date;
    protected String description;
    protected String dose;

    public RelapseItem(String date, String description, String dose) {
        this.date = date;
        this.description = description;
        this.dose = dose;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getDose() {
        return dose;
    }
}

