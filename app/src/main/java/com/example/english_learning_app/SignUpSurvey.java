package com.example.english_learning_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpSurvey extends AppCompatActivity {

    private EditText edtName, edtDob;
    private LinearLayout btnMale, btnFemale, btnStart;
    private TextView tvMale, tvFemale;

    private String selectedGender = "Nam";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_survey);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        edtName = findViewById(R.id.edt_name);
        edtDob = findViewById(R.id.edt_dob);
        btnMale = findViewById(R.id.btn_gender_male);
        btnFemale = findViewById(R.id.btn_gender_female);
        btnStart = findViewById(R.id.btn_start);
        tvMale = (TextView) btnMale.getChildAt(0);
        tvFemale = (TextView) btnFemale.getChildAt(0);

        btnMale.setOnClickListener(v -> setGenderSelection("Nam"));
        btnFemale.setOnClickListener(v -> setGenderSelection("Nữ"));
        btnStart.setOnClickListener(v -> saveUserDataToFirestore());
    }

    // Hàm thay đổi giao diện khi chọn giới tính
    private void setGenderSelection(String gender) {
        selectedGender = gender;
        if (gender.equals("Nam")) {
            btnMale.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvMale.setTextColor(Color.parseColor("#48A05D"));

            btnFemale.setBackgroundColor(Color.parseColor("#FAF8F8"));
            tvFemale.setTextColor(Color.parseColor("#504D5D"));
        } else {
            btnFemale.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvFemale.setTextColor(Color.parseColor("#48A05D"));

            btnMale.setBackgroundColor(Color.parseColor("#FAF8F8"));
            tvMale.setTextColor(Color.parseColor("#504D5D"));
        }
    }

    private void saveUserDataToFirestore() {
        String name = edtName.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();

        if (name.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String email = currentUser.getEmail();

            // Tạo Map chứa dữ liệu người dùng
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("email", email);
            userMap.put("name", name);
            userMap.put("dob", dob);
            userMap.put("gender", selectedGender);
            userMap.put("target_score", 500);
            userMap.put("created_at", System.currentTimeMillis());

            // Lưu vào bảng "users" với ID chính là UID của Auth
            db.collection("users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thiết lập hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpSurvey.this, Home.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}