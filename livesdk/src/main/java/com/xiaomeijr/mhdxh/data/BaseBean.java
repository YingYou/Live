package com.xiaomeijr.mhdxh.data;

import java.io.Serializable;

/**
 * Created by wuwei on 2017/6/27.
 */

public class BaseBean<T> implements Serializable {
    private T RLive;
    private String msg;
    private String code;

    public T getRLive() {
        return RLive;
    }
    public void setRLive(T RLive) {
        this.RLive = RLive;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
