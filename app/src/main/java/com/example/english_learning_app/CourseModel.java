package com.example.english_learning_app;

import java.util.ArrayList;
import java.util.List;

public class CourseModel {
    private String id;
    private String name;
    private String tag;

    private String short_description;
    private int total_units;

    private String image_url;
    private boolean isExpanded = false;
    private List<UnitModel> units = new ArrayList<>();

    public CourseModel() {} // Bắt buộc cho Firebase

    // --- GETTER & SETTER ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getShort_description() { return short_description; }
    public void setShort_description(String short_description) { this.short_description = short_description; }

    public int getTotal_units() { return total_units; }
    public void setTotal_units(int total_units) { this.total_units = total_units; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    public List<UnitModel> getUnits() { return units; }
    public void setUnits(List<UnitModel> units) { this.units = units; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
}