package com.it_tech613.zhe.instagramunfollow.utils;

public enum UnfollowStatus {
    success("success",0),
    limited("limited",1),
    failed("failed",2),
    limited_per_hour("limited_per_hour",3),
    limited_per_12hours("limited_per_12hours",4);
    private String stringValue;
    private int intValue;
    private UnfollowStatus(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int getIntValue(){
        return intValue;
    }
}
