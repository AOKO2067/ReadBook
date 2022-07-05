package com.hbwl.model;

public class ListViewModel {
    private String code;
    private String title;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public ListViewModel(String code, String title) {
        this.code = code;
        this.title = title;
    }
}
