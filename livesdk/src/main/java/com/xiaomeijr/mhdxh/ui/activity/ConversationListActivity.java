package com.xiaomeijr.mhdxh.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;
import com.xiaomeijr.mhdxh.ui.fragment.AllroomFragment;
import com.xiaomeijr.mhdxh.ui.fragment.MyroomFragment;

import io.rong.imkit.fragment.ConversationListFragment;

public class ConversationListActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout mContent;
    private ConversationListFragment conversationListFragment;
    private MyroomFragment myroomFragment;
    private AllroomFragment allroomFragment;
    private TextView tab1;
    private TextView tab2;
    private TextView tab3;
    private TextView title;
    private LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_conversation_list;
    }

    @Override
    protected void initUI() {
        mContent = (FrameLayout) findViewById(R.id.content);
        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        tab3 = (TextView) findViewById(R.id.tab3);
        title = (TextView) findViewById(R.id.title);
        back = (LinearLayout) findViewById(R.id.back);
        title.setText("聊天室");

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        conversationListFragment = new ConversationListFragment();
        myroomFragment = MyroomFragment.newInstance();
        allroomFragment = AllroomFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content, conversationListFragment)
                .add(R.id.content, myroomFragment)
                .add(R.id.content, allroomFragment)
                .hide(myroomFragment)
                .hide(allroomFragment)
                .show(conversationListFragment)
                .commit();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back) {
            finish();

        } else if (i == R.id.tab1) {
            getSupportFragmentManager().beginTransaction()
                    .hide(myroomFragment)
                    .hide(allroomFragment)
                    .show(conversationListFragment)
                    .commit();

        } else if (i == R.id.tab2) {
            getSupportFragmentManager().beginTransaction()
                    .hide(conversationListFragment)
                    .hide(allroomFragment)
                    .show(myroomFragment)
                    .commit();

        } else if (i == R.id.tab3) {
            getSupportFragmentManager().beginTransaction()
                    .hide(myroomFragment)
                    .hide(conversationListFragment)
                    .show(allroomFragment)
                    .commit();

        } else {
        }
    }
}
