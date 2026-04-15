package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        rvJourneyList = findViewById(R.id.rv_journey_list);

        rvJourneyList.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList);
        rvJourneyList.setAdapter(courseAdapter);

        fetchCourses();
        setupNavigation();
    }

    private void fetchCourses() {
        db.collection("Courses").get().addOnSuccessListener(snapshots -> {
            courseList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                CourseModel course = doc.toObject(CourseModel.class);
                course.setId(doc.getId()); // Gắn ID để truy vấn Units
                courseList.add(course);
            }
            courseAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("FirebaseData", "Lỗi tải Courses", e));
    }
    private void setupNavigation() {
        navHome = findViewById(R.id.nav_home);
        navTest = findViewById(R.id.nav_test);
        navFlashcard = findViewById(R.id.nav_flashcard);
        navCategory = findViewById(R.id.nav_categrory);
        navProfile = findViewById(R.id.nav_profile);
        
        // Nút Flashcard
        navFlashcard.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Collection.class));
            overridePendingTransition(0, 0);
        });

        // Nút Category
        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Categories.class));
            overridePendingTransition(0, 0);
        });

        // Nút Profile
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Profile.class));
            overridePendingTransition(0, 0);
        });
    }
}