package com.xiaomeijr.mhdxh.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.apkfuns.logutils.LogUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;
import com.xiaomeijr.mhdxh.base.LiveKit;
import com.xiaomeijr.mhdxh.controller.RcLog;
import com.xiaomeijr.mhdxh.data.RUserInfo;
import com.xiaomeijr.mhdxh.data.RongyunToken;
import com.xiaomeijr.mhdxh.fakeserver.FakeServer;
import com.xiaomeijr.mhdxh.fakeserver.HttpUtil;
import com.xiaomeijr.mhdxh.model.NetWorkRequest;
import com.xiaomeijr.mhdxh.model.UIDataListener;
import com.xiaomeijr.mhdxh.ui.widget.DialogProgress;
import com.xiaomeijr.mhdxh.utils.ACache;
import com.xiaomeijr.mhdxh.utils.Constant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class H5Activity extends BaseActivity implements UIDataListener {

    private WebView mWebview;
    private String url_web = "http://59.173.86.226:8282/XmApp/live_test/live_login.html";
    private int room = 0;
    private Dialog dialog;
    private NetWorkRequest request;
    private RUserInfo userInfo;
    private ACache mAcache;
    private String mJson;
    private String userToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_h5;
    }

    @Override
    protected void initUI() {

        mWebview = (WebView) findViewById(R.id.h5_web);
        dialog = DialogProgress.createLoadingDialog(H5Activity.this, "", this);
        request = new NetWorkRequest(H5Activity.this, this);
        mAcache = ACache.get(H5Activity.this);

        Intent intent = getIntent();
        if (intent.hasExtra("url")) {
            url_web = intent.getStringExtra("url");
        }
    }

    @Override
    protected void initData() {

        setWebView();
    }

    /**
     * 设置web参数
     */
    private void setWebView() {
        WebSettings localWebSettings = mWebview.getSettings();
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setDefaultTextEncodingName("UTF-8");
        localWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        localWebSettings.setUseWideViewPort(true);
        localWebSettings.setLoadWithOverviewMode(true);
        localWebSettings.setNeedInitialFocus(true);
        localWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        localWebSettings.setLoadsImagesAutomatically(true);
        localWebSettings.setTextZoom(100);
        localWebSettings.setDomStorageEnabled(true);
        localWebSettings.setAppCacheMaxSize(8388608L);
        localWebSettings.setAppCachePath(this.getApplicationContext().getCacheDir().getAbsolutePath());
        localWebSettings.setAllowFileAccess(true);
        localWebSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 16) {
            localWebSettings.setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            localWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
           //设置响应js 的Alert()函数
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                return super.onJsAlert(view,url,message,result);
            }
            //设置响应js 的Confirm()函数
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

                return super.onJsConfirm(view, url, message, result);
            }
            //设置响应js 的Prompt()函数
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

                return super.onJsPrompt(view,url,message,defaultValue,result);
            }
        });
        mWebview.addJavascriptInterface(new JSHook(), "android");
        mWebview.loadUrl(url_web);

    }

    public class JSHook {
        /**
         * 视频点播
         * 播放手机录制视频，支持多种格式
         *
         * @param json
         */
        @JavascriptInterface
        public void playVideo(String json) {
            LogUtils.d(json);
            JSONObject object = JSONObject.parseObject(json);
            final String url = object.getString("url");
            final String name = object.getString("name");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(H5Activity.this, VideoPlayActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            });

        }

        /**
         * type=1 直播播放端界面
         * 否则   电脑直播播放端界面
         *
         * @param json
         */
        @JavascriptInterface
        public void userLive(final String json) {
            LogUtils.d(json);
            try {
                JSONObject object = JSONObject.parseObject(json);
                String token = object.getString("token");
                String imageUrl = object.getString("imageUrl");
                final int type = object.getIntValue("type");
                if (TextUtils.isEmpty(token)) {
                    userInfo = new RUserInfo();
                    userInfo.setUserId(new Date().getTime() + "");
                    userInfo.setNickName("昵称");
                    userInfo.setUserImage(imageUrl);
                    mAcache.put("UserInfo", userInfo);
                    mJson = json;
                    if (type == 1) {
                        room = 0;
                    } else {
                        room = 2;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FakeServer.getToken(new UserInfo(userInfo.getUserId(), userInfo.getNickName(), Uri.parse(userInfo.getUserImage())), new HttpUtil.OnResponse() {
                                @Override
                                public void onResponse(int code, String body) {
                                    LogUtils.d(code + body);
                                    RongyunToken RYToken = JSON.parseObject(body, new TypeReference<RongyunToken>() {
                                    });
                                    if (RYToken.getCode() == 200) {
                                        fakeLogin(userInfo, RYToken.getToken(), mJson);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    userToken = token;
                    final String finalToken = token;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type == 1) {
                                room = 0;
                            } else {
                                room = 2;
                            }
                            mJson = json;
                            Map map = new HashMap();
                            map.put("token", finalToken);
                            request.doPostRequest(0, true, Constant.GetUserInfo, map);
                        }
                    });
                }
            } catch (Exception e) {
                LogUtils.d(e.getMessage());
            }
        }
        
        /**
         * 返回操作
         *
         * @param
         */
        @JavascriptInterface
        public void backToApp(){
        
            finish();
        } 
        


        /**
         * 主播直播录制页面
         *
         * @param json
         */
        @JavascriptInterface
        public void teacherLive(String json) {
            LogUtils.d(json);
            JSONObject object = JSONObject.parseObject(json);
            final String token = object.getString("token");
            userToken = token;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    room = 1;
                    Map map = new HashMap();
                    map.put("token", token);
                    request.doPostRequest(0, true, Constant.GetUserInfo, map);
                }
            });
        }

        /**
         * 分享页面
         *
         * @param json 参数说明：shareUrl，shareTitle，shareContent，imageUrl
         */
        @JavascriptInterface
        public void shareH5(String json) {

            LogUtils.d(json);
            JSONObject object = JSONObject.parseObject(json);
            final String shareUrl = object.getString("shareUrl");
            final String shareTitle = object.getString("shareTitle");
            final String shareContent = object.getString("shareContent");
//            final String imageUrl = object.getString("imageUrl");

            UMWeb web = new UMWeb(shareUrl);
            web.setTitle(shareTitle);//标题
//            UMImage image = new UMImage(H5Activity.this, imageUrl);//资源文件
//            web.setThumb(image);  //缩略图
            web.setDescription(shareContent);//描述
            new ShareAction(H5Activity.this)
                    .withMedia(web)
                    .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                    .setCallback(umShareListener).open();
        }
    }

    
    /**
     * 分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);

            Toast.makeText(H5Activity.this, platform + " 分享成功!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(H5Activity.this, platform + " 分享失败!", Toast.LENGTH_SHORT).show();
            if (t != null) {
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(H5Activity.this, platform + " 分享取消!", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 登录融云
     *
     * @param info
     * @param token
     * @param json
     */
    private void fakeLogin(RUserInfo info, String token, final String json) {

        final UserInfo userInfo = new UserInfo(info.getUserId(), info.getNickName(), Uri.parse(info.getUserImage()));
        if (!TextUtils.isEmpty(token)) {
            LiveKit.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    // 检查appKey 与token是否匹配.
                }

                @Override
                public void onSuccess(String userId) {
                    LiveKit.setCurrentUser(userInfo);
                    LogUtils.d(userId);
                    Intent intent = null;
                    if (room == 0) {
                        intent = new Intent(H5Activity.this, LivePlayActivity.class);
                        intent.putExtra("json", json);
                        startActivity(intent);
                    } else if (room == 1) {
                        intent = new Intent(H5Activity.this, LiveShowActivity.class);
                        startActivity(intent);
                    } else if (room == 2) {
                        intent = new Intent(H5Activity.this, PcVideoPlayActivity.class);
                        intent.putExtra("json", json);
                        startActivity(intent);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    // 根据errorCode 检查原因.
                    LogUtils.d("connect onError = " + errorCode);
                }
            });

        }

    }

    @Override
    public void loadDataFinish(int code, Object data) {
        if (code == 0) {
            userInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
//            LogUtils.d(userInfo);
            if (userInfo != null) {
                userInfo.setToken(userToken);
                mAcache.put("UserInfo", userInfo);
//                Map map = new HashMap();
//                map.put("userId", userInfo.getUserId() + "");
//                map.put("name", userInfo.getNickName());
//                request.doPostRequest2(1, true, Constant.GetRongYunToken, map);

                FakeServer.getToken(new UserInfo(userInfo.getUserId(), userInfo.getNickName(), Uri.parse(userInfo.getUserImage())), new HttpUtil.OnResponse() {
                    @Override
                    public void onResponse(int code, String body) {
                        LogUtils.d(code + body);
                        RongyunToken RYToken = JSON.parseObject(body, new TypeReference<RongyunToken>() {
                        });
                        if (RYToken.getCode() == 200) {
                            fakeLogin(userInfo, RYToken.getToken(), mJson);
                        }
                    }
                });
            }
        } else if (code == 1) {
            RongyunToken RYToken = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RongyunToken>() {
            });
            if (RYToken.getCode() == 200) {
                fakeLogin(userInfo, RYToken.getToken(), mJson);
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(H5Activity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog() {
        if (dialog != null && !isFinishing())
            dialog.show();
    }

    @Override
    public void dismissDialog() {
        if (dialog != null && !isFinishing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
                LogUtils.d(e.getMessage());
            }
    }

    @Override
    public void onError(String errorCode, String errorMessage) {
        showToast(errorMessage);
    }

    @Override
    public void cancelRequest() {
        request.CancelPost();
    }
}
