package com.example.english_learning_app;

public class AchievementModel {
    private String id;
    private String Name;
    private String description;

    // 1. Constructor mặc định (Bắt buộc phải có để Firebase hoạt động)
    public AchievementModel() {
    }

    // 2. Constructor có 3 tham số (Đây là thứ bạn đang thiếu khiến app báo lỗi)
    public AchievementModel(String id, String name, String description) {
        this.id = id;
        this.Name = name;
        this.description = description;
    }

    // 3. Các hàm Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}