package com.example.english_learning_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private LinearLayout btnLogout, btnProfile;
    private TextView tvUserName, tvUserScore, tvAccountType;
    private LinearLayout tabProgress, tabAchievements;
    private TextView tvTabProgress, tvTabAchievements;
    private RecyclerView rvCertificates, rvAchievements;
    private ImageView navHome, navProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Danh sách lưu trữ dữ liệu thật từ Firebase
    private List<CertificateModel> fullCertList = new ArrayList<>();
    private List<AchievementModel> fullAchieveList = new ArrayList<>();

    // Khai báo Adapter
    private CertificateAdapter certAdapter;
    private AchievementAdapter achieveAdapter;

    private boolean isProgressTabSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerViews();
        setupNavigation();

        // Tải dữ liệu người dùng và danh sách từ Firebase
        loadUserData();

        // Sự kiện Đăng xuất
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(Profile.this, SignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> showProfileBottomSheet());

        // Sự kiện chuyển Tab
        tabProgress.setOnClickListener(v -> selectTab(true));
        tabAchievements.setOnClickListener(v -> selectTab(false));
    }

    private void initViews() {
        btnLogout = findViewById(R.id.btn_logout);
        btnProfile = findViewById(R.id.btn_profile);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserScore = findViewById(R.id.tv_user_score);
        tvAccountType = findViewById(R.id.tv_account_type);
        tabProgress = findViewById(R.id.tab_progress);
        tabAchievements = findViewById(R.id.tab_achievements);
        tvTabProgress = findViewById(R.id.tv_tab_progress);
        tvTabAchievements = findViewById(R.id.tv_tab_achievements);
        rvCertificates = findViewById(R.id.rv_certificates);
        rvAchievements = findViewById(R.id.rv_achievements);
        navHome = findViewById(R.id.nav_home);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupRecyclerViews() {
        // Thiết lập hướng cuộn cho RecyclerView
        rvCertificates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter
        certAdapter = new CertificateAdapter();
        achieveAdapter = new AchievementAdapter();

        // Gán adapter vào view
        rvCertificates.setAdapter(certAdapter);
        rvAchievements.setAdapter(achieveAdapter);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, Home.class));
            overridePendingTransition(0, 0);
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            tvUserName.setText(doc.getString("name"));
                            Long score = doc.getLong("target_score");
                            if (score != null) tvUserScore.setText(String.valueOf(score));

                            // Lấy mảng ID từ User
                            List<String> myCertIds = (List<String>) doc.get("earned_certificates");
                            List<String> myAchieveIds = (List<String>) doc.get("earned_achievements");

                            // Truy vấn chi tiết từ bảng gốc
                            fetchDetailsFromRootTables(myCertIds, myAchieveIds);
                        }
                    });
        }
    }

    private void fetchDetailsFromRootTables(List<String> certIds, List<String> achieveIds) {
        // 1. Lấy dữ liệu Chứng chỉ
        if (certIds != null && !certIds.isEmpty()) {
            db.collection("Certificate").whereIn(FieldPath.documentId(), certIds).get()
                    .addOnSuccessListener(snapshots -> {
                        fullCertList.clear();
                        for (DocumentSnapshot d : snapshots) {
                            fullCertList.add(new CertificateModel(d.getId(), d.getString("Name")));
                        }
                        selectTab(isProgressTabSelected); // Cập nhật danh sách hiển thị
                    });
        }

        // 2. Lấy dữ liệu Thành tựu (Có thêm description)
        if (achieveIds != null && !achieveIds.isEmpty()) {
            db.collection("Achievement").whereIn(FieldPath.documentId(), achieveIds).get()
                    .addOnSuccessListener(snapshots -> {
                        fullAchieveList.clear();
                        for (DocumentSnapshot d : snapshots) {
                            fullAchieveList.add(new AchievementModel(
                                    d.getId(),
                                    d.getString("Name"),
                                    d.getString("description")
                            ));
                        }
                        selectTab(isProgressTabSelected);
                    });
        }
    }

    private void selectTab(boolean isProgressSelected) {
        isProgressTabSelected = isProgressSelected;

        if (isProgressSelected) {
            // Sáng tab Tiến trình
            tvTabProgress.setTextColor(Color.parseColor("#55BA5D"));
            tvTabProgress.setTypeface(null, Typeface.BOLD);
            tvTabAchievements.setTextColor(Color.parseColor("#B9B9B9"));
            tvTabAchievements.setTypeface(null, Typeface.NORMAL);

            // Chứng chỉ: Hiện tất cả | Thành tựu: Hiện 5
            certAdapter.setData(fullCertList);
            achieveAdapter.setData(fullAchieveList.subList(0, Math.min(5, fullAchieveList.size())));
            rvCertificates.setVisibility(View.VISIBLE);
            rvAchievements.setVisibility(View.GONE);
        } else {
            // Sáng tab Thành tựu
            tvTabAchievements.setTextColor(Color.parseColor("#55BA5D"));
            tvTabAchievements.setTypeface(null, Typeface.BOLD);
            tvTabProgress.setTextColor(Color.parseColor("#B9B9B9"));
            tvTabProgress.setTypeface(null, Typeface.NORMAL);

            // Thành tựu: Hiện tất cả | Chứng chỉ: Hiện 5
            achieveAdapter.setData(fullAchieveList);
            certAdapter.setData(fullCertList.subList(0, Math.min(5, fullCertList.size())));
            rvAchievements.setVisibility(View.VISIBLE);
            rvCertificates.setVisibility(View.GONE);
        }
    }

    private void showProfileBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_profile, null);
        dialog.setContentView(view);

        EditText edtName = view.findViewById(R.id.edt_profile_name);
        EditText edtEmail = view.findViewById(R.id.edt_profile_email);
        LinearLayout btnUpdate = view.findViewById(R.id.btn_update_profile);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            edtEmail.setText(user.getEmail());
            edtEmail.setEnabled(false);
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
                if (doc.exists()) edtName.setText(doc.getString("name"));
            });
        }

        btnUpdate.setOnClickListener(v -> {
            String newName = edtName.getText().toString();
            db.collection("users").document(user.getUid()).update("name", newName)
                    .addOnSuccessListener(aVoid -> {
                        tvUserName.setText(newName);
                        dialog.dismiss();
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    });
        });
        dialog.show();
    }
}