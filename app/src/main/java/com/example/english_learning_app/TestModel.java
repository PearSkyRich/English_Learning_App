package com.example.english_learning_app;
import java.util.List;

public class TestModel {
    private String id, test_id, test_name, description, certificate_id, kind, image_url;
    private List<String> quizzes_ids;

    public TestModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTest_id() { return test_id; }
    public String getTest_name() { return test_name; }
    public String getDescription() { return description; }
    public String getCertificate_id() { return certificate_id; }
    public String getKind() { return kind; }
    public String getImage_url() { return image_url; }
    public List<String> getQuizzes_ids() { return quizzes_ids; }
}