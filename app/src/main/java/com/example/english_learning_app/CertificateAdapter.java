package com.example.english_learning_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {

    private List<CertificateModel> listData = new ArrayList<>();

    // Hàm này được Profile.java gọi để nạp dữ liệu mới (5 cái hoặc tất cả)
    public void setData(List<CertificateModel> newData) {
        this.listData = newData;
        notifyDataSetChanged(); // Báo cho RecyclerView biết để vẽ lại màn hình
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_certificate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CertificateModel model = listData.get(position);
        holder.tvName.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_cert_title); // Ánh xạ ID trong file profile_item_certificate.xml
        }
    }
}