package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Document extends AppCompatActivity {

    private TextView tvTitle, tvDesc, tvTheory, tvExEn, tvExVi, tvBtnTakeQuiz;
    private LinearLayout layoutCompletedBadge, btnTakeQuiz;
    private ImageView btnBack;

    private String levelId;
    private ArrayList<String> quizIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thiết lập giao diện tràn viền (EdgeToEdge)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_document);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Ánh xạ các thành phần giao diện (UI)
        initViews();

        // 2. Nhận toàn bộ dữ liệu từ LevelAdapter gửi sang (Đã bọc chống Null)
        receiveDataFromIntent();

        // 3. Xử lý sự kiện bấm nút
        setupListeners();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_lesson_title);
        tvDesc = findViewById(R.id.tv_lesson_description);
        tvTheory = findViewById(R.id.tv_theory_detail);
        tvExEn = findViewById(R.id.tv_example_english);
        tvExVi = findViewById(R.id.tv_example_vietnamese);

        // ID chuẩn cho khung nút bấm
        btnTakeQuiz = findViewById(R.id.btn_take_quiz);
        btnBack = findViewById(R.id.btn_back);

        // Thẻ màu xanh báo hiệu hoàn thành bài học
        layoutCompletedBadge = findViewById(R.id.layout_completed_badge);
        if (layoutCompletedBadge != null) {
            layoutCompletedBadge.setVisibility(View.GONE); // Mặc định ẩn đi lúc mới vào
        }

        // Chữ bên trong nút Take Quiz
        tvBtnTakeQuiz = findViewById(R.id.tv_btn_take_quiz_text);
    }

    private void receiveDataFromIntent() {
        Intent intent = getIntent();
        levelId = intent.getStringExtra("LEVEL_ID");
        quizIds = intent.getStringArrayListExtra("QUIZ_IDS"); // Nhận mảng ID câu hỏi

        // Lấy dữ liệu
        String title = intent.getStringExtra("LEVEL_TITLE");
        String desc = intent.getStringExtra("LEVEL_DESC");
        String theory = intent.getStringExtra("LEVEL_THEORY");
        String exEn = intent.getStringExtra("LEVEL_EX_EN");
        String exVi = intent.getStringExtra("LEVEL_EX_VI");

        // ÁO GIÁP CHỐNG NULL: Nếu Firebase thiếu dữ liệu, tự điền chữ mặc định để không bị trắng màn hình
        if (title != null && !title.isEmpty()) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText("Đang cập nhật tiêu đề...");
        }

        if (desc != null && !desc.isEmpty()) {
            tvDesc.setText(desc);
        } else {
            tvDesc.setText("Phần giới thiệu bài học đang được cập nhật...");
        }

        if (theory != null && !theory.isEmpty()) {
            tvTheory.setText(theory);
        } else {
            tvTheory.setText("Lý thuyết đang được biên soạn, bạn vui lòng quay lại sau nhé!");
        }

        if (exEn != null && !exEn.isEmpty()) {
            tvExEn.setText(exEn);
        } else {
            tvExEn.setText("Ex: Updating example...");
        }

        if (exVi != null && !exVi.isEmpty()) {
            tvExVi.setText(exVi);
        } else {
            tvExVi.setText("VD: Đang cập nhật ví dụ...");
        }
    }

    private void setupListeners() {
        // Nút quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Nút Làm bài tập (Take the quiz)
        btnTakeQuiz.setOnClickListener(v -> {
            if (quizIds != null && !quizIds.isEmpty()) {
                // Mở trang QuizActivity và truyền dữ liệu theo
                Intent quizIntent = new Intent(Document.this, Quiz.class);
                quizIntent.putExtra("LEVEL_ID", levelId);
                quizIntent.putStringArrayListExtra("QUIZ_IDS", quizIds);
                startActivity(quizIntent);
            } else {
                Toast.makeText(Document.this, "Bài học này đang được cập nhật câu hỏi, vui lòng quay lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm này tự động chạy mỗi khi người dùng thi xong và quay trở lại màn hình Document
    @Override
    protected void onResume() {
        super.onResume();
        checkIfLevelIsCompleted();
    }

    private void checkIfLevelIsCompleted() {
        if (levelId == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Gọi lên Firebase kiểm tra xem ID level này đã nằm trong mảng hoàn thành chưa
            FirebaseFirestore.getInstance().collection("UserProgress").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("completed_levels")) {
                            List<String> completedLevels = (List<String>) documentSnapshot.get("completed_levels");

                            if (completedLevels != null && completedLevels.contains(levelId)) {
                                // NẾU ĐÃ HOÀN THÀNH:
                                // 1. Hiện cái thẻ xanh "Lessons Complete" lên
                                if (layoutCompletedBadge != null) {
                                    layoutCompletedBadge.setVisibility(View.VISIBLE);
                                }
                                // 2. Đổi chữ ở nút bấm thành Ôn tập
                                if (tvBtnTakeQuiz != null) {
                                    tvBtnTakeQuiz.setText("Ôn tập lại");
                                }
                            }
                        }
                    });
        }
    }
}