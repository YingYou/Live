package com.mer.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mer.live.R;
import com.mer.live.base.LiveKit;
import com.mer.live.controller.RcLog;
import com.mer.live.fakeserver.FakeServer;
import com.mer.live.fakeserver.HttpUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
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
    }

    private void fakeLogin(String id, String password) {
        final UserInfo user = FakeServer.getLoginUser(id, password);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Token 解析失败!", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                        Intent intent;
                        if (room == 0){
                            intent = new Intent(LoginActivity.this, LivePlayActivity.class);
                        }else {
                            intent = new Intent(LoginActivity.this, LiveShowActivity.class);
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
        });
    }


    private String requestStreamJson() {
        try {
            // Replace "Your app server" by your app sever url which can get the StreamJson as the SDK's input.
            HttpURLConnection httpConn = (HttpURLConnection) new URL("rtmp://pili-publish.test.mfc.com.cn/cz-test/test001").openConnection();
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(10000);
            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int length = httpConn.getContentLength();
            if (length <= 0) {
                return null;
            }
            InputStream is = httpConn.getInputStream();
            byte[] data = new byte[length];
            int read = is.read(data);
            is.close();
            if (read <= 0) {
                return null;
            }
            return new String(data, 0, read);
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Network error!", Toast.LENGTH_SHORT);
        }
        return null;
    }
}
