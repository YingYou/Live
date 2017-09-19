package com.xiaomeijr.mhdxh.model;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.apkfuns.logutils.LogUtils;
import com.xiaomeijr.mhdxh.data.BaseBean;
import com.xiaomeijr.mhdxh.data.BaseBean2;

import java.util.Iterator;
import java.util.Map;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpCycleContext;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;

/**
 * Created by Administrator on 2016/12/21.
 */
public class NetWorkRequest {
    private Context context;
    private UIDataListener uiDataListener;
    private String currentUrl = "";

    public NetWorkRequest(Context context, UIDataListener uiDataListener) {
        this.context = context;
        this.uiDataListener = uiDataListener;
    }

    /**
     * Get请求
     *
     * @param map
     */
    public void doGetRequest(final int flag, final boolean isShowDialog, String url, Map map) {
        RequestParams params = new RequestParams((HttpCycleContext) context);//请求参数
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key;
            String value;
            key = it.next().toString();
            value = (String) map.get(key);
            params.addFormDataPart(key, value);
        }
        currentUrl = url;
        LogUtils.d(currentUrl + params);
        HttpRequest.get(url, params, new BaseHttpRequestCallback<String>() {

            //请求网络前
            @Override
            public void onStart() {
                if (isShowDialog)
                    uiDataListener.showDialog();
            }

            @Override
            protected void onSuccess(String response) {
                if (TextUtils.isEmpty(response)) {
                    return;
                } else {
                    LogUtils.d(response);
                    BaseBean result = JSON.parseObject(response, new TypeReference<BaseBean>() {
                    });

                    if (result != null && result.getCode().equals("fw.success")) {
                        uiDataListener.loadDataFinish(flag, result.getRLive());
                    } else if (result != null) {
                        uiDataListener.onError(result.getCode() + "", result.getMsg());
                    }
                }
            }

            //请求失败（服务返回非法JSON、服务器异常、网络异常）
            @Override
            public void onFailure(int errorCode, String msg) {
                LogUtils.d("onFailure" + msg);
                uiDataListener.onError(errorCode + "", msg);
            }

            //请求网络结束
            @Override
            public void onFinish() {
                uiDataListener.dismissDialog();
            }
        });
    }

    /**
     * Post请求
     *
     * @param map
     */
    public void doPostRequest(final int flag, final boolean isShowDialog, String url, Map map) {
        RequestParams params = new RequestParams((HttpCycleContext) context);//请求参数
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key;
            String value;
            key = it.next().toString();
            value = (String) map.get(key);
            params.addFormDataPart(key, value);
        }
        currentUrl = url;
        LogUtils.d(currentUrl + params);
        HttpRequest.post(url, params, new BaseHttpRequestCallback<String>() {

            //请求网络前
            @Override
            public void onStart() {
                if (isShowDialog)
                    uiDataListener.showDialog();
            }

            @Override
            protected void onSuccess(String response) {
                if (TextUtils.isEmpty(response)) {
                    return;
                } else {
                    LogUtils.d(flag+"/"+response);
                    BaseBean result = JSON.parseObject(response, new TypeReference<BaseBean>() {
                    });

                    if (result != null && result.getCode().equals("fw.success")) {
                        uiDataListener.loadDataFinish(flag, result.getRLive());
                    } else if (result != null) {
                        uiDataListener.onError(result.getCode() + "", result.getMsg());
                    }
                }
            }

            //请求失败（服务返回非法JSON、服务器异常、网络异常）
            @Override
            public void onFailure(int errorCode, String msg) {
                LogUtils.d("onFailure" + msg);
                uiDataListener.onError(errorCode + "", msg);
            }

            //请求网络结束
            @Override
            public void onFinish() {
                uiDataListener.dismissDialog();
            }
        });
    }
    /**
     * Post请求
     *
     * @param map
     */
    public void doPostRequest2(final int flag, final boolean isShowDialog, String url, Map map) {
        RequestParams params = new RequestParams((HttpCycleContext) context);//请求参数
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key;
            String value;
            key = it.next().toString();
            value = (String) map.get(key);
            params.addFormDataPart(key, value);
        }
        currentUrl = url;
        LogUtils.d(currentUrl + params);
        HttpRequest.post(url, params, new BaseHttpRequestCallback<String>() {

            //请求网络前
            @Override
            public void onStart() {
                if (isShowDialog)
                    uiDataListener.showDialog();
            }

            @Override
            protected void onSuccess(String response) {
                if (TextUtils.isEmpty(response)) {
                    return;
                } else {
                    LogUtils.d(response);
                    BaseBean2 result = JSON.parseObject(response, new TypeReference<BaseBean2>() {
                    });

                    if (result != null && result.getCode().equals("fw.success")) {
                        LogUtils.d( "111"+result.getRRongYun());
                        uiDataListener.loadDataFinish(flag, result.getRRongYun());
                    } else if (result != null) {
                        uiDataListener.onError(result.getCode() + "", result.getMsg());
                    }
                }
            }

            //请求失败（服务返回非法JSON、服务器异常、网络异常）
            @Override
            public void onFailure(int errorCode, String msg) {
                LogUtils.d("onFailure" + msg);
                uiDataListener.onError(errorCode + "", msg);
            }

            //请求网络结束
            @Override
            public void onFinish() {
                uiDataListener.dismissDialog();
            }
        });
    }
    /**
     * Post请求
     *
     * @param map
     */
    public void doPostRequest3(final int flag, final boolean isShowDialog, String url, Map map) {
        RequestParams params = new RequestParams((HttpCycleContext) context);//请求参数
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key;
            String value;
            key = it.next().toString();
            value = (String) map.get(key);
            params.addFormDataPart(key, value);
        }
        currentUrl = url;
        LogUtils.d(currentUrl + params);
        HttpRequest.post(url, params, new BaseHttpRequestCallback<String>() {

            //请求网络前
            @Override
            public void onStart() {
                if (isShowDialog)
                    uiDataListener.showDialog();
            }

            @Override
            protected void onSuccess(String response) {
                if (TextUtils.isEmpty(response)) {
                    return;
                } else {
                    LogUtils.d(response);
                    uiDataListener.loadDataFinish(flag, response);
                }
            }

            //请求失败（服务返回非法JSON、服务器异常、网络异常）
            @Override
            public void onFailure(int errorCode, String msg) {
                LogUtils.d("onFailure" + msg);
                uiDataListener.onError(errorCode + "", msg);
            }

            //请求网络结束
            @Override
            public void onFinish() {
                uiDataListener.dismissDialog();
            }
        });
    }

    /**
     * 取消请求
     */
    public void CancelPost() {
        HttpRequest.cancel(currentUrl);
    }
}
