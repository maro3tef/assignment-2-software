package com.masroofy.domain;

/**
 * The type Category.
 */
public class Category {
    private int categoryId;
    private String name;
    private String iconPath;

    /**
     * Instantiates a new Category.
     *
     * @param categoryId the category id
     * @param name       the name
     * @param iconPath   the icon path
     */
    public Category(int categoryId, String name, String iconPath) {
        this.categoryId = categoryId;
        this.name = name;
        this.iconPath = iconPath;
    }

    /**
     * Gets category id.
     *
     * @return the category id
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets icon path.
     *
     * @return the icon path
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Gets category info.
     *
     * @return the category info
     */
    public String getCategoryInfo() {
        return "ID: " + categoryId + " | Name: " + name + " | Icon: " + iconPath;
    }
}
