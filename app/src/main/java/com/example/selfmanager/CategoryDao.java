package com.example.selfmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public CategoryDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertCategory(String name, String type) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CAT_NAME, name);
        values.put(DatabaseHelper.COLUMN_CAT_TYPE, type);
        return database.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
    }

    public Category getCategoryByName(String name) {
        Category category = null;
        String selection = DatabaseHelper.COLUMN_CAT_NAME + " = ?";
        String[] selectionArgs = { name };
        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORIES, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ID));
                String catName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME));
                String catType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_TYPE));
                category = new Category(id, catName, catType);
            }
            cursor.close();
        }
        return category;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORIES, null, null, null, null, null, DatabaseHelper.COLUMN_CAT_NAME + " ASC");

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_TYPE));
                categories.add(new Category(id, name, type));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return categories;
    }

    public boolean isCategoryTableEmpty() {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CATEGORIES, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count == 0;
        }
        return true;
    }
}
