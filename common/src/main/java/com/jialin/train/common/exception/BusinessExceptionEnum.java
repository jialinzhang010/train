package com.jialin.train.common.exception;

public enum BusinessExceptionEnum {

    MEMBER_MOBILE_EXIST("Phone number is exist"),
    MEMBER_MOBILE_NOT_EXIST("Please first get the verification code"),
    MEMBER_MOBILE_CODE_ERROR("Wrong verification code");

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
