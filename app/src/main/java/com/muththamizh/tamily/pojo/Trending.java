package com.muththamizh.tamily.pojo;

public class Trending {

    private String categoryDocumentId;
    private String categoryId;
    private String categoryName;


    public Trending(String categoryDocumentId, String categoryId, String categoryName) {
        this.categoryDocumentId = categoryDocumentId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryDocumentId() {
        return categoryDocumentId;
    }

    public void setCategoryDocumentId(String categoryDocumentId) {
        this.categoryDocumentId = categoryDocumentId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
