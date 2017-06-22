package com.mer.live.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mer.live.R;
import com.mer.live.base.LiveKit;
import com.mer.live.controller.RcLog;
import com.mer.live.fakeserver.FakeServer;
import com.mer.live.fakeserver.HttpUtil;
import com.mer.live.utils.PermissionUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button btnTV;
    private Button btnLive;
    private int room = 0;
    private Button btnVedio;
    private SharedPreferences share;
    private UserInfo user;
    private Button btnpcVedio;
    private Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
         share = getSharedPreferences("live", MODE_PRIVATE);
    }

    private void initView() {
        btnTV = (Button) findViewById(R.id.btn_tv);
        btnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeLogin("张三", "123456");
                room = 0;
            }
        });

        btnLive = (Button) findViewById(R.id.btn_live);
        btnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeLogin("张三", "123456");
                room = 1;
            }
        });

        btnVedio = (Button) findViewById(R.id.btn_vedio);
        btnVedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,VideoPlayActivity.class));
            }
        });
        btnpcVedio = (Button) findViewById(R.id.btn_pcvedio);
        btnpcVedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeLogin("张三", "123456");
                room = 2;
            }
        });
        btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(LoginActivity.this).withText("hello")
                        .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA)
                        .setCallback(umShareListener).open();
            }
        });
        if (PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[0]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[1]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[2]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[3]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[4]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[5]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[6]) ||
                PermissionUtil.isLacksOfPermission(PermissionUtil.PERMISSION[7])
                ) {
            ActivityCompat.requestPermissions(LoginActivity.this, PermissionUtil.PERMISSION, 0x12);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);

            Toast.makeText(LoginActivity.this, platform + " 分享成功!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(LoginActivity.this,platform + " 分享失败!", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(LoginActivity.this,platform + " 分享取消!", Toast.LENGTH_SHORT).show();
        }
    };
    private void fakeLogin(String id, String password) {
        user = FakeServer.getLoginUser(id, password);
        String tokenTemp = share.getString("token", "");
        if (TextUtils.isEmpty(tokenTemp)){
            FakeServer.getToken(user, new HttpUtil.OnResponse() {
                @Override
                public void onResponse(int code, String body) {
                    if (code != 200) {
                        Toast.makeText(LoginActivity.this, body, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String token;
                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        token = jsonObj.getString("token");
                        SharedPreferences.Editor edit = share.edit(); //编辑文件
                        edit.putString("token", token);
                        edit.commit();  //保存数据信息
                        login(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Token 解析失败!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }else {
            login(tokenTemp);
        }

    }

    private void login(final String token){
        LiveKit.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                RcLog.d(TAG, "connect onTokenIncorrect");
                // 检查appKey 与token是否匹配.
            }

            @Override
            public void onSuccess(String userId) {
                RcLog.d(TAG, "connect onSuccess");
                LiveKit.setCurrentUser(user);

//                            // Get the stream json from http
//                            String streamJson = requestStreamJson();
//                            Log.d("stream_json_str",streamJson);
                Intent intent = null;
                if (room == 0){
                    intent = new Intent(LoginActivity.this, LivePlayActivity.class);
                }else if (room==1){
                    intent = new Intent(LoginActivity.this, LiveshowPreActivity.class);
                    intent.putExtra("token",token);
                }else if (room==2){
                    intent = new Intent(LoginActivity.this, PcVideoPlayActivity.class);
                }
                startActivity(intent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                RcLog.d(TAG, "connect onError = " + errorCode);
                // 根据errorCode 检查原因.
            }
        });
    }
}
