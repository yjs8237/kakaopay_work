package com.greatyun.kakakopay.enumuration;

public enum EnumFinishYn implements EnumModel {

    Y("완료") , N("미완료");

    private String description;
    private EnumFinishYn (String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return description;
    }
}
