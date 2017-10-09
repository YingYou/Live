package com.xiaomeijr.mhdxh.ui.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.controller.Const;

import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;

public class InfoMsgView extends BaseMsgView {

    private TextView username;
    private TextView infoText;
    private Context context;
    private LinearLayout ll;

    public InfoMsgView(Context context) {
        super(context);
        this.context = context;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_info_view, this);
        username = (TextView) view.findViewById(R.id.username);
        infoText = (TextView) view.findViewById(R.id.info_text);
        ll = (LinearLayout) view.findViewById(R.id.ll);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        InformationNotificationMessage msg = (InformationNotificationMessage) msgContent;
        if (Const.MsgType==1){
            username.setTextColor(context.getResources().getColor(R.color.text_name));
            infoText.setTextColor(context.getResources().getColor(R.color.text_name));
            ll.setBackground(null);
        }
        username.setText(msg.getUserInfo().getName() + " ");
        infoText.setText(msg.getMessage());
    }

    @Override
    public View getName() {
        return username;
    }
}
