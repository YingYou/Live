package com.mer.live.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mer.live.R;

public class LiveshowPreActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveshow_pre);

        initUI();
    }

    private void initUI() {

        mBack = (ImageView) findViewById(R.id.showpre_back);
        mBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.showpre_back:
                finish();
                break;
            default:
                break;
        }
    }
}
