package com.example.english_learning_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentQuizSpeak extends Fragment {

    private QuizModel currentQuiz;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private TextView tvQuestionEn, tvQuestionVi, tvStatus, tvOpt1, tvOpt2, tvOpt3, tvOpt4;
    private LinearLayout btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    private ImageView btnMicrophone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_speak, container, false);

        Quiz parentActivity = (Quiz) getActivity();
        if (parentActivity != null) {
            currentQuiz = parentActivity.getCurrentQuiz();
        }

        // 1. Ánh xạ toàn bộ View (Gồm cả các Option để người dùng đọc theo)
        tvQuestionEn = view.findViewById(R.id.tv_question_en);
        tvQuestionVi = view.findViewById(R.id.tv_question_vi);
        tvStatus = view.findViewById(R.id.tv_status); // TextView báo tình trạng "Đang nghe..."

        tvOpt1 = view.findViewById(R.id.tv_option_1);
        tvOpt2 = view.findViewById(R.id.tv_option_2);
        tvOpt3 = view.findViewById(R.id.tv_option_3);
        tvOpt4 = view.findViewById(R.id.tv_option_4);

        btnOpt1 = view.findViewById(R.id.btn_option_1);
        btnOpt2 = view.findViewById(R.id.btn_option_2);
        btnOpt3 = view.findViewById(R.id.btn_option_3);
        btnOpt4 = view.findViewById(R.id.btn_option_4);

        btnMicrophone = view.findViewById(R.id.btn_microphone);

        // 2. Đổ dữ liệu 4 Option lên màn hình để người dùng có cái mà đọc
        if (currentQuiz != null) {
            tvQuestionEn.setText(currentQuiz.getQuestion());
            tvQuestionVi.setText(currentQuiz.getWord());

            List<String> options = currentQuiz.getOptions();
            if (options != null && options.size() >= 4) {
                tvOpt1.setText(options.get(0));
                tvOpt2.setText(options.get(1));
                tvOpt3.setText(options.get(2));
                tvOpt4.setText(options.get(3));
            }
        }

        // 3. Khởi tạo Speech Recognizer
        initSpeechRecognizer(parentActivity);

        // 4. Bấm Mic để bắt đầu thu âm
        btnMicrophone.setOnClickListener(v -> {
            checkPermissionAndListen();
        });

        return view;
    }

    private void initSpeechRecognizer(Quiz parentActivity) {
        if (SpeechRecognizer.isRecognitionAvailable(getContext())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    tvStatus.setText("Listening... Speak now!");
                    tvStatus.setTextColor(Color.BLUE);
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null) {
                        String resultText = matches.get(0); // Lấy kết quả máy nghe được
                        tvStatus.setText("You said: " + resultText);

                        // So sánh kết quả thu được với đáp án đúng trong Firebase
                        boolean isCorrect = resultText.trim().equalsIgnoreCase(currentQuiz.getCorrect_answer().trim());

                        if (parentActivity != null) {
                            parentActivity.submitAnswer(isCorrect);
                        }
                    }
                }

                @Override
                public void onError(int error) {
                    String message;
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO: message = "Audio error"; break;
                        case SpeechRecognizer.ERROR_NO_MATCH: message = "No match found"; break;
                        case SpeechRecognizer.ERROR_NETWORK: message = "Network error"; break;
                        default: message = "Try again"; break;
                    }
                    tvStatus.setText(message);
                }

                // Các hàm override khác giữ trống
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {}
                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    private void checkPermissionAndListen() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }
}