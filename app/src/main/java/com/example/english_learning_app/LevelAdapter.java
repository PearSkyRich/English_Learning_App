package com.example.english_learning_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<LevelModel> levelList;

    public LevelAdapter(List<LevelModel> levelList) {
        this.levelList = levelList;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_lv2, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        LevelModel level = levelList.get(position);

        holder.tvLessonType.setText(level.getName() != null ? level.getName() : "Level " + (position + 1));
        holder.tvLessonTitle.setText(level.getTitle() != null ? level.getTitle() : "Đang cập nhật...");

        if (level.getImage_url() != null && !level.getImage_url().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(level.getImage_url()).into(holder.ivIcon);
        }

        // Sự kiện Click để chuyển sang trang Document
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Document.class);

            // Đóng gói dữ liệu gửi đi
            intent.putExtra("LEVEL_ID", level.getId());
            intent.putExtra("LEVEL_TITLE", level.getTitle());
            intent.putExtra("LEVEL_DESC", level.getDescription());
            intent.putExtra("LEVEL_THEORY", level.getTheory_detail());
            intent.putExtra("LEVEL_EX_EN", level.getExample_english());
            intent.putExtra("LEVEL_EX_VI", level.getExample_viet());

            // Chuyển mảng Quiz IDs
            if (level.getQuiz_ids() != null) {
                intent.putStringArrayListExtra("QUIZ_IDS", new ArrayList<>(level.getQuiz_ids()));
            }

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView tvLessonTitle, tvLessonType;
        ImageView ivIcon;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLessonTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvLessonType = itemView.findViewById(R.id.tv_lesson_type);
            ivIcon = itemView.findViewById(R.id.iv_lesson_thumbnail);
        }
    }
}