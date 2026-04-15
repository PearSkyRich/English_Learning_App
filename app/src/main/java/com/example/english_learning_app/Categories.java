package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Categories extends AppCompatActivity {

    private RecyclerView rvCourses, rvTabs;
    private CourseAdapter courseAdapter;
    private TabFilterAdapter tabAdapter;

    private List<CourseModel> originalCourseList;
    private List<CourseModel> displayCourseList;
    private List<String> dynamicTabList; // Chứa danh sách các Tag tự động

    private FirebaseFirestore db;
    private EditText edtSearch;
    private ImageView navHome, navTest, navFlashcard, navCategory, navProfile;
    private String currentCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerViews();
        fetchCoursesAndGenerateTabs();
        setupSearch();
        setupNavigation();
    }

    private void initViews() {
        rvCourses = findViewById(R.id.rv_courses_category);
        rvTabs = findViewById(R.id.rv_category_tabs);
        edtSearch = findViewById(R.id.edt_search);

        ImageView btnBack = findViewById(R.id.btn_back);
        if(btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerViews() {
        // Cài đặt danh sách khóa học (dọc)
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        originalCourseList = new ArrayList<>();
        displayCourseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(displayCourseList);
        rvCourses.setAdapter(courseAdapter);

        // Cài đặt danh sách Tab (ngang)
        rvTabs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dynamicTabList = new ArrayList<>();
    }

    private void fetchCoursesAndGenerateTabs() {
        db.collection("Courses").get().addOnSuccessListener(snapshots -> {
            originalCourseList.clear();
            dynamicTabList.clear();

            // LUÔN LUÔN có tab "Tất cả" đứng đầu tiên
            dynamicTabList.add("Tất cả");

            for (QueryDocumentSnapshot doc : snapshots) {
                CourseModel course = doc.toObject(CourseModel.class);
                course.setId(doc.getId());
                originalCourseList.add(course);

                // TỰ ĐỘNG GOM TAG: Nếu tag chưa có trong list thì thêm vào
                String tag = course.getTag();
                if (tag != null && !tag.trim().isEmpty() && !dynamicTabList.contains(tag)) {
                    dynamicTabList.add(tag);
                }
            }

            // Đổ dữ liệu ra danh sách khóa học
            displayCourseList.clear();
            displayCourseList.addAll(originalCourseList);
            courseAdapter.notifyDataSetChanged();

            // Đổ dữ liệu ra danh sách Tab
            tabAdapter = new TabFilterAdapter(dynamicTabList, selectedTab -> {
                // Sự kiện khi bấm vào 1 tab bất kỳ
                filterCourses(edtSearch.getText().toString().trim(), selectedTab);
            });
            rvTabs.setAdapter(tabAdapter);

        }).addOnFailureListener(e -> Log.e("FirebaseData", "Lỗi tải Courses", e));
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString().trim(), currentCategory);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm lọc dữ liệu kết hợp Tìm kiếm & Bấm Tab
    private void filterCourses(String keyword, String category) {
        currentCategory = category;
        displayCourseList.clear();

        for (CourseModel course : originalCourseList) {
            boolean matchKeyword = true;
            boolean matchCategory = true;

            if (!keyword.isEmpty()) {
                matchKeyword = course.getName().toLowerCase().contains(keyword.toLowerCase());
            }

            if (!category.equals("Tất cả")) {
                if (course.getTag() != null) {
                    matchCategory = course.getTag().equalsIgnoreCase(category);
                } else {
                    matchCategory = false;
                }
            }

            if (matchKeyword && matchCategory) {
                displayCourseList.add(course);
            }
        }
        courseAdapter.notifyDataSetChanged();
    }

    private void setupNavigation() {

        navHome = findViewById(R.id.nav_home);
        navTest = findViewById(R.id.nav_test);
        navFlashcard = findViewById(R.id.nav_flashcard);
        navCategory = findViewById(R.id.nav_categrory);
        navProfile = findViewById(R.id.nav_profile);

        // Nút Flashcard
        navFlashcard.setOnClickListener(v -> {
            startActivity(new Intent(Categories.this, FlashCard.class));
            overridePendingTransition(0, 0);
        });

        // Nút Category
        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(Categories.this, Collection.class));
            overridePendingTransition(0, 0);
        });

        // Nút Profile
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(Categories.this, Profile.class));
            overridePendingTransition(0, 0);
        });
    }
}