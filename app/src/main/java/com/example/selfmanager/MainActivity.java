package com.example.selfmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TransactionDao transactionDao;
    private CategoryDao categoryDao; // Add CategoryDao
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize DAOs
        transactionDao = new TransactionDao(this);
        categoryDao = new CategoryDao(this);
        transactionDao.open();
        categoryDao.open(); // Open category DAO as well

        // 2. Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Insert dummy data (only if the db is empty)
        if (categoryDao.isCategoryTableEmpty()) {
            insertDummyData();
        }

        // 4. Load data and set adapter
        loadTransactions();
    }

    private void insertDummyData() {
        // Insert categories first
        categoryDao.insertCategory("Salary", "income");
        categoryDao.insertCategory("Food & Dining", "expense");
        categoryDao.insertCategory("Entertainment", "expense");
        categoryDao.insertCategory("Transportation", "expense");

        // Get the IDs of the newly created categories
        int salaryId = categoryDao.getCategoryByName("Salary").getId();
        int foodId = categoryDao.getCategoryByName("Food & Dining").getId();
        int entertainmentId = categoryDao.getCategoryByName("Entertainment").getId();
        int transportId = categoryDao.getCategoryByName("Transportation").getId();

        // Insert transactions with the correct category IDs
        transactionDao.insertTransaction(new Transaction(0, "Monthly Salary", 3000000, "income", salaryId, "2024-05-01", "Salary for May"));
        transactionDao.insertTransaction(new Transaction(0, "Starbucks Coffee", 50000, "expense", foodId, "2024-05-28", "Coffee with client"));
        transactionDao.insertTransaction(new Transaction(0, "Netflix Subscription", 186000, "expense", entertainmentId, "2024-05-15", null));
        transactionDao.insertTransaction(new Transaction(0, "Go-Jek Ride", 25000, "expense", transportId, "2024-05-12", "Trip to office"));
    }

    private void loadTransactions() {
        List<Transaction> transactionList = transactionDao.getAllTransactions();
        // Convert List<Transaction> to List<DisplayItem> before passing to the adapter
        List<DisplayItem> displayItems = new ArrayList<>(transactionList);
        adapter = new TransactionAdapter(this, displayItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connections when the activity is destroyed
        transactionDao.close();
        categoryDao.close();
    }
}
