package com.example.selfmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinancialReportsActivity extends AppCompatActivity {

    private TransactionDao transactionDao;
    private TextView totalIncomeAmount, totalExpensesAmount, circularExpensesText, dateSelector;
    private ProgressBar incomeProgressBar, expensesProgressBar, circularProgressBar;
    private LinearLayout categoryBreakdownLayout;
    private ImageView backButton, forwardButton;

    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_reports);

        transactionDao = new TransactionDao(this);
        currentCalendar = Calendar.getInstance();

        initializeViews();
        setupNavigation();
        setupMonthNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReportsData();
    }

    private void initializeViews() {
        totalIncomeAmount = findViewById(R.id.total_income_amount);
        totalExpensesAmount = findViewById(R.id.total_expenses_amount);
        incomeProgressBar = findViewById(R.id.income_progress_bar);
        expensesProgressBar = findViewById(R.id.expenses_progress_bar);
        circularProgressBar = findViewById(R.id.circular_progress_bar);
        circularExpensesText = findViewById(R.id.circular_expense_total);
        categoryBreakdownLayout = findViewById(R.id.category_breakdown_list);
        dateSelector = findViewById(R.id.date_selector);
        backButton = findViewById(R.id.back_button);
        forwardButton = findViewById(R.id.forward_button);
    }

    private void setupMonthNavigation() {
        backButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            loadReportsData();
        });

        forwardButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            loadReportsData();
        });
    }

    private void loadReportsData() {
        // Update date selector UI
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
        dateSelector.setText(dateFormat.format(currentCalendar.getTime()));

        // Get all transactions from DB
        transactionDao.open();
        List<Transaction> allTransactions = transactionDao.getAllTransactions();
        transactionDao.close();

        // Filter transactions for the current month
        List<Transaction> monthlyTransactions = filterTransactionsForCurrentMonth(allTransactions);

        double totalIncome = 0;
        double totalExpense = 0;
        Map<String, Double> expenseByCategory = new HashMap<>();

        for (Transaction transaction : monthlyTransactions) {
            if ("income".equalsIgnoreCase(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
                String category = transaction.getCategoryName();
                expenseByCategory.put(category, expenseByCategory.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

        // Update Monthly Summary UI
        totalIncomeAmount.setText("+" + currencyFormatter.format(totalIncome));
        totalExpensesAmount.setText("-" + currencyFormatter.format(totalExpense));

        // Update progress bars
        double grandTotal = totalIncome + totalExpense;
        if (grandTotal > 0) {
            incomeProgressBar.setProgress((int) ((totalIncome / grandTotal) * 100));
            expensesProgressBar.setProgress((int) ((totalExpense / grandTotal) * 100));
        }

        // Update Category Breakdown UI
        circularExpensesText.setText(currencyFormatter.format(totalExpense));
        if (totalIncome > 0) {
            circularProgressBar.setProgress((int) ((totalExpense / totalIncome) * 100));
        } else {
            circularProgressBar.setProgress(0);
        }
        updateCategoryBreakdownUI(expenseByCategory, totalExpense);
    }

    private List<Transaction> filterTransactionsForCurrentMonth(List<Transaction> transactions) {
        List<Transaction> filteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String currentMonthStr = sdf.format(currentCalendar.getTime());

        for (Transaction transaction : transactions) {
            if (transaction.getDate() != null && transaction.getDate().startsWith(currentMonthStr)) {
                filteredList.add(transaction);
            }
        }
        return filteredList;
    }

    private void updateCategoryBreakdownUI(Map<String, Double> expenseByCategory, double totalExpense) {
        categoryBreakdownLayout.removeAllViews();
        if (totalExpense == 0) return;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        int[] colors = {R.color.blue_500, R.color.light_blue_300, R.color.grey_400}; 
        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            View itemView = getLayoutInflater().inflate(R.layout.category_breakdown_item, categoryBreakdownLayout, false);

            View categoryDot = itemView.findViewById(R.id.category_dot);
            TextView categoryName = itemView.findViewById(R.id.category_name);
            TextView categoryPercentage = itemView.findViewById(R.id.category_percentage);
            TextView categoryAmount = itemView.findViewById(R.id.category_amount);

            int percentage = (int) ((entry.getValue() / totalExpense) * 100);

            categoryDot.getBackground().setTint(ContextCompat.getColor(this, colors[colorIndex % colors.length]));
            categoryName.setText(entry.getKey());
            categoryPercentage.setText(percentage + "% of total");
            categoryAmount.setText(currencyFormatter.format(entry.getValue()));

            categoryBreakdownLayout.addView(itemView);
            colorIndex++;
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_reports);
        FloatingActionButton fab = findViewById(R.id.fab_add);

        bottomNavigationView.setSelectedItemId(R.id.nav_analytics);

        fab.setOnClickListener(v -> startActivity(new Intent(FinancialReportsActivity.this, AddTransactionActivity.class)));

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_analytics) {
                return true;
            } else if (itemId == R.id.nav_activity) {
                startActivity(new Intent(FinancialReportsActivity.this, TransactionHistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(FinancialReportsActivity.this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(FinancialReportsActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }
}
