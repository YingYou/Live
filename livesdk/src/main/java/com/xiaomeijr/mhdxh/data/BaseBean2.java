package com.xiaomeijr.mhdxh.data;

import java.io.Serializable;

/**
 * Created by wuwei on 2017/6/27.
 */

public class BaseBean2<T> implements Serializable {
    private T RRongYun;
    private String msg;
    private String code;

    public T getRRongYun() {
        return RRongYun;
    }

    public void setRRongYun(T RRongYun) {
        this.RRongYun = RRongYun;
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
