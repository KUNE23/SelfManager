package com.example.selfmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public TransactionDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, transaction.getTitle());
        values.put(DatabaseHelper.COLUMN_AMOUNT, transaction.getAmount());
        values.put(DatabaseHelper.COLUMN_TYPE, transaction.getType());
        values.put(DatabaseHelper.COLUMN_DATE, transaction.getDate());
        values.put(DatabaseHelper.COLUMN_NOTE, transaction.getNote());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, transaction.getCategoryId());

        return database.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.*, c." + DatabaseHelper.COLUMN_CAT_NAME + " FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                       "LEFT JOIN " + DatabaseHelper.TABLE_CATEGORIES + " c ON t." + DatabaseHelper.COLUMN_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CAT_ID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Transaction transaction = cursorToTransaction(cursor);
                transactions.add(transaction);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return transactions;
    }

    public void deleteTransaction(int id) {
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        database.delete(DatabaseHelper.TABLE_TRANSACTIONS, whereClause, whereArgs);
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
        String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));

        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));

        Transaction transaction = new Transaction(id, title, amount, type, categoryId, date, note);

        int catNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CAT_NAME);
        if (catNameIndex != -1) {
            transaction.setCategoryName(cursor.getString(catNameIndex));
        }

        return transaction;
    }
}
