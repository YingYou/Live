package com.xiaomeijr.mhdxh.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;

public class ConversationActivity extends BaseActivity {

    TextView mTitle;
    private String targetId;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void initUI() {
        mTitle = (TextView) findViewById(R.id.title);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        targetId = getIntent().getData().getQueryParameter("targetId");//群聊ID
        title = getIntent().getData().getQueryParameter("title");//昵称（加了用户提供者）

        mTitle.setText(title);
    }
}
