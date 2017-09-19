package com.xiaomeijr.mhdxh.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xiaomeijr.mhdxh.base.LiveKit;
import com.xiaomeijr.mhdxh.data.RUserInfo;
import com.xiaomeijr.mhdxh.model.NetWorkRequest;
import com.xiaomeijr.mhdxh.ui.activity.PcVideoPlayActivity;
import com.xiaomeijr.mhdxh.ui.message.BaseMsgView;
import com.xiaomeijr.mhdxh.ui.message.UnknownMsgView;
import com.xiaomeijr.mhdxh.ui.widget.myDialog;
import com.xiaomeijr.mhdxh.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

public class ChatListAdapter extends BaseAdapter {

    private ArrayList<MessageContent> msgList;
    private Context context;
    private RUserInfo rUserInfo;
    private NetWorkRequest request;
    private String opolUserId;

    public ChatListAdapter(Context context, RUserInfo rUserInfo, NetWorkRequest request) {
        msgList = new ArrayList<>();
        this.context = context;
        this.rUserInfo = rUserInfo;
        this.request = request;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    public String getuserName(int i) {
        return msgList.get(i).getUserInfo().getName();
    }

    public String getuserImg(int i) {
        return msgList.get(i).getUserInfo().getPortraitUri().toString();
    }

    public String getuserId(int i) {
        return msgList.get(i).getUserInfo().getUserId();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseMsgView baseMsgView = (BaseMsgView) convertView;
        MessageContent msgContent = msgList.get(position);
        final  UserInfo userInfo = msgContent.getUserInfo();
        Class<? extends BaseMsgView> msgViewClass = LiveKit.getRegisterMessageView(msgContent.getClass());
        if (msgViewClass == null) {
            baseMsgView = new UnknownMsgView(parent.getContext());
        } else if (baseMsgView == null || baseMsgView.getClass() != msgViewClass) {
            try {
                baseMsgView = msgViewClass.getConstructor(Context.class).newInstance(parent.getContext());
                baseMsgView.getName().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final myDialog myDialog = new myDialog(context);

                        if (rUserInfo!=null&&userInfo!=null&&!userInfo.getUserId().equals(rUserInfo.getUserId())) {
                            myDialog.setContenttext(userInfo.getName());
                            myDialog.setImg(userInfo.getPortraitUri().toString());
                            myDialog.setYesOnclickListener("禁言", new myDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    //禁言
                                    opolUserId = userInfo.getUserId();
                                    Map map = new HashMap();
                                    map.put("type", "0");
                                    map.put("token", rUserInfo.getToken());
                                    map.put("beUserId", userInfo.getUserId());
                                    request.doPostRequest(6, true, Constant.GetQuanxian, map);
                                    myDialog.dismiss();
                                }
                            });
                            myDialog.setNoOnclickListener("踢出频道", new myDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    //踢出
                                    opolUserId = userInfo.getUserId();
                                    Map map = new HashMap();
                                    map.put("type", "1");
                                    map.put("token", rUserInfo.getToken());
                                    map.put("beUserId", userInfo.getUserId());
                                    request.doPostRequest(7, true, Constant.GetQuanxian, map);
                                    myDialog.dismiss();
                                }
                            });
                            myDialog.show();
                        }
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException("baseMsgView newInstance failed.");
            }


        }
        baseMsgView.setContent(msgContent);
        return baseMsgView;
    }

    public void addMessage(MessageContent msg) {
        msgList.add(msg);
    }

    public String getOpolUserId(){
        return opolUserId;
    }
}
