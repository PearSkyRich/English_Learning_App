package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test extends AppCompatActivity {

    private RecyclerView rvTests, rvTabs;
    private TestAdapter testAdapter;
    private TestTabAdapter tabAdapter; // Sử dụng adapter Tab của bạn
    private List<TestModel> testList;
    private List<TestTabModel> kindList; // Danh sách các loại bài thi (Tabs)
    private FirebaseFirestore db;
    private EditText edtSearch;
    private ImageView btnBack, navHome, navProfile, navCategory, navFlashcard, navTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        db = FirebaseFirestore.getInstance();
        initViews();
        setupRecyclerViews();

        // 1. Lấy danh sách Kind từ DB để hiện lên Tab
        fetchKindsFromFirestore();

        // 2. Mặc định tải tất cả bài test
        fetchTestsByKind("Tất cả");

        setupNavigation();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvTests = findViewById(R.id.rv_tests_list);
        rvTabs = findViewById(R.id.rv_test_kind_tabs);
        edtSearch = findViewById(R.id.edt_search_test);
        btnBack = findViewById(R.id.btn_back);

        navHome = findViewById(R.id.nav_home);
        navTest = findViewById(R.id.nav_test);
        navFlashcard = findViewById(R.id.nav_flashcard);
        navCategory = findViewById(R.id.nav_categrory);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupRecyclerViews() {
        // Setup List Test
        testList = new ArrayList<>();
        testAdapter = new TestAdapter(testList);
        rvTests.setLayoutManager(new LinearLayoutManager(this));
        rvTests.setAdapter(testAdapter);

        // Setup Tabs
        kindList = new ArrayList<>();
        tabAdapter = new TestTabAdapter(kindList, kind -> {

            fetchTestsByKind(kind);
        });
        rvTabs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTabs.setAdapter(tabAdapter);
    }

    private void fetchKindsFromFirestore() {
        db.collection("Test").get().addOnSuccessListener(snapshots -> {
            Set<String> uniqueKinds = new HashSet<>();
            uniqueKinds.add("Tất cả"); // Luôn có tab Tất cả

            for (QueryDocumentSnapshot doc : snapshots) {
                String kind = doc.getString("kind");
                if (kind != null && !kind.isEmpty()) {
                    uniqueKinds.add(kind);
                }
            }

            kindList.clear();
            for (String k : uniqueKinds) {
                kindList.add(new TestTabModel(k, k.equals("Tất cả")));
            }
            tabAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("Firestore", "Lỗi tải Kinds: " + e.getMessage()));
    }

    private void fetchTestsByKind(String kind) {
        Query query;
        if (kind.equals("Tất cả")) {
            query = db.collection("Test");
        } else {
            query = db.collection("Test").whereEqualTo("kind", kind);
        }

        query.get().addOnSuccessListener(snapshots -> {
            testList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                TestModel test = doc.toObject(TestModel.class);
                test.setId(doc.getId());
                testList.add(test);
            }
            testAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("Firestore", "Lỗi tải Test: " + e.getMessage()));
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, Home.class));
            overridePendingTransition(0, 0);
        });
        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(this, Categories.class));
            overridePendingTransition(0, 0);
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
            overridePendingTransition(0, 0);
        });
        navFlashcard.setOnClickListener(v -> {
            startActivity(new Intent(this, Collection.class));
            overridePendingTransition(0, 0);
        });
    }
}