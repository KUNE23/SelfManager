package com.example.selfmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter adapter;
    private CategoryDao categoryDao;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryDao = new CategoryDao(this);

        ImageView backArrow = findViewById(R.id.category_back_arrow);
        backArrow.setOnClickListener(v -> finish());

        setupRecyclerView();

        FloatingActionButton fabAddCategory = findViewById(R.id.fab_add_category);
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void setupRecyclerView() {
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(this, new ArrayList<>()); 
        categoriesRecyclerView.setAdapter(adapter);
    }

    private void loadCategories() {
        categoryDao.open();
        List<Category> updatedCategories = categoryDao.getAllCategories();
        categoryDao.close();
        
        if(adapter != null){
            adapter.updateData(updatedCategories);
        }
    }

    private void showAddCategoryDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);

        final TextInputEditText categoryNameEditText = dialogView.findViewById(R.id.edit_text_category_name);
        final RadioGroup categoryTypeRadioGroup = dialogView.findViewById(R.id.radio_group_category_type);

        new AlertDialog.Builder(this)
                .setTitle("Add New Category")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String categoryName = categoryNameEditText.getText().toString().trim();

                    if (categoryName.isEmpty()) {
                        Toast.makeText(CategoryActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int selectedId = categoryTypeRadioGroup.getCheckedRadioButtonId();
                    String categoryType = (selectedId == R.id.radio_income) ? "income" : "expense";

                    categoryDao.open();
                    long result = categoryDao.insertCategory(categoryName, categoryType);
                    categoryDao.close();
                    
                    if (result != -1) {
                        loadCategories(); 
                        Toast.makeText(CategoryActivity.this, "Category added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
