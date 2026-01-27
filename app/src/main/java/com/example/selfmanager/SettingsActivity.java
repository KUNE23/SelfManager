package com.example.selfmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // --- Your existing navigation code ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.settings_bottom_navigation);
        FloatingActionButton fab = findViewById(R.id.settings_fab_add);
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        fab.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, AddTransactionActivity.class)));
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_settings) {
                return true;
            } else if (itemId == R.id.nav_activity) {
                startActivity(new Intent(SettingsActivity.this, TransactionHistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(SettingsActivity.this, FinancialReportsActivity.class));
                return true;
            }
            return false;
        });

        // --- New Navigation Logic for Manage Categories ---
        // Assuming your "Manage Categories" item is a View with this ID.
        // You might need to find it inside an included layout.
        View manageCategoriesItem = findViewById(R.id.manage_categories_item);
        if (manageCategoriesItem != null) {
            manageCategoriesItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start CategoryActivity
                    Intent intent = new Intent(SettingsActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
