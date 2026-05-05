package com.masroofy.domain;

public class Category {
    private int categoryId;
    private String name;
    private String iconPath;

    public Category(int categoryId, String name, String iconPath) {
        this.categoryId = categoryId;
        this.name = name;
        this.iconPath = iconPath;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getCategoryInfo() {
        return "ID: " + categoryId + " | Name: " + name + " | Icon: " + iconPath;
    }
}
