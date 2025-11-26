package com.example.berenang10;

public class MostSearchedItem {
    private String name;
    private String searchCount;
    private int imageResId;

    public MostSearchedItem(String name, String searchCount, int imageResId) {
        this.name = name;
        this.searchCount = searchCount;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(String searchCount) {
        this.searchCount = searchCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}