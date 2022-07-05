package com.hbwl.model;

public class Type {
    private String code;
    private String view;

    public Type(String code, String view) {
        this.code = code;
        this.view = view;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return view;
    }
}
