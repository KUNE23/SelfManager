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

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.title.setText(category.getName());

        holder.subtitle.setText(category.getType() + " category"); 

        if ("income".equalsIgnoreCase(category.getType())) {
            holder.icon.setImageResource(R.drawable.trending_up);
            holder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_light));
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.green_dark));
        } else {
            holder.icon.setImageResource(R.drawable.trending_down);
            holder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_light));
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateData(List<Category> newCategories) {
        this.categoryList.clear();
        this.categoryList.addAll(newCategories);
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView iconContainer;
        ImageView icon;
        TextView title, subtitle;

        public CategoryViewHolder(View view) {
            super(view);
            iconContainer = view.findViewById(R.id.category_item_icon_container);
            icon = view.findViewById(R.id.category_item_icon);
            title = view.findViewById(R.id.category_item_title);
            subtitle = view.findViewById(R.id.category_item_subtitle);
        }
    }
}
