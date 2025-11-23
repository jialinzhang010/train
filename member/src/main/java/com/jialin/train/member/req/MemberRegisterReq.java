package com.jialin.train.member.req;

import jakarta.validation.constraints.NotBlank;

public class MemberRegisterReq {
    @NotBlank(message = "phone number must not be blank")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberRegisterReq{");
        sb.append("mobile='").append(mobile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
