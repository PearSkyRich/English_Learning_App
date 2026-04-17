package com.example.english_learning_app;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TabFilterAdapter extends RecyclerView.Adapter<TabFilterAdapter.TabViewHolder> {

    private List<String> tabList;
    private int selectedPosition = 0;
    private OnTabClickListener listener;


    public interface OnTabClickListener {
        void onTabClick(String tagName);
    }

    public TabFilterAdapter(List<String> tabList, OnTabClickListener listener) {
        this.tabList = tabList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab_filter, parent, false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        String tagName = tabList.get(position);
        holder.tvTabName.setText(tagName);

        if (selectedPosition == position) {
            holder.layoutBg.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#55BA5D")));
            holder.tvTabName.setTextColor(Color.WHITE);
        } else {
            holder.layoutBg.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FAF8F8")));
            holder.tvTabName.setTextColor(Color.parseColor("#696674"));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPos);
            notifyItemChanged(selectedPosition);
            listener.onTabClick(tagName);
        });
    }

    @Override
    public int getItemCount() {
        return tabList != null ? tabList.size() : 0;
    }

    public static class TabViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutBg;
        TextView tvTabName;

        public TabViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBg = itemView.findViewById(R.id.layout_tab_bg);
            tvTabName = itemView.findViewById(R.id.tv_tab_name);
        }
    }
}