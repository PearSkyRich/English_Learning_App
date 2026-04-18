package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Nhớ thêm thư viện Glide vào build.gradle
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private RecyclerView rvJourneyList;
    private CourseAdapter courseAdapter;
    private List<CourseModel> courseList;
    private FirebaseFirestore db;
    private ImageView navHome, navTest, navFlashcard, navCategory, navProfile;
    private ImageView ivBannerAds; // Thêm ImageView cho banner
    private TextView viewAll, testbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        initViews();
        setupRecyclerViews();

        // Tải dữ liệu
        fetchCourses();
        fetchBannerAd(); // Gọi hàm tải ảnh quảng cáo

        setupNavigation();
    }

    private void initViews() {
        rvJourneyList = findViewById(R.id.rv_journey_list);
        ivBannerAds = findViewById(R.id.iv_banner_ads);

        navHome = findViewById(R.id.nav_home);
        navTest = findViewById(R.id.nav_test);
        navFlashcard = findViewById(R.id.nav_flashcard);
        navCategory = findViewById(R.id.nav_categrory);
        navProfile = findViewById(R.id.nav_profile);
        viewAll = findViewById(R.id.btn_view_all_courses);
        testbtn = findViewById(R.id.btn_take_test);
    }

    private void setupRecyclerViews() {
        rvJourneyList.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList);
        rvJourneyList.setAdapter(courseAdapter);
    }

    // --- HÀM LẤY ẢNH QUẢNG CÁO TỪ CLOUDINARY QUA FIREBASE ---
    private void fetchBannerAd() {
        db.collection("Ads").document("banner_01").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("image_url");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Dùng Glide để load ảnh vào ImageView
                            Glide.with(Home.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.cr16) // Ảnh hiện khi đang tải
                                    .error(R.drawable.cr16)       // Ảnh hiện nếu lỗi link
                                    .into(ivBannerAds);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseData", "Lỗi tải quảng cáo", e));
    }

    private void fetchCourses() {
        db.collection("Courses").limit(5).get().addOnSuccessListener(snapshots -> {
            courseList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                CourseModel course = doc.toObject(CourseModel.class);
                course.setId(doc.getId());
                courseList.add(course);
            }
            courseAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("FirebaseData", "Lỗi tải Courses", e));
    }

    private void setupNavigation() {
        viewAll.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Categories.class));
            overridePendingTransition(0, 0);
        });

        testbtn.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Test.class));
            overridePendingTransition(0, 0);
        });

        navTest.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Test.class));
            overridePendingTransition(0, 0);
        });

        navFlashcard.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Collection.class));
            overridePendingTransition(0, 0);
        });

        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Categories.class));
            overridePendingTransition(0, 0);
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Profile.class));
            overridePendingTransition(0, 0);
        });
    }
}