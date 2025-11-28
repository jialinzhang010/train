package com.jialin.train.member.enums;

public enum PassengerTypeEnum {
    ADULT("1", "Adult"),
    CHILD("2", "Child"),
    STUDENT("3", "Student");

    private String code;

    private String desc;

    PassengerTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
