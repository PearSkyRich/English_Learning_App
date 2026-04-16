package com.example.english_learning_app;

import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class FragmentQuizChoice extends Fragment {

    private QuizModel currentQuiz;
    private String selectedAnswer = "";

    private TextView tvVocab, tvPhonetic, tvOpt1, tvOpt2, tvOpt3, tvOpt4;
    private LinearLayout btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnCheck;
    private TextToSpeech tts;
    private ImageView btnSpeakNormal, btnSpeakSlow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp giao diện
        View view = inflater.inflate(R.layout.fragment_quiz_choice, container, false);

        // Kết nối với Activity Quiz
        Quiz parentActivity = (Quiz) getActivity();
        if (parentActivity != null) {
            currentQuiz = parentActivity.getCurrentQuiz();
        }

        // Ánh xạ View
        tvVocab = view.findViewById(R.id.tv_vocabulary);
        tvPhonetic = view.findViewById(R.id.tv_phonetic);
        tvOpt1 = view.findViewById(R.id.tv_option_1);
        tvOpt2 = view.findViewById(R.id.tv_option_2);
        tvOpt3 = view.findViewById(R.id.tv_option_3);
        tvOpt4 = view.findViewById(R.id.tv_option_4);

        btnOpt1 = view.findViewById(R.id.btn_option_1);
        btnOpt2 = view.findViewById(R.id.btn_option_2);
        btnOpt3 = view.findViewById(R.id.btn_option_3);
        btnOpt4 = view.findViewById(R.id.btn_option_4);
        btnCheck = view.findViewById(R.id.btn_check_answer);
        btnSpeakNormal = view.findViewById(R.id.btn_speak_normal);
        btnSpeakSlow = view.findViewById(R.id.btn_speak_slow);

        tts = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US); // Đặt ngôn ngữ là tiếng Anh Mỹ
            }
        });
        // Đổ dữ liệu
        if (currentQuiz != null) {
            tvVocab.setText(currentQuiz.getWord());
            tvPhonetic.setText(currentQuiz.getPhonetics());

            if (currentQuiz.getOptions() != null && currentQuiz.getOptions().size() >= 4) {
                tvOpt1.setText(currentQuiz.getOptions().get(0));
                tvOpt2.setText(currentQuiz.getOptions().get(1));
                tvOpt3.setText(currentQuiz.getOptions().get(2));
                tvOpt4.setText(currentQuiz.getOptions().get(3));
            }
        }

        // Phát âm bình thường (Tốc độ 1.0)
        btnSpeakNormal.setOnClickListener(v -> speak(1.0f));

        // Phát âm chậm (Tốc độ 0.5)
        btnSpeakSlow.setOnClickListener(v -> speak(0.5f));
        // Xử lý chọn đáp án
        btnOpt1.setOnClickListener(v -> selectOption(btnOpt1, tvOpt1.getText().toString()));
        btnOpt2.setOnClickListener(v -> selectOption(btnOpt2, tvOpt2.getText().toString()));
        btnOpt3.setOnClickListener(v -> selectOption(btnOpt3, tvOpt3.getText().toString()));
        btnOpt4.setOnClickListener(v -> selectOption(btnOpt4, tvOpt4.getText().toString()));

        // Nút KIỂM TRA
        btnCheck.setOnClickListener(v -> {
            if (selectedAnswer.isEmpty()) return;

            // So sánh chính xác (tránh lỗi khoảng trắng)
            boolean isCorrect = selectedAnswer.trim().equalsIgnoreCase(currentQuiz.getCorrect_answer().trim());

            if (parentActivity != null) {
                parentActivity.submitAnswer(isCorrect);
            }
        });

        return view;
    }
    private void speak(float speed) {
        if (currentQuiz != null && currentQuiz.getWord() != null) {
            tts.setSpeechRate(speed); // Cài đặt tốc độ
            tts.speak(currentQuiz.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
    private void selectOption(LinearLayout btn, String text) {
        // Reset tất cả các nút về màu xanh nhạt mặc định của bạn
        btnOpt1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e2f2fd")));
        btnOpt2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e2f2fd")));
        btnOpt3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e2f2fd")));
        btnOpt4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e2f2fd")));

        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
        selectedAnswer = text;
        btnCheck.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
    }
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}