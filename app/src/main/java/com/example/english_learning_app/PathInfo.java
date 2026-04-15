package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;

public class PathInfo extends AppCompatActivity {

    private TextView tvCourseTitle, tvCourseDesc, tvCourseModuleCount;
    private RecyclerView rvCourseUnits;
    private UnitDetailAdapter unitAdapter;
    private List<UnitModel> unitList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_info);

        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        tvCourseTitle = findViewById(R.id.tv_course_title);
        tvCourseDesc = findViewById(R.id.tv_course_desc);
        tvCourseModuleCount = findViewById(R.id.tv_course_module_count);
        rvCourseUnits = findViewById(R.id.rv_course_units);
        ImageView ivCourseImage = findViewById(R.id.iv_course_image);
        // Nhận dữ liệu từ Home
        Intent intent = getIntent();
        String courseId = intent.getStringExtra("COURSE_ID");
        String courseName = intent.getStringExtra("COURSE_NAME");
        String courseDesc = intent.getStringExtra("COURSE_DESC");
        int totalUnits = intent.getIntExtra("COURSE_UNITS", 0);
        String courseImage = intent.getStringExtra("COURSE_IMAGE");

        // Đổ dữ liệu lên màn hình
        if (courseName != null) tvCourseTitle.setText(courseName);
        if (courseDesc != null) tvCourseDesc.setText(courseDesc);
        tvCourseModuleCount.setText(totalUnits + " Mô-đun");

        if (courseImage != null && !courseImage.isEmpty()) {
            Glide.with(this)
                    .load(courseImage)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh hiển thị tạm lúc đang tải mạng
                    .into(ivCourseImage);
        }
        // Cài đặt RecyclerView danh sách Unit
        rvCourseUnits.setLayoutManager(new LinearLayoutManager(this));
        unitList = new ArrayList<>();
        unitAdapter = new UnitDetailAdapter(unitList);
        rvCourseUnits.setAdapter(unitAdapter);

        // Lấy danh sách bài học con
        if (courseId != null) {
            fetchUnits(courseId);
        }
    }

    private void fetchUnits(String parentCourseId) {
        // Tìm các Unit mà mảng course_id chứa ID của khóa học hiện tại
        db.collection("Units")
                .whereEqualTo("course_id", parentCourseId)
                //.orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    unitList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        UnitModel unit = doc.toObject(UnitModel.class);
                        unit.setId(doc.getId());
                        unitList.add(unit);
                    }
                    unitAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseData", "Lỗi tải Units", e));
    }
}