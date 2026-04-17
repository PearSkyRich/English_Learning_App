package com.example.english_learning_app;

import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TestTabAdapter extends RecyclerView.Adapter<TestTabAdapter.ViewHolder> {

    private List<TestTabModel> list;
    private OnTabClickListener listener;
    private int selectedPosition = 0; // Mặc định chọn tab "Tất cả" ở vị trí 0

    public interface OnTabClickListener {
        void onTabClick(String kind);
    }

    public TestTabAdapter(List<TestTabModel> list, OnTabClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng đúng layout item_tab_filter của bạn
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestTabModel item = list.get(position);
        holder.tvTabName.setText(item.getName());

        // Xử lý đổi màu khi được chọn
        if (position == selectedPosition) {
            // Màu xanh chủ đạo của bạn (#55BA5D)
            holder.layoutTab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
            holder.tvTabName.setTextColor(Color.WHITE);
        } else {
            // Màu xám nhạt mặc định
            holder.layoutTab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FAF8F8")));
            holder.tvTabName.setTextColor(Color.parseColor("#504D5D"));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Làm mới giao diện để đổi màu nút
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // Gọi callback ra Activity để load dữ liệu từ Firebase
            if (listener != null) {
                listener.onTabClick(item.getName());
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTabName;
        LinearLayout layoutTab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTabName = itemView.findViewById(R.id.tv_tab_name);
            layoutTab = itemView.findViewById(R.id.layout_tab_bg);
        }
    }
}