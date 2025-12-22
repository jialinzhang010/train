package com.jialin.train.business.enums;

public enum ConfirmOrderStatusEnum {

    INIT("I", "Initiating"),
    PENDING("P", "Pending"),
    SUCCESS("S", "Succeeded"),
    FAILURE("F", "Failed"),
    EMPTY("E", "Out of stock"),
    CANCEL("C", "Cancel"),;

    private String code;

    private String desc;

    ConfirmOrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override    public String toString() {
        return "ConfirmOrderStatusEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                "} " + super.toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
