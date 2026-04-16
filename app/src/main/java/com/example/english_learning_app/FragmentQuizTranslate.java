package com.example.english_learning_app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Collections;
import java.util.List;

public class FragmentQuizTranslate extends Fragment {

    private QuizModel currentQuiz;
    private TextView tvQuestionEn;
    private ChipGroup cgSelectedWords, cgWordBank;
    private LinearLayout btnCheck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_translate, container, false);

        Quiz parentActivity = (Quiz) getActivity();
        if (parentActivity != null) {
            currentQuiz = parentActivity.getCurrentQuiz();
            Log.d("QuizApp", "Translate Fragment: Đã nhận dữ liệu");
        }

        tvQuestionEn = view.findViewById(R.id.tv_question_en);
        cgSelectedWords = view.findViewById(R.id.cg_selected_words);
        cgWordBank = view.findViewById(R.id.cg_word_bank);
        btnCheck = view.findViewById(R.id.btn_check);

        if (currentQuiz != null) {
            tvQuestionEn.setText(currentQuiz.getQuestion());

            List<String> shuffledWords = currentQuiz.getOptions();
            Collections.shuffle(shuffledWords);
            if (shuffledWords != null) {
                cgWordBank.removeAllViews(); // Xóa sạch view cũ để tránh trùng lặp
                for (String word : shuffledWords) {
                    addChipToWordBank(word);
                }
            }
        }

        btnCheck.setOnClickListener(v -> {
            StringBuilder answerBuilder = new StringBuilder();
            for (int i = 0; i < cgSelectedWords.getChildCount(); i++) {
                View child = cgSelectedWords.getChildAt(i);
                if (child instanceof Chip) {
                    answerBuilder.append(((Chip) child).getText().toString()).append(" ");
                }
            }

            String finalAnswer = answerBuilder.toString().trim();
            if (finalAnswer.isEmpty()) return;

            boolean isCorrect = finalAnswer.equalsIgnoreCase(currentQuiz.getCorrect_answer());
            if (parentActivity != null) {
                parentActivity.submitAnswer(isCorrect);
            }
        });

        return view;
    }

    private void addChipToWordBank(String word) {
        // Cách tạo Chip an toàn nhất để tránh lỗi hiển thị
        Chip chip = new Chip(getContext(), null, com.google.android.material.R.attr.chipStyle);
        chip.setText(word);
        chip.setCheckable(false);
        chip.setClickable(true);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
        chip.setChipStrokeColor(ColorStateList.valueOf(Color.LTGRAY));
        chip.setChipStrokeWidth(2f);

        chip.setOnClickListener(v -> {
            cgWordBank.removeView(chip);
            addChipToSelected(word);
            checkIfCanSubmit();
        });

        cgWordBank.addView(chip);
    }

    private void addChipToSelected(String word) {
        Chip chip = new Chip(getContext(), null, com.google.android.material.R.attr.chipStyle);
        chip.setText(word);
        chip.setCheckable(false);
        chip.setClickable(true);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E2F2FD"))); // Đổi màu chút cho dễ nhìn

        chip.setOnClickListener(v -> {
            cgSelectedWords.removeView(chip);
            addChipToWordBank(word);
            checkIfCanSubmit();
        });

        cgSelectedWords.addView(chip);
    }

    private void checkIfCanSubmit() {
        if (cgSelectedWords.getChildCount() > 0) {
            btnCheck.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55BA5D")));
        } else {
            btnCheck.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cdccd1")));
        }
    }
}