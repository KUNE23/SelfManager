package com.example.selfmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private static final String TAG = "AddTransactionActivity";
    private TextView amountText;
    private StringBuilder amountBuilder = new StringBuilder("0");

    private TextView expenseButton, incomeButton;
    private View dateRow, categoryRow, noteRow;
    private TextView dateValue, categoryValue, noteValue;

    private String currentType = "expense";
    private String currentDate;
    private int currentCategoryId = -1;
    private String currentCategoryName = "Dining & Food";
    private String currentNote = null;

    private TransactionDao transactionDao;
    private CategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        transactionDao = new TransactionDao(this);
        categoryDao = new CategoryDao(this);

        initializeViews();
        setDefaultValues();
        setupClickListeners();
    }

    private void initializeViews() {
        amountText = findViewById(R.id.amount_text);
        expenseButton = findViewById(R.id.expense_button);
        incomeButton = findViewById(R.id.income_button);
        dateRow = findViewById(R.id.date_row);
        categoryRow = findViewById(R.id.category_row);
        noteRow = findViewById(R.id.note_row);
        dateValue = dateRow.findViewById(R.id.row_value);
        categoryValue = categoryRow.findViewById(R.id.row_value);
        noteValue = noteRow.findViewById(R.id.row_value);
    }

    private void setDefaultValues() {
        Calendar calendar = Calendar.getInstance();
        updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        categoryDao.open();
        Category defaultCategory = categoryDao.getCategoryByName(currentCategoryName);
        if(defaultCategory != null){
            currentCategoryId = defaultCategory.getId();
        }
        categoryDao.close();
        categoryValue.setText(currentCategoryName);

        noteValue.setText("Add a note...");
        noteValue.setTextColor(Color.GRAY);
    }

    private void setupClickListeners() {
        findViewById(R.id.cancel_button).setOnClickListener(v -> finish());
        expenseButton.setOnClickListener(v -> selectType("expense"));
        incomeButton.setOnClickListener(v -> selectType("income"));
        dateRow.setOnClickListener(v -> showDatePickerDialog());
        categoryRow.setOnClickListener(v -> showCategoryBottomSheet());
        noteRow.setOnClickListener(v -> showNoteInputDialog());

        GridLayout numpad = findViewById(R.id.numpad);
        for (int i = 0; i < numpad.getChildCount(); i++) {
            View view = numpad.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setOnClickListener(this::onNumpadClick);
            } else if (view instanceof android.widget.ImageView) {
                view.setOnClickListener(v -> {
                    if (amountBuilder.length() > 1) {
                        amountBuilder.deleteCharAt(amountBuilder.length() - 1);
                    } else {
                        amountBuilder.setLength(0);
                        amountBuilder.append("0");
                    }
                    updateAmountText();
                });
            }
        }
        findViewById(R.id.save_button).setOnClickListener(v -> saveTransaction());
    }

    private void showCategoryBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_category_picker, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView categoryPickerRecyclerView = bottomSheetView.findViewById(R.id.category_picker_recycler_view);
        categoryPickerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryDao.open();
        List<Category> allCategories = categoryDao.getAllCategories();
        categoryDao.close();

        List<Category> filteredCategories = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getType().equalsIgnoreCase(currentType)) {
                filteredCategories.add(category);
            }
        }

        CategoryPickerAdapter pickerAdapter = new CategoryPickerAdapter(this, filteredCategories, currentCategoryId, category -> {
            currentCategoryId = category.getId();
            currentCategoryName = category.getName();
            categoryValue.setText(currentCategoryName);
            bottomSheetDialog.dismiss();
        });

        categoryPickerRecyclerView.setAdapter(pickerAdapter);
        bottomSheetDialog.show();
    }

    private void onNumpadClick(View view) {
        TextView key = (TextView) view;
        String text = key.getText().toString();

        if (amountBuilder.toString().equals("0") && !text.equals(".")) {
            amountBuilder.setLength(0);
        }

        if (text.equals(".") && amountBuilder.toString().contains(".")) {
            return;
        }
        amountBuilder.append(text);
        updateAmountText();
    }

    private void updateAmountText() {
        try {
            double value = Double.parseDouble(amountBuilder.toString());
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMinimumFractionDigits(amountBuilder.toString().contains(".") ? amountBuilder.length() - amountBuilder.indexOf(".") - 1 : 0);
            amountText.setText(formatter.format(value));
        } catch (NumberFormatException e) {
            amountText.setText(amountBuilder.toString());
        }
    }

    private void selectType(String type) {
        currentType = type;
        if (type.equals("expense")) {
            expenseButton.setBackgroundResource(R.drawable.toggle_selected_background);
            incomeButton.setBackgroundColor(Color.TRANSPARENT);
        } else {
            incomeButton.setBackgroundResource(R.drawable.toggle_selected_background);
            expenseButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> updateDate(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        dateValue.setText(displayFormat.format(calendar.getTime()));
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = dbFormat.format(calendar.getTime());
    }

    private void showNoteInputDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (currentNote != null) {
            input.setText(currentNote);
        }

        new AlertDialog.Builder(this)
                .setTitle("Add a Note")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String note = input.getText().toString().trim();
                    if (note.isEmpty()) {
                        currentNote = null;
                        noteValue.setText("Add a note...");
                        noteValue.setTextColor(Color.GRAY);
                    } else {
                        currentNote = note;
                        noteValue.setText(currentNote);
                        noteValue.setTextColor(Color.BLACK);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void saveTransaction() {
        String amountString = amountBuilder.toString();
        if (amountString.equals("0")) {
            Toast.makeText(this, "Amount cannot be zero", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentCategoryId == -1) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountString);
            String title = currentCategoryName;

            transactionDao.open();
            long result = transactionDao.insertTransaction(new Transaction(0, title, amount, currentType, currentCategoryId, currentDate, currentNote));
            transactionDao.close();

            if (result != -1) {
                Toast.makeText(this, "Transaction saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR CRASH: " + e.getMessage());
        }
    }
}
