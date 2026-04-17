package com.example.english_learning_app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private LinearLayout btnLogout, btnProfile;
    private TextView tvUserName, tvUserScore, tvAccountType;
    private LinearLayout tabProgress, tabAchievements;
    private TextView tvTabProgress, tvTabAchievements;
    private RecyclerView rvCertificates, rvAchievements;
    private ImageView navHome, navProfile;
    private String selectedGender = "";
    private ImageView ivAvatar;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<CertificateModel> fullCertList = new ArrayList<>();
    private List<AchievementModel> fullAchieveList = new ArrayList<>();

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

        loadUserData();
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleImageSelected(uri);
                    }
                }
        );
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(Profile.this, SignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> showProfileBottomSheet());
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
        ivAvatar = findViewById(R.id.iv_avatar);
        ivAvatar.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
    }

    private void setupRecyclerViews() {
        rvCertificates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));

        certAdapter = new CertificateAdapter();
        achieveAdapter = new AchievementAdapter();

        rvCertificates.setAdapter(certAdapter);
        rvAchievements.setAdapter(achieveAdapter);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, Home.class));
            overridePendingTransition(0, 0);
        });
    }
    private void handleImageSelected(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // 1. Nén ảnh
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // Chất lượng 70%
            byte[] imageBytes = baos.toByteArray();

            // 2. Chuyển thành chuỗi Base64
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // 3. Hiển thị tạm lên màn hình
            ivAvatar.setImageBitmap(scaledBitmap);

            // 4. Lưu chuỗi Base64 này vào Firestore
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                db.collection("users").document(user.getUid())
                        .update("avatar_base64", encodedImage)
                        .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Lỗi lưu ảnh", Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                            String base64Image = doc.getString("avatar_base64");
                            if (base64Image != null && !base64Image.isEmpty()) {
                                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                ivAvatar.setImageBitmap(decodedBitmap);
                            }
                            List<String> myCertIds = (List<String>) doc.get("earned_certificates");
                            List<String> myAchieveIds = (List<String>) doc.get("earned_achievements");

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
                        selectTab(isProgressTabSelected);
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
        EditText edtDob = view.findViewById(R.id.edt_profile_dob);
        EditText edtTargetScore = view.findViewById(R.id.edt_profile_target_score);
        EditText edtPassword = view.findViewById(R.id.edt_profile_password);

        LinearLayout btnMale = view.findViewById(R.id.btn_profile_gender_male);
        LinearLayout btnFemale = view.findViewById(R.id.btn_profile_gender_female);
        TextView tvMale = view.findViewById(R.id.tv_profile_gender_male);
        TextView tvFemale = view.findViewById(R.id.tv_profile_gender_female);
        LinearLayout btnUpdate = view.findViewById(R.id.btn_update_profile);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            edtEmail.setText(user.getEmail());
            edtEmail.setEnabled(false);
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
                if (doc.exists()) edtName.setText(doc.getString("name"));
                if (doc.exists()) {
                    edtName.setText(doc.getString("name"));
                    edtDob.setText(doc.getString("dob"));
                    if (doc.getLong("target_score") != null) {
                        edtTargetScore.setText(String.valueOf(doc.getLong("target_score")));
                    }
                    String gender = doc.getString("gender");
                    if (gender != null) {
                        updateGenderUI(gender, btnMale, btnFemale, tvMale, tvFemale);
                    }
                }
            });
        }
        edtDob.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(this, (view1, y, m, d) -> {
                String date = String.format("%02d/%02d/%d", d, m + 1, y);
                edtDob.setText(date);
            }, year, month, day);
            datePicker.show();
        });
        btnUpdate.setOnClickListener(v -> {
            String newName = edtName.getText().toString();
            String newDob = edtDob.getText().toString();
            String scoreStr = edtTargetScore.getText().toString();
            String newPassword = edtPassword.getText().toString();

            java.util.Map<String, Object> updates = new java.util.HashMap<>();
            updates.put("name", newName);
            updates.put("dob", newDob);
            updates.put("gender", selectedGender);
            if (!scoreStr.isEmpty()) {
                updates.put("target_score", Long.parseLong(scoreStr));
            }

            if (!newPassword.isEmpty() && newPassword.length() >= 6) {
                user.updatePassword(newPassword).addOnSuccessListener(unused ->
                        Toast.makeText(this, "Đã đổi mật khẩu!", Toast.LENGTH_SHORT).show());
            }

            db.collection("users").document(user.getUid()).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        tvUserName.setText(newName); // Cập nhật tên ở màn hình Profile chính
                        dialog.dismiss();
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    });
        });
        dialog.show();
    }
    private void updateGenderUI(String gender, LinearLayout btnMale, LinearLayout btnFemale, TextView tvMale, TextView tvFemale) {
        selectedGender = gender;

        btnMale.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FAF8F8")));
        tvMale.setTextColor(Color.parseColor("#504D5D"));
        btnFemale.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FAF8F8")));
        tvFemale.setTextColor(Color.parseColor("#504D5D"));

        if (gender.equals("Nam")) {
            btnMale.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
            tvMale.setTextColor(Color.WHITE);
        } else if (gender.equals("Nữ")) {
            btnFemale.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
            tvFemale.setTextColor(Color.WHITE);
        }
    }
}