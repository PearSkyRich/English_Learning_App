package com.example.english_learning_app;

public class UnitModel {
    private String id;
    private String course_id;
    private String name;
    private int order;
    private String short_description;
    private String image_url;

    public UnitModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourse_id() { return course_id; }
    public void setCourse_id(String course_id) { this.course_id = course_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public String getShort_description() { return short_description; }
    public void setShort_description(String short_description) { this.short_description = short_description; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
}