package com.example.english_learning_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<LevelModel> levelList;

    public LevelAdapter(List<LevelModel> levelList) {
        this.levelList = levelList;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo bạn gọi đúng tên file XML này nhé (item_row_lv2)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_lv2, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        LevelModel level = levelList.get(position);

        // 1. Hiển thị Tên Level (VD: Level 3)
        if (level.getName() != null) {
            holder.tvLessonType.setText(level.getName());
        }

        // 2. Hiển thị Tiêu đề (VD: Cất cánh)
        if (level.getTitle() != null) {
            holder.tvLessonTitle.setText(level.getTitle());
        }

        // 3. Hiển thị Ảnh bằng Glide
        if (level.getImage_url() != null && !level.getImage_url().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(level.getImage_url())
                    .into(holder.ivIcon);
        } else {
            // Ảnh mặc định nếu Firebase chưa có link
            holder.ivIcon.setImageResource(android.R.drawable.star_big_on);
        }
    }

    @Override
    public int getItemCount() {
        return levelList != null ? levelList.size() : 0;
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView tvLessonTitle, tvLessonType, tvQuestionCount;
        ImageView ivIcon; // Thêm biến chứa ảnh

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ khớp với các ID trong file item_row_lv2.xml của bạn
            tvLessonTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvLessonType = itemView.findViewById(R.id.tv_lesson_type);
            tvQuestionCount = itemView.findViewById(R.id.tv_lesson_question_count);
            ivIcon = itemView.findViewById(R.id.iv_lesson_thumbnail);
        }
    }
}