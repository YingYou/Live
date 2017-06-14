package com.mer.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mer.live.R;
import com.mer.live.base.LiveKit;
import com.mer.live.controller.RcLog;
import com.mer.live.fakeserver.FakeServer;
import com.mer.live.fakeserver.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LiveshowPreActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mBack;
    private TextView shexiangtou_tv;
    private TextView meiyan_tv;

    private int meiyan = 0;
    private int shexiangtou = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveshow_pre);

        initUI();
    }

    private void initUI() {

        mBack = (ImageView) findViewById(R.id.showpre_back);
        meiyan_tv = (TextView) findViewById(R.id.showpre_meiyan_tv);
        shexiangtou_tv = (TextView) findViewById(R.id.showpre_shexiangtou_tv);

        findViewById(R.id.showpre_meiyan_ll).setOnClickListener(this);
        findViewById(R.id.showpre_shexiangtou_ll).setOnClickListener(this);
        findViewById(R.id.showpre_save).setOnClickListener(this);
        findViewById(R.id.showpre_confirm).setOnClickListener(this);
        mBack.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        meiyan = sharedPreferences.getInt("meiyan",0);
        shexiangtou = sharedPreferences.getInt("shexiangtou",0);

        if (meiyan==0){
            meiyan_tv.setText("开启美颜             ");
        }else if (meiyan==1){
            meiyan_tv.setText("关闭美颜             ");
        }
        if (shexiangtou==1){
            shexiangtou_tv.setText("开启后置摄像头");
        }else if (shexiangtou==0){
            shexiangtou_tv.setText("开启前置摄像头");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.showpre_back:
                finish();
                break;
            case R.id.showpre_meiyan_ll:
                if (meiyan==0){
                    meiyan=1;
                    meiyan_tv.setText("关闭美颜             ");
                }else if (meiyan==1){
                    meiyan=0;
                    meiyan_tv.setText("开启美颜             ");
                }
                break;
            case R.id.showpre_shexiangtou_ll:
                if (shexiangtou==0){
                    shexiangtou=1;
                    shexiangtou_tv.setText("开启前置摄像头");
                }else if (shexiangtou==1){
                    shexiangtou=0;
                    shexiangtou_tv.setText("开启后置摄像头");
                }
                break;
            case R.id.showpre_save:
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putInt("meiyan", meiyan);
                editor.putInt("shexiangtou", shexiangtou);
                editor.commit();//提交修改
                Toast.makeText(LiveshowPreActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                Log.e("sssssssssssssss",meiyan+"//"+shexiangtou);
                break;
            case R.id.showpre_confirm:
                fakeLogin("张三", "123456");
                break;
            default:
                break;
        }
    }

    private void fakeLogin(String id, String password) {
        final UserInfo user = FakeServer.getLoginUser(id, password);
        FakeServer.getToken(user, new HttpUtil.OnResponse() {
            @Override
            public void onResponse(int code, String body) {
                if (code != 200) {
                    Toast.makeText(LiveshowPreActivity.this, body, Toast.LENGTH_SHORT).show();
                    return;
                }

                String token;
                try {
                    JSONObject jsonObj = new JSONObject(body);
                    token = jsonObj.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LiveshowPreActivity.this, "Token 解析失败!", Toast.LENGTH_SHORT).show();
                    return;
                }

                LiveKit.connect(token, new RongIMClient.ConnectCallback() {

                    @Override
                    public void onTokenIncorrect() {
                        RcLog.d("", "connect onTokenIncorrect");
                        // 检查appKey 与token是否匹配.
                    }

                    @Override
                    public void onSuccess(String userId) {
                        RcLog.d("", "connect onSuccess");
                        LiveKit.setCurrentUser(user);
                        Intent intent = new Intent(LiveshowPreActivity.this, LiveShowActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        RcLog.d("", "connect onError = " + errorCode);
                        // 根据errorCode 检查原因.
                    }
                });
            }
        });
    }
}
