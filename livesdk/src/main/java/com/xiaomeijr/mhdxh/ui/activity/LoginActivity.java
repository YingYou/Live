package com.xiaomeijr.mhdxh.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;
import com.xiaomeijr.mhdxh.base.LiveKit;
import com.xiaomeijr.mhdxh.controller.RcLog;
import com.xiaomeijr.mhdxh.fakeserver.FakeServer;
import com.xiaomeijr.mhdxh.fakeserver.HttpUtil;
import com.xiaomeijr.mhdxh.utils.ACache;
import com.xiaomeijr.mhdxh.utils.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private int room = 0;
    private UserInfo user;
    private Button chat1;
    private Button chat2;
    private TextView btnH5;
    private ACache mAcache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        mAcache = ACache.get(LoginActivity.this);
    }

    @Override
    protected void initUI() {
        btnH5 = (Button) findViewById(R.id.btn_tv);
        btnH5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, H5Activity.class));
            }
        });
        chat1 = (Button) findViewById(R.id.btn_chat1);
        chat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeLogin("张三", "123456");
                room = 3;
            }
        });
        chat2 = (Button) findViewById(R.id.btn_chat2);
        chat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeLogin("张三", "123456");
                room = 4;
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

    private void fakeLogin(String id, String password) {
        user = FakeServer.getLoginUser(id, password);
        String tokenTemp = mAcache.getAsString("Token");
        if (TextUtils.isEmpty(tokenTemp)) {
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
                        login(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Token 解析失败!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        } else {
            login(tokenTemp);
        }

    }

    private void login(final String token) {
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

                LogUtils.d(userId);
                Intent intent = null;
                if (room == 0) {
                    intent = new Intent(LoginActivity.this, LivePlayActivity.class);
                    startActivity(intent);
                } else if (room == 1) {
                    intent = new Intent(LoginActivity.this, LiveShowActivity.class);
//                    intent.putExtra("token",token);
                    startActivity(intent);
                } else if (room == 2) {
                    intent = new Intent(LoginActivity.this, PcVideoPlayActivity.class);
                    startActivity(intent);
                } else if (room == 3) {
//                    RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
//                    RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
//                    RongIM.getInstance().startGroupChat(LoginActivity.this, "Q5gW7ebfG", "美尔雅");
                } else if (room == 4) {
//                    Map<String, Boolean> supportedConversation = new HashMap<String, Boolean>();
//                    supportedConversation.put(Conversation.ConversationType.PRIVATE.getName(), false);
//                    RongIM.getInstance().startConversationList(LoginActivity.this, supportedConversation);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                RcLog.d(TAG, "connect onError = " + errorCode);
                // 根据errorCode 检查原因.
            }
        });
    }
}
