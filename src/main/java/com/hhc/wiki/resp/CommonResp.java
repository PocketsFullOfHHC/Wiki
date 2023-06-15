package com.hhc.wiki.resp;

// 定义服务器通用的返回类，共有三个值：返回信息是否成功，提示信息(成功时为null)，返回的响应内容(失败时为null)
// 也可以加上一些通用的属性，比如接口、版本号和返回码等
public class CommonResp<T> {

    /**
     * 业务上的成功或失败
     */
    private boolean success = true;

    /**
     * 返回提示信息
     */
    private String message;

    /**
     * 返回泛型(是一种把明确类型的工作推迟到创建对象或者调用方法的时候才去明确的特殊的类型)数据，自定义类型，返回什么都可以
     */
    private T content;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResponseDto{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", content=").append(content);
        sb.append('}');
        return sb.toString();
    }
}