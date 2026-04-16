package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CourseDetails extends AppCompatActivity {

    private TextView tvCourseTitle, tvCourseDesc, tvLessonCount;
    private RecyclerView rvLevels;
    private ImageView btnBack;

    private LevelAdapter levelAdapter;
    private List<LevelModel> levelList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        db = FirebaseFirestore.getInstance();

        // 1. Ánh xạ các View
        tvCourseTitle = findViewById(R.id.tv_course_title);
        tvCourseDesc = findViewById(R.id.tv_course_description);
        tvLessonCount = findViewById(R.id.tv_lesson_count);
        rvLevels = findViewById(R.id.rv_levels);
        btnBack = findViewById(R.id.rzwxoal9k95);
        ImageView ivCourseIcon = findViewById(R.id.iv_course_icon);
        TextView tvDuration = findViewById(R.id.tv_course_duration); // Nếu cần dùng sau này

        // Sự kiện Quay lại
        btnBack.setOnClickListener(v -> finish());

        // 2. Nhận dữ liệu từ UnitDetailAdapter
        Intent intent = getIntent();
        String unitId = intent.getStringExtra("UNIT_ID");
        String unitName = intent.getStringExtra("UNIT_NAME");
        String unitDesc = intent.getStringExtra("UNIT_DESC");
        String unitImage = intent.getStringExtra("UNIT_IMAGE");

        // 3. Hiển thị thông tin Unit lên giao diện Header
        if (unitName != null) {
            tvCourseTitle.setText(unitName);
        }

        if (unitDesc != null && !unitDesc.isEmpty()) {
            tvCourseDesc.setText(unitDesc);
        } else if (unitName != null) {
            // Nếu Firebase chưa có mô tả, dùng câu tự động này
            tvCourseDesc.setText("Danh sách các bài học chi tiết của phần " + unitName);
        }

        // Tải ảnh Unit bằng Glide
        if (unitImage != null && !unitImage.isEmpty()) {
            Glide.with(this).load(unitImage).into(ivCourseIcon);
        }

        // 4. Khởi tạo RecyclerView (Bắt buộc phải làm TRƯỚC KHI tải dữ liệu)
        rvLevels.setLayoutManager(new LinearLayoutManager(this));
        levelList = new ArrayList<>();
        levelAdapter = new LevelAdapter(levelList);
        rvLevels.setAdapter(levelAdapter);

        // 5. Tải danh sách Levels (Gọi 1 lần duy nhất, truyền đủ 2 tham số)
        if (unitId != null && !unitId.isEmpty()) {
            fetchLevels(unitId, tvLessonCount);
        } else {
            Log.e("LoiData", "UNIT_ID bị null, không thể tải danh sách Levels");
        }
    }

    private void fetchLevels(String parentUnitId, TextView tvCount) {
        db.collection("Levels")
                .whereEqualTo("unit_id", parentUnitId)
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    levelList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        LevelModel level = doc.toObject(LevelModel.class);
                        level.setId(doc.getId());
                        levelList.add(level);
                    }
                    // Báo cho Adapter biết có dữ liệu mới để vẽ lại danh sách
                    levelAdapter.notifyDataSetChanged();

                    // Cập nhật tổng số bài học hiển thị ở trên cùng
                    tvCount.setText(levelList.size() + " bài học");
                })
                .addOnFailureListener(e -> Log.e("LoiData", "Không tải được Levels", e));
    }
}