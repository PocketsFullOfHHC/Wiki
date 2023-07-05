package com.hhc.wiki.req;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserLoginReq {

    // NotEmpty不光检验null，还检验空字符串(场景：在input框中输入一些字符串后再清空，此时不是null，而是空字符串)
    @NotEmpty(message = "【用户名】不能为空")
    private String loginName;

    @NotEmpty(message = "【密码】不能为空")
    // 长度校验
    // @Length(min = 6, max = 20, message = "【密码】6~20位")
    // 正则表达式校验
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "【密码】规则不正确")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", loginName=").append(loginName);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}