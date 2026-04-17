package com.example.english_learning_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UnitDetailAdapter extends RecyclerView.Adapter<UnitDetailAdapter.ChildViewHolder> {

    private List<UnitModel> unitList;

    public UnitDetailAdapter(List<UnitModel> unitList) {
        this.unitList = unitList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        UnitModel unit = unitList.get(position);

        // Hiển thị tên Unit
        holder.tvChildName.setText("Bài " + unit.getOrder() + ": " + unit.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CourseDetails.class);

            // Đóng gói ID và Tên của Unit gửi sang trang sau
            intent.putExtra("UNIT_ID", unit.getId());
            intent.putExtra("UNIT_NAME", unit.getName());
            intent.putExtra("UNIT_DESC", unit.getShort_description());
            intent.putExtra("UNIT_IMAGE", unit.getImage_url());
            // Bắt đầu chuyển trang
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return unitList != null ? unitList.size() : 0;
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvChildName;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildName = itemView.findViewById(R.id.tv_child_unit_name);
        }
    }
}