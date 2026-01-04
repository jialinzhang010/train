package com.jialin.train.business.enums;

public enum RedisKeyPreEnum {

    CONFIRM_ORDER("LOCK_CONFIRM_ORDER", "First class"),
    SK_TOKEN("LOCK_SK_TOKEN", "Second class"),
    SK_TOKEN_COUNT("SK_TOKEN_COUNT", "Token balance");

    private final String code;
    private final String desc;

    RedisKeyPreEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
