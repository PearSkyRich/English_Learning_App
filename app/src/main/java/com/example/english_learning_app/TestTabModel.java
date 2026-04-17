package com.example.english_learning_app;

public class TestTabModel {
    private String name;
    private boolean isSelected;

    public TestTabModel(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}