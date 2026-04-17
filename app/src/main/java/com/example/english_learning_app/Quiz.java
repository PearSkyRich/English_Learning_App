package com.example.english_learning_app;

import android.app.AlertDialog;
import android.content.Intent;
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
    private String certificateId;
    private ArrayList<String> quizIds;
    private List<QuizModel> quizList = new ArrayList<>();

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    private android.view.View viewProgressBar;
    private boolean isTestMode = false;

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
        btnClose.setOnClickListener(v -> finish());

        // 2. NHẬN DỮ LIỆU TỪ INTENT (Phải lấy đầy đủ isTestMode và certificateId)
        levelId = getIntent().getStringExtra("LEVEL_ID");
        quizIds = getIntent().getStringArrayListExtra("QUIZ_IDS");

        isTestMode = getIntent().getBooleanExtra("IS_TEST_MODE", false);
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");

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

        for (String id : quizIds) {
            tasks.add(db.collection("Quizzes").document(id).get());
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            for (Object result : results) {
                DocumentSnapshot doc = (DocumentSnapshot) result;
                if (doc.exists()) {
                    QuizModel quiz = doc.toObject(QuizModel.class);
                    quiz.setId(doc.getId());
                    quizList.add(quiz);
                }
            }
            displayQuestion();
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
        String kind = (currentQuiz.getKind() != null) ? currentQuiz.getKind() : "multiple_choice";

        Fragment fragmentToLoad = null;
        switch (kind.trim().toLowerCase()) {
            case "multiple_choice": fragmentToLoad = new FragmentQuizChoice(); break;
            case "translate": fragmentToLoad = new FragmentQuizTranslate(); break;
            case "speak": fragmentToLoad = new FragmentQuizSpeak(); break;
        }

        if (fragmentToLoad != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentToLoad)
                    .commitAllowingStateLoss();
        }
    }

    private void updateProgressBar() {
        if (viewProgressBar != null && !quizList.isEmpty()) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels - 100;
            int progressWidth = (int) (((float) currentQuestionIndex / quizList.size()) * screenWidth);

            ViewGroup.LayoutParams params = viewProgressBar.getLayoutParams();
            params.width = Math.max(20, progressWidth);
            viewProgressBar.setLayoutParams(params);
        }
    }

    public QuizModel getCurrentQuiz() {
        return quizList.get(currentQuestionIndex);
    }

    public void submitAnswer(boolean isCorrect) {
        if (isCorrect) {
            correctAnswers++;
            Toast.makeText(this, "Chính xác! ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tiếc quá, sai rồi! ", Toast.LENGTH_SHORT).show();
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            currentQuestionIndex++;
            displayQuestion();
        }, 1000);
    }

    private void finishQuizAndSaveProgress() {
        double percentage = ((double) correctAnswers / quizList.size()) * 100;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (isTestMode) {
            if (percentage >= 80.0) {
                if (certificateId != null) {
                    db.collection("users").document(userId)
                            .update("earned_certificates", FieldValue.arrayUnion(certificateId))
                            .addOnSuccessListener(aVoid -> showSuccessDialog(percentage))
                            .addOnFailureListener(e -> {
                                Log.e("QuizSave", "Lỗi lưu: " + e.getMessage());
                                Toast.makeText(this, "Lỗi lưu chứng chỉ!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("QuizSave", "Lỗi: certificateId bị null!");
                    finish();
                }
            } else {
                showFailDialog(percentage, 80);
            }
        } else {
            if (percentage >= 70.0) {
                Map<String, Object> progress = new HashMap<>();
                progress.put("completed_levels", FieldValue.arrayUnion(levelId));

                db.collection("UserProgress").document(userId)
                        .set(progress, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Hoàn thành! " + (int)percentage + "%", Toast.LENGTH_LONG).show();
                            finish();
                        });
            } else {
                showFailDialog(percentage, 70);
            }
        }
    }

    private void showSuccessDialog(double score) {
        new AlertDialog.Builder(this)
                .setTitle("Chúc mừng bạn")
                .setMessage("Bạn đạt " + (int)score + "%. Chứng chỉ đã được lưu vào Profile của bạn!")
                .setCancelable(false)
                .setPositiveButton("Tuyệt vời", (dialog, which) -> finish())
                .show();
    }

    private void showFailDialog(double score, int required) {
        new AlertDialog.Builder(this)
                .setTitle("Chưa đạt rồi!")
                .setMessage("Bạn đạt " + (int)score + "%. Cần ít nhất " + required + "% để qua bài.")
                .setCancelable(false)
                .setPositiveButton("Thử lại", (dialog, which) -> finish())
                .show();
    }
}