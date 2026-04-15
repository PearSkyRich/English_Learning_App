package com.example.english_learning_app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class FragmentQuizSpeak extends Fragment {

    private QuizModel currentQuiz;
    private String selectedAnswer = "";

    private TextView tvQuestionEn, tvQuestionVi, tvOpt1, tvOpt2, tvOpt3, tvOpt4;
    private LinearLayout btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnSkip;
    private ImageView btnMicrophone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_speak, container, false);

        // 1. Kết nối Activity
        Quiz parentActivity = (Quiz) getActivity();
        if (parentActivity != null) {
            currentQuiz = parentActivity.getCurrentQuiz();
            Log.d("QuizApp", "FragmentQuizSpeak: Đã nhận dữ liệu câu hỏi");
        }

        // 2. Ánh xạ
        tvQuestionEn = view.findViewById(R.id.tv_question_en);
        tvQuestionVi = view.findViewById(R.id.tv_question_vi);
        tvOpt1 = view.findViewById(R.id.tv_option_1);
        tvOpt2 = view.findViewById(R.id.tv_option_2);
        tvOpt3 = view.findViewById(R.id.tv_option_3);
        tvOpt4 = view.findViewById(R.id.tv_option_4);

        btnOpt1 = view.findViewById(R.id.btn_option_1);
        btnOpt2 = view.findViewById(R.id.btn_option_2);
        btnOpt3 = view.findViewById(R.id.btn_option_3);
        btnOpt4 = view.findViewById(R.id.btn_option_4);

        btnSkip = view.findViewById(R.id.btn_skip);
        btnMicrophone = view.findViewById(R.id.btn_microphone);

        // 3. Đổ dữ liệu
        if (currentQuiz != null) {
            tvQuestionEn.setText(currentQuiz.getQuestion());
            tvQuestionVi.setText(currentQuiz.getWord()); // Nghĩa tiếng Việt để ở trường word

            List<String> options = currentQuiz.getOptions();
            if (options != null && options.size() >= 4) {
                tvOpt1.setText(options.get(0));
                tvOpt2.setText(options.get(1));
                tvOpt3.setText(options.get(2));
                tvOpt4.setText(options.get(3));
            }
        }

        // 4. Sự kiện chọn đáp án (Sử dụng hàm dùng chung để tránh lỗi ép kiểu)
        btnOpt1.setOnClickListener(v -> selectOption(btnOpt1, tvOpt1.getText().toString(), parentActivity));
        btnOpt2.setOnClickListener(v -> selectOption(btnOpt2, tvOpt2.getText().toString(), parentActivity));
        btnOpt3.setOnClickListener(v -> selectOption(btnOpt3, tvOpt3.getText().toString(), parentActivity));
        btnOpt4.setOnClickListener(v -> selectOption(btnOpt4, tvOpt4.getText().toString(), parentActivity));

        // 5. Microphone
        btnMicrophone.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng nhận diện giọng nói đang phát triển!", Toast.LENGTH_SHORT).show();
        });

        // 6. Bỏ qua
        btnSkip.setOnClickListener(v -> {
            if (parentActivity != null) {
                parentActivity.submitAnswer(false);
            }
        });

        return view;
    }

    private void selectOption(LinearLayout btn, String text, Quiz parentActivity) {
        // Reset màu (Màu xám nhạt mặc định của bạn)
        ColorStateList defaultColor = ColorStateList.valueOf(Color.parseColor("#faf8f8"));
        btnOpt1.setBackgroundTintList(defaultColor);
        btnOpt2.setBackgroundTintList(defaultColor);
        btnOpt3.setBackgroundTintList(defaultColor);
        btnOpt4.setBackgroundTintList(defaultColor);

        // Highlight màu xanh cho ô được chọn
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
        selectedAnswer = text;

        // So sánh ngay và gửi kết quả (Vì loại Speak này thường bấm chọn là xong luôn)
        if (parentActivity != null) {
            boolean isCorrect = selectedAnswer.trim().equalsIgnoreCase(currentQuiz.getCorrect_answer().trim());
            parentActivity.submitAnswer(isCorrect);
        }
    }
}