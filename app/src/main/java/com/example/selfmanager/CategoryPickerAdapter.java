package com.example.selfmanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryPickerAdapter extends RecyclerView.Adapter<CategoryPickerAdapter.ViewHolder> {

    private final List<Category> categories;
    private final OnCategorySelectedListener listener;
    private int selectedCategoryId;
    private final Context context;

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    public CategoryPickerAdapter(Context context, List<Category> categories, int selectedCategoryId, OnCategorySelectedListener listener) {
        this.context = context;
        this.categories = categories;
        this.selectedCategoryId = selectedCategoryId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());

        holder.checkMark.setVisibility(category.getId() == selectedCategoryId ? View.VISIBLE : View.GONE);

        boolean isIncome = "income".equalsIgnoreCase(category.getType());

        int iconRes = isIncome ? R.drawable.ic_trending_up : R.drawable.ic_trending_down;
        int bgColor = ContextCompat.getColor(context, isIncome ? R.color.green_light : R.color.red_light);
        int iconColor = ContextCompat.getColor(context, isIncome ? R.color.green_dark : R.color.red_dark);

        holder.icon.setImageResource(iconRes);
        holder.icon.setColorFilter(iconColor);

        Drawable background = ContextCompat.getDrawable(context, R.drawable.icon_background_circle);
        Drawable wrappedDrawable = DrawableCompat.wrap(background);
        DrawableCompat.setTint(wrappedDrawable, bgColor);
        holder.icon.setBackground(wrappedDrawable);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView checkMark, icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.picker_category_name);
            checkMark = itemView.findViewById(R.id.picker_category_check);
            icon = itemView.findViewById(R.id.picker_icon);
        }
    }
}
