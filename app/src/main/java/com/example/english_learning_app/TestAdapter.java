package com.example.english_learning_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private List<TestModel> testList;

    public TestAdapter(List<TestModel> testList) {
        this.testList = testList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_lv1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestModel test = testList.get(position);

        holder.tvType.setText("Kỳ thi • " + test.getKind());
        holder.tvTitle.setText(test.getTest_name());
        holder.tvType.setText(test.getKind());
        holder.tvTitle.setText(test.getTest_name());
        // Xử lý Expand/Collapse đơn giản để xem mô tả trước khi thi
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Quiz.class);
            intent.putStringArrayListExtra("QUIZ_IDS", (ArrayList<String>) test.getQuizzes_ids());
            intent.putExtra("CERTIFICATE_ID", test.getCertificate_id());
            intent.putExtra("IS_TEST_MODE", true);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return testList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvTitle;
        ImageView ivArrow;
        RecyclerView rvChild;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_unit_type);
            tvTitle = itemView.findViewById(R.id.tv_unit_title);
            ivArrow = itemView.findViewById(R.id.iv_expand_arrow);
            rvChild = itemView.findViewById(R.id.rv_child_units);
        }
    }
}