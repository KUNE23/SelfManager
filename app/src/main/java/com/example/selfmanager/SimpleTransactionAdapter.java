package com.example.selfmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SimpleTransactionAdapter extends RecyclerView.Adapter<SimpleTransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;
    private final Context context;

    public SimpleTransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        // Set Title and Date
        holder.titleTextView.setText(transaction.getTitle());
        holder.dateTextView.setText(transaction.getDate());

        // Format and set Amount with color based on Type
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedAmount = currencyFormatter.format(transaction.getAmount());

        if ("income".equalsIgnoreCase(transaction.getType())) {
            holder.amountTextView.setText(formattedAmount);
            holder.amountTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else { // "expense"
            holder.amountTextView.setText(String.format("-%s", formattedAmount));
            holder.amountTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    /**
     * ViewHolder class to hold the views for each item in the RecyclerView.
     */
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final TextView dateTextView;
        final TextView amountTextView;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            dateTextView = itemView.findViewById(R.id.text_view_date);
            amountTextView = itemView.findViewById(R.id.text_view_amount);
        }
    }
}
