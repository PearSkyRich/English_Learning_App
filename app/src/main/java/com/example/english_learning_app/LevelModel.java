package com.example.english_learning_app;

import java.util.List;

public class LevelModel {
    private String id;
    private String unit_id;
    private String name;
    private String title;
    private int order;
    private String image_url;

    private String description;
    private String theory_detail;
    private String example_english;
    private String example_viet;

    // Danh sách ID các câu hỏi cho bài thi
    private List<String> quiz_ids;

    public LevelModel() {}

    // Getter và Setter cho tất cả các trường
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUnit_id() { return unit_id; }
    public void setUnit_id(String unit_id) { this.unit_id = unit_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTheory_detail() { return theory_detail; }
    public void setTheory_detail(String theory_detail) { this.theory_detail = theory_detail; }

    public String getExample_english() { return example_english; }
    public void setExample_english(String example_english) { this.example_english = example_english; }

    public String getExample_viet() { return example_viet; }
    public void setExample_viet(String example_viet) { this.example_viet = example_viet; }

    public List<String> getQuiz_ids() { return quiz_ids; }
    public void setQuiz_ids(List<String> quiz_ids) { this.quiz_ids = quiz_ids; }
}