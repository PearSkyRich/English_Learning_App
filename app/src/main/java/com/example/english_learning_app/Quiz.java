package com.example.english_learning_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz extends AppCompatActivity {

    private String levelId;
    private ArrayList<String> quizIds;
    private List<QuizModel> quizList = new ArrayList<>();

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    private android.view.View viewProgressBar; // Thanh tiến trình màu xanh
    private int initialProgressBarWidth = 0;   // Chiều rộng ban đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Ánh xạ View
        ImageView btnClose = findViewById(R.id.btn_close);
        viewProgressBar = findViewById(R.id.view_progress_bar);

        btnClose.setOnClickListener(v -> finish()); // Thoát bài thi

        // 2. Nhận dữ liệu từ trang Document
        levelId = getIntent().getStringExtra("LEVEL_ID");
        quizIds = getIntent().getStringArrayListExtra("QUIZ_IDS");

        // 3. Tải câu hỏi
        if (quizIds != null && !quizIds.isEmpty()) {
            loadQuizzesFromFirebase();
        } else {
            Toast.makeText(this, "Không có câu hỏi nào!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadQuizzesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        // Yêu cầu Firebase tải từng ID một
        for (String id : quizIds) {
            tasks.add(db.collection("Quizzes").document(id).get());
        }

        // Chờ tải XONG TẤT CẢ mới bắt đầu làm bài
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            for (Object result : results) {
                DocumentSnapshot doc = (DocumentSnapshot) result;
                if (doc.exists()) {
                    QuizModel quiz = doc.toObject(QuizModel.class);
                    quiz.setId(doc.getId());
                    quizList.add(quiz);
                }
            }
            Log.d("QuizApp", "Đã tải: " + quizList.size() + " câu");
            displayQuestion(); // Bắt đầu hiển thị câu đầu tiên

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải dữ liệu mạng!", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayQuestion() {
        updateProgressBar();

        if (currentQuestionIndex >= quizList.size()) {
            finishQuizAndSaveProgress();
            return;
        }

        QuizModel currentQuiz = quizList.get(currentQuestionIndex);

        // THÊM DÒNG NÀY ĐỂ KIỂM TRA:
        // Nếu kind bị null, chúng ta ép nó thành "multiple_choice" để hiện thử giao diện
        String kind = currentQuiz.getKind();
        if (kind == null) {
            Log.e("QuizApp", "CẢNH BÁO: Kind bị null trên Firebase! Đang hiện tạm Multiple Choice để test.");
            kind = "multiple_choice";
        }

        Fragment fragmentToLoad = null;
        switch (kind.trim().toLowerCase()) {
            case "multiple_choice":
                fragmentToLoad = new FragmentQuizChoice();
                break;
            case "translate":
                fragmentToLoad = new FragmentQuizTranslate();
                break;
            case "speak":
                fragmentToLoad = new FragmentQuizSpeak();
                break;
        }

        if (fragmentToLoad != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentToLoad)
                    .commitAllowingStateLoss();
        }
    }

    private void updateProgressBar() {
        // Để cho đẹp, lấy chiều rộng của màn hình làm gốc, sau đó chia %
        if (viewProgressBar != null) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels - 100; // Trừ bớt padding
            int progressWidth = (int) (((float) currentQuestionIndex / quizIds.size()) * screenWidth);

            ViewGroup.LayoutParams params = viewProgressBar.getLayoutParams();
            // Nếu câu số 0 thì set width nhỏ xíu, nếu không sẽ bằng progressWidth
            params.width = (progressWidth <= 0) ? 20 : progressWidth;
            viewProgressBar.setLayoutParams(params);
        }
    }

    // ==========================================
    // CÁC HÀM CUNG CẤP CHO FRAGMENT SỬ DỤNG
    // ==========================================

    // 1. Fragment gọi hàm này để xin dữ liệu câu hỏi hiện tại
    public QuizModel getCurrentQuiz() {
        return quizList.get(currentQuestionIndex);
    }

    // 2. Fragment gọi hàm này khi người dùng bấm nút "Kiểm tra"
    public void submitAnswer(boolean isCorrect) {
        if (isCorrect) {
            correctAnswers++;
            Toast.makeText(this, "Chính xác! +1 điểm", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai rồi! Cố gắng nhé", Toast.LENGTH_SHORT).show();
        }

        // Chờ 1 giây cho người dùng đọc thông báo rồi tự chuyển câu
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            currentQuestionIndex++;
            displayQuestion();
        }, 1000);
    }

    // ==========================================
    // HÀM CHẤM ĐIỂM CUỐI CÙNG
    // ==========================================
    private void finishQuizAndSaveProgress() {
        double percentage = ((double) correctAnswers / quizList.size()) * 100;

        if (percentage >= 70.0) {
            // LƯU VÀO FIREBASE
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String, Object> progress = new HashMap<>();
            progress.put("completed_levels", FieldValue.arrayUnion(levelId));

            FirebaseFirestore.getInstance().collection("UserProgress").document(userId)
                    .set(progress, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Bạn đạt " + (int)percentage + "%! Hoàn thành xuất sắc!", Toast.LENGTH_LONG).show();
                        finish(); // Đóng quiz, quay về Document (sẽ tự hiện Tích xanh)
                    });
        } else {
            Toast.makeText(this, "Bạn chỉ đạt " + (int)percentage + "%. Cần 70% để qua bài. Thử lại nhé!", Toast.LENGTH_LONG).show();
            finish(); // Thất bại, văng ra ngoài không lưu kết quả
        }
    }
}