package com.sqs.resourceshare_android.entity;

import java.util.Collections;

public class ResponseDto<T> {
    private String msg;
    private Object data;
    private Object extra;

    public ResponseDto(String msg, Object data, Object extra) {
        this.msg = msg;
        this.data = data;
        this.extra = extra;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public static ResponseDto success() {
        return success(Collections.EMPTY_LIST);
    }

    public static ResponseDto success(Object data) {
        return success(data, Collections.EMPTY_LIST);
    }

    public static ResponseDto success(Object data, Object extra) {
        return new ResponseDto("success", data, extra);
    }

    public static ResponseDto fail(String msg) {
        return fail(msg, Collections.EMPTY_LIST);
    }

    public static ResponseDto fail(Object data) {
        return fail("fail", data);
    }

    public static ResponseDto fail(String msg, Object data) {
        return fail(msg, data, Collections.EMPTY_LIST);
    }

    public static ResponseDto fail(String msg, Object data, Object extra) {
        return new ResponseDto(msg, data, extra);
    }
}
