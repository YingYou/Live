package com.xiaomeijr.mhdxh.ui.message;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

public abstract class BaseMsgView extends RelativeLayout {

    public BaseMsgView(Context context) {
        super(context);
    }

    public abstract void setContent(MessageContent msgContent);

    public abstract View getName();
}
