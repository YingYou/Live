package com.xiaomeijr.mhdxh.ui.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.controller.EmojiManager;

import io.rong.imlib.model.MessageContent;

public class GiftMsgView extends BaseMsgView {

    private TextView username;
    private TextView content;

    public GiftMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_gift_view, this);
        username = (TextView) view.findViewById(R.id.username);
        content = (TextView) view.findViewById(R.id.content);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        GiftMessage msg = (GiftMessage) msgContent;
        username.setText(msg.getUserInfo().getName() + " ");
    }

    @Override
    public View getName() {
        return username;
    }
}
