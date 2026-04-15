package com.example.english_learning_app;

public class LevelModel {
    private String id;
    private String unit_id;
    private String name;
    private String title;
    private int order;
    private String image_url;

    public LevelModel() {}

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
}