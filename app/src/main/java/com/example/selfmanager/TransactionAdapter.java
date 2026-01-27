package com.example.selfmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DisplayItem> displayList;
    private Context context;

    public TransactionAdapter(Context context, List<DisplayItem> displayList) {
        this.context = context;
        this.displayList = new ArrayList<>(displayList);
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == DisplayItem.VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.list_header_month, parent, false);
            return new MonthHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.transaction_list_item, parent, false);
            return new TransactionItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == DisplayItem.VIEW_TYPE_HEADER) {
            MonthHeaderViewHolder headerHolder = (MonthHeaderViewHolder) holder;
            MonthHeader monthHeader = (MonthHeader) displayList.get(position);
            headerHolder.headerTitle.setText(monthHeader.getMonthAndYear());
        } else {
            TransactionItemViewHolder itemHolder = (TransactionItemViewHolder) holder;
            Transaction transaction = (Transaction) displayList.get(position);

            itemHolder.title.setText(transaction.getTitle());
            itemHolder.date.setText(transaction.getDate());

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            String formattedAmount = currencyFormatter.format(transaction.getAmount());

            if ("income".equalsIgnoreCase(transaction.getType())) {
                itemHolder.amount.setText("+" + formattedAmount);
                itemHolder.amount.setTextColor(ContextCompat.getColor(context, R.color.green));
                itemHolder.icon.setImageResource(R.drawable.ic_trending_up);
                itemHolder.icon.setColorFilter(ContextCompat.getColor(context, R.color.green_dark));
                itemHolder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_light));
            } else { // Expense
                itemHolder.amount.setText("-" + formattedAmount);
                itemHolder.amount.setTextColor(ContextCompat.getColor(context, R.color.red));
                itemHolder.icon.setImageResource(R.drawable.ic_trending_down);
                itemHolder.icon.setColorFilter(ContextCompat.getColor(context, R.color.red_dark));
                itemHolder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_light));
            }
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public void updateData(List<DisplayItem> newList) {
        displayList.clear();
        displayList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class TransactionItemViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView iconContainer;
        ImageView icon;
        TextView title, date, amount;

        public TransactionItemViewHolder(View view) {
            super(view);
            iconContainer = view.findViewById(R.id.transaction_item_icon_container);
            icon = view.findViewById(R.id.transaction_item_icon);
            title = view.findViewById(R.id.transaction_item_title);
            date = view.findViewById(R.id.transaction_item_date);
            amount = view.findViewById(R.id.transaction_item_amount);
        }
    }

    public static class MonthHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public MonthHeaderViewHolder(View view) {
            super(view);
            headerTitle = view.findViewById(R.id.header_title);
        }
    }
}
