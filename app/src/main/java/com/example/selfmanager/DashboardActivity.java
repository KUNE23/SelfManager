package com.example.selfmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity"; // Tag for logging

    private TransactionDao transactionDao;
    private TextView balanceAmount, incomeAmount, expenseAmount, insightText;
    private LinearLayout transactionsListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        transactionDao = new TransactionDao(this);
        initializeViews();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initializeViews() {
        balanceAmount = findViewById(R.id.balance_amount);
        incomeAmount = findViewById(R.id.income_amount);
        expenseAmount = findViewById(R.id.expense_amount);
        insightText = findViewById(R.id.insight_text);
        transactionsListLayout = findViewById(R.id.transactions_list);
    }

    private void loadDashboardData() {
        transactionDao.open();
        List<Transaction> allTransactions = transactionDao.getAllTransactions();
        transactionDao.close();

        if (allTransactions == null) {
            return;
        }

        double totalIncome = 0;
        double totalExpense = 0;
        for (Transaction transaction : allTransactions) {
            if ("income".equalsIgnoreCase(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

        if (balanceAmount != null) balanceAmount.setText(currencyFormatter.format(totalIncome - totalExpense));
        if (incomeAmount != null) incomeAmount.setText(currencyFormatter.format(totalIncome));
        if (expenseAmount != null) expenseAmount.setText(currencyFormatter.format(totalExpense));

        updateRecentTransactions(allTransactions);
        updateSmartInsight(totalExpense);
    }

    private void updateRecentTransactions(List<Transaction> transactions) {
        if (transactionsListLayout == null) return;
        transactionsListLayout.removeAllViews();
        Collections.reverse(transactions);

        int count = 0;
        for (Transaction transaction : transactions) {
            if (count >= 3) break;

            View transactionView = getLayoutInflater().inflate(R.layout.transaction_item, transactionsListLayout, false);

            TextView title = transactionView.findViewById(R.id.transaction_title);
            TextView subtitle = transactionView.findViewById(R.id.transaction_subtitle);
            TextView amount = transactionView.findViewById(R.id.transaction_amount);
            ImageView icon = transactionView.findViewById(R.id.transaction_icon);

            View iconContainer = transactionView.findViewById(R.id.transaction_icon_container);

            title.setText(transaction.getTitle());
            subtitle.setText(formatDate(transaction.getDate()));

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            String formattedAmount = currencyFormatter.format(transaction.getAmount());

            int colorRes, iconRes, darkColorRes, lightColorRes;
            if ("income".equalsIgnoreCase(transaction.getType())) {
                amount.setText("+" + formattedAmount);
                colorRes = R.color.green;
                iconRes = R.drawable.trending_up;
                darkColorRes = R.color.green_dark;
                lightColorRes = R.color.green_light;
            } else {
                amount.setText("-" + formattedAmount);
                colorRes = R.color.red;
                iconRes = R.drawable.trending_down;
                darkColorRes = R.color.red_dark;
                lightColorRes = R.color.red_light;
            }

            amount.setTextColor(ContextCompat.getColor(this, colorRes));
            icon.setImageResource(iconRes);
            icon.setColorFilter(ContextCompat.getColor(this, darkColorRes));

            if (iconContainer != null) {
                iconContainer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(this, "income".equalsIgnoreCase(transaction.getType()) ? R.color.green_light : R.color.red_light)
                ));
            }

            transactionsListLayout.addView(transactionView);
            count++;
        }
    }
    
    private String formatDate(String dateStr) {
        if (dateStr == null) return "";
        SimpleDateFormat fromDB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat toDisplay = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        try {
            Date date = fromDB.parse(dateStr);
            return toDisplay.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    private void updateSmartInsight(double totalExpense) {
        if (insightText == null) {
            return;
        }
        if (totalExpense > 500000) {
            insightText.setText("You've spent quite a bit this month. Time to review your expenses!");
        } else {
            insightText.setText("Great job keeping your expenses low this month!");
        }
    }

    private void setupNavigation() {
        TextView viewAllButton = findViewById(R.id.view_all_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = findViewById(R.id.fab);

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) return true;
                if (itemId == R.id.nav_activity) {
                    startActivity(new Intent(DashboardActivity.this, TransactionHistoryActivity.class));
                    return true;
                }
                if (itemId == R.id.nav_analytics) {
                    startActivity(new Intent(DashboardActivity.this, FinancialReportsActivity.class));
                    return true;
                }
                if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            });
        } 

        if (viewAllButton != null) {
            viewAllButton.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, TransactionHistoryActivity.class)));
        }

        if (fab != null) {
            fab.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, AddTransactionActivity.class)));
        }
    }
}
