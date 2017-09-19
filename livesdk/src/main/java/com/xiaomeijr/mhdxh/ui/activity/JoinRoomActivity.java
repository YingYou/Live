package com.xiaomeijr.mhdxh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;

public class JoinRoomActivity extends BaseActivity implements View.OnClickListener{

    TextView mTitle;
    private TextView mJoinroom;
    private LinearLayout mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_join_room;
    }

    @Override
    protected void initUI() {
        mTitle = (TextView) findViewById(R.id.title);
        mJoinroom = (TextView) findViewById(R.id.joinroom);
        mBack = (LinearLayout) findViewById(R.id.back);

        mJoinroom.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back) {
            finish();

        } else if (i == R.id.joinroom) {
            startActivity(new Intent(JoinRoomActivity.this, VerificationActivity.class));

        }
    }
}
