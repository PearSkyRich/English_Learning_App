package com.example.english_learning_app;

import java.util.List;

public class QuizModel {
    private String id;
    private String kind; // "multiple_choice", "translate", "speak"
    private String question;
    private String word;
    private String phonetics;
    private List<String> options;
    private String correct_answer;

    public QuizModel() {} // Bắt buộc cho Firebase

    // Các hàm Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getPhonetics() { return phonetics; }
    public void setPhonetics(String phonetics) { this.phonetics = phonetics; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getCorrect_answer() { return correct_answer; }
    public void setCorrect_answer(String correct_answer) { this.correct_answer = correct_answer; }
}