package com.xiaomeijr.mhdxh.utils;

/**
 * Created by wuwei on 2017/7/26.
 */

public class Constant {

    /**
     *测试
     */
    public final static String  BaseUrl= "http://59.173.86.226:8282/XmApp/front";
    /**
     *正式
     */
//    public final static String  BaseUrl= "http://xmapp.mfc.com.cn:8088/XmApp/front";
    /**
     *获取⽤户详细信息
     */
    public final static String  GetUserInfo= BaseUrl+"?_A=PLive_UI&_mt=json";
    /**
     *获取融云Token
     */
    public final static String  GetRongYunToken= BaseUrl+"?_A=RYong_T&_mt=json";
    /**
     *获取操作权限
     */
    public final static String  GetQuanxian= BaseUrl+"?_A=PLive_UO&_mt=json";
    /**
     *获取直播推流地址
     */
    public final static String  GetShowUrl= BaseUrl+"?_A=PLive_LA&_mt=json";
    /**
     *获取在线人数  X
     */
    public final static String  GetfansNum= BaseUrl+"?_A=PLive_UN&_mt=json";
    /**
     *通过登陆账号获取⽤户详细信息
     */
    public final static String  GetShowInfos= BaseUrl+"?_A=PLive_UL&_mt=json";
    /**
     *获取关注主播状态
     */
    public final static String  GetFocusState= BaseUrl+"?_A=PLive_UF&_mt=json";
    /**
     *关注主播或取消关注
     */
    public final static String  GetFocus= BaseUrl+"?_A=PLive_LF&_mt=json";
    /**
     *关注主播或取消关注
     */
    public final static String  sendGifts= BaseUrl+"?_A=PLive_LG&_mt=json";
    /**
     *禁言服务
     */
    public final static String  NotTalk= "http://api.cn.ronghub.com/chatroom/user/gag/add.json";
    /**
     *封禁服务
     */
    public final static String  Remove= "http://api.cn.ronghub.com/chatroom/user/block/add.json";
    /**
     *查询是否被封禁
     */
    public final static String  CheckRemove= "http://api.cn.ronghub.com/chatroom/user/block/list.json";
    /**
     *查询是否被禁言
     */
    public final static String  CheckNottalk= "http://api.cn.ronghub.com/chatroom/user/gag/list.json";
    /**
     *封禁时长
     */
    public final static String  RemoveTime= "5";
    /**
     *禁言时长
     */
    public final static String  NotTalkTime= "5";

}
