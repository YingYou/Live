package com.xiaomeijr.mhdxh.data;

import java.io.Serializable;

/**
 * Created by wuwei on 2017/7/26.
 */

public class RUserInfo implements Serializable {

    /**
     * userId : 873
     * loginNo : 18186611360
     * nickName : 李四
     * userImage : http://59.173.86.226:8282/XmImg/root/file/xmpp/1/20170223/8819f3a75ea84fd893823d2b29e3b313.jpg
     * liveAddress :
     * status :
     * focus :
     */

    private String userId;
    private String loginNo;
    private String nickName;
    private String userImage;
    private String liveAddress;
    private String liveIntroduce;
    private String status;
    private String focus;
    private String token;//自定义数据

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginNo() {
        return loginNo;
    }

    public void setLoginNo(String loginNo) {
        this.loginNo = loginNo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getLiveAddress() {
        return liveAddress;
    }

    public void setLiveAddress(String liveAddress) {
        this.liveAddress = liveAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLiveIntroduce() {
        return liveIntroduce;
    }

    public void setLiveIntroduce(String liveIntroduce) {
        this.liveIntroduce = liveIntroduce;
    }
}
