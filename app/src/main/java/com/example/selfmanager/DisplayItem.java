package com.example.selfmanager;

public interface DisplayItem {
    int VIEW_TYPE_HEADER = 0;
    int VIEW_TYPE_TRANSACTION = 1;

    int getViewType();
}
