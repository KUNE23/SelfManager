package com.example.selfmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "selfmanager.db";
    // 1. INCREMENT DATABASE VERSION to trigger onUpgrade
    private static final int DATABASE_VERSION = 3;

    // Table Names
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_CATEGORIES = "categories";

    // Transactions Table Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";
    // 2. Add new category_id column for the foreign key
    public static final String COLUMN_CATEGORY_ID = "category_id";

    // Categories Table Columns
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_NAME = "name";
    public static final String COLUMN_CAT_TYPE = "type";

    // Create Categories table query
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "(" +
            COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CAT_NAME + " TEXT NOT NULL, " +
            COLUMN_CAT_TYPE + " TEXT NOT NULL" +
            ");";

    // Updated Create Transactions table query with Foreign Key
    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_AMOUNT + " REAL, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_NOTE + " TEXT, " +
            COLUMN_CATEGORY_ID + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CAT_ID + ")" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 3. Create both tables. Categories table must be created first.
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 4. Easiest upgrade policy for development: drop and recreate tables.
        // WARNING: This will delete all existing data.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }
}
