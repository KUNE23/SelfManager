package com.example.selfmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TransactionDao transactionDao;
    private List<Transaction> allTransactions;

    private TextView chipAll, chipIncome, chipExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        initializeViews();
        setupNavigation();
        setupChipListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayTransactions();
    }

    private void initializeViews() {
        transactionsRecyclerView = findViewById(R.id.transactions_recycler_view);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chipAll = findViewById(R.id.chip_all);
        chipIncome = findViewById(R.id.chip_income);
        chipExpenses = findViewById(R.id.chip_expenses);
        transactionDao = new TransactionDao(this);
    }

    private void loadAndDisplayTransactions() {
        transactionDao.open();
        allTransactions = transactionDao.getAllTransactions();
        transactionDao.close();
        Collections.sort(allTransactions, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        filterTransactions("all");
    }

    private void setupChipListeners() {
        chipAll.setOnClickListener(v -> filterTransactions("all"));
        chipIncome.setOnClickListener(v -> filterTransactions("income"));
        chipExpenses.setOnClickListener(v -> filterTransactions("expense"));
    }

    private void filterTransactions(String type) {
        List<Transaction> filteredList = new ArrayList<>();
        if (type.equals("all")) {
            filteredList.addAll(allTransactions);
            updateChipSelection(chipAll);
        } else {
            for (Transaction transaction : allTransactions) {
                if (transaction.getType().equalsIgnoreCase(type)) {
                    filteredList.add(transaction);
                }
            }
            updateChipSelection(type.equals("income") ? chipIncome : chipExpenses);
        }
        
        List<DisplayItem> displayList = createDisplayList(filteredList);
        if (transactionAdapter == null) {
            transactionAdapter = new TransactionAdapter(this, displayList);
            transactionsRecyclerView.setAdapter(transactionAdapter);
        } else {
            transactionAdapter.updateData(displayList);
        }
    }

    private List<DisplayItem> createDisplayList(List<Transaction> transactions) {
        Map<String, List<Transaction>> groupedTransactions = new LinkedHashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (Transaction transaction : transactions) {
            try {
                Date date = dbFormat.parse(transaction.getDate());
                String monthKey = monthFormat.format(date);
                if (!groupedTransactions.containsKey(monthKey)) {
                    groupedTransactions.put(monthKey, new ArrayList<>());
                }
                groupedTransactions.get(monthKey).add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<DisplayItem> displayItems = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : groupedTransactions.entrySet()) {
            displayItems.add(new MonthHeader(entry.getKey()));
            displayItems.addAll(entry.getValue());
        }
        return displayItems;
    }

    private void updateChipSelection(TextView selectedChip) {
        chipAll.setBackgroundResource(R.drawable.chip_background_unselected);
        chipIncome.setBackgroundResource(R.drawable.chip_background_unselected);
        chipExpenses.setBackgroundResource(R.drawable.chip_background_unselected);
        chipAll.setTextColor(Color.DKGRAY);
        chipIncome.setTextColor(Color.DKGRAY);
        chipExpenses.setTextColor(Color.DKGRAY);

        selectedChip.setBackgroundResource(R.drawable.chip_background_selected);
        selectedChip.setTextColor(Color.WHITE);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = findViewById(R.id.fab_add_transaction);
        fab.setOnClickListener(v -> startActivity(new Intent(TransactionHistoryActivity.this, AddTransactionActivity.class)));
        bottomNavigationView.setSelectedItemId(R.id.nav_activity);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_activity) return true;
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            }
            if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(this, FinancialReportsActivity.class));
                return true;
            }
            if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }
}
