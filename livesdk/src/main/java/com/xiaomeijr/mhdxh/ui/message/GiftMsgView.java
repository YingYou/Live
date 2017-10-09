package com.xiaomeijr.mhdxh.ui.message;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.controller.Const;
import com.xiaomeijr.mhdxh.controller.EmojiManager;

import io.rong.imlib.model.MessageContent;

public class GiftMsgView extends BaseMsgView {

    private TextView username;
    private TextView content;
    private LinearLayout ll;
    private Context context;

    public GiftMsgView(Context context) {
        super(context);
        this.context = context;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_gift_view, this);
        username = (TextView) view.findViewById(R.id.username);
        content = (TextView) view.findViewById(R.id.content);
        ll = (LinearLayout) view.findViewById(R.id.ll);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        if (Const.MsgType==1){
            username.setTextColor(context.getResources().getColor(R.color.text_name));
            content.setTextColor(context.getResources().getColor(R.color.text_content));
            ll.setBackground(null);
        }
        GiftMessage msg = (GiftMessage) msgContent;
        username.setText(msg.getUserInfo().getName() + " ");
        ImageSpan imgSpan = new ImageSpan(context, R.drawable.img_rose_small);
        ImageSpan imgSpan2 = new ImageSpan(context, R.drawable.img_rose_small);
        ImageSpan imgSpan3 = new ImageSpan(context, R.drawable.img_rose_small);
        SpannableString spannableString = new SpannableString("赠送给主播        ");
        spannableString.setSpan(imgSpan, 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(imgSpan2, 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(imgSpan3, 8, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(spannableString);
    }

    @Override
    public View getName() {
        return username;
    }
}
