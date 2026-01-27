package com.example.selfmanager;

public class Transaction implements DisplayItem {

    private int id;
    private String title;
    private double amount;
    private String type;
    private int categoryId;
    private String categoryName;
    private String date;
    private String note;

    public Transaction(int id, String title, double amount, String type, int categoryId, String date, String note) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.date = date;
        this.note = note;
    }

    @Override
    public int getViewType() {
        return DisplayItem.VIEW_TYPE_TRANSACTION;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
