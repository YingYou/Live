package com.xiaomeijr.mhdxh.data;

import java.io.Serializable;

/**
 * Created by wuwei on 2017/7/26.
 */

public class RongyunToken implements Serializable{

    /**
     * code : 200
     * errorMessage :
     * token : 8Xu==
     */

    private int code;
    private String errorMessage;
    private String token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
