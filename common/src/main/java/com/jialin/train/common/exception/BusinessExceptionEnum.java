package com.jialin.train.common.exception;

public enum BusinessExceptionEnum {

    MEMBER_MOBILE_EXIST("Phone number already exists"),
    MEMBER_MOBILE_NOT_EXIST("Please first get the verification code"),
    MEMBER_MOBILE_CODE_ERROR("Wrong verification code"),

    BUSINESS_STATION_NAME_UNIQUE_ERROR("Station already exists"),
    BUSINESS_TRAIN_CODE_UNIQUE_ERROR("Train already exists"),
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR("Station index already exists"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR("Station name already exists"),
    BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR("Carriage number already exists"),

    CONFIRM_ORDER_TICKET_COUNT_ERROR("Ticket is out of stock"),
    CONFIRM_ORDER_EXCEPTION("Server is busy. Please try again later."),
    CONFIRM_ORDER_LOCK_FAIL("Server is busy. Please try again later."),
    CONFIRM_ORDER_FLOW_EXCEPTION("Too many people are buying tickets. Please try again later."),
    CONFIRM_ORDER_SK_TOKEN_FAIL("Too many people are buying tickets. Please try again later.");

    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BusinessExceptionEnum{");
        sb.append("desc='").append(desc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
