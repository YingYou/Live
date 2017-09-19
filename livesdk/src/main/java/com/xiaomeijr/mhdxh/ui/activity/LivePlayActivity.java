package com.xiaomeijr.mhdxh.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.apkfuns.logutils.LogUtils;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;
import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;
import com.xiaomeijr.mhdxh.base.LiveKit;
import com.xiaomeijr.mhdxh.controller.ChatListAdapter;
import com.xiaomeijr.mhdxh.data.RUserInfo;
import com.xiaomeijr.mhdxh.data.Removebean;
import com.xiaomeijr.mhdxh.fakeserver.FakeServer;
import com.xiaomeijr.mhdxh.fakeserver.HttpUtil;
import com.xiaomeijr.mhdxh.model.NetWorkRequest;
import com.xiaomeijr.mhdxh.model.UIDataListener;
import com.xiaomeijr.mhdxh.ui.animation.HeartLayout;
import com.xiaomeijr.mhdxh.ui.fragment.BottomPanelFragment_liveplay;
import com.xiaomeijr.mhdxh.ui.message.GiftMessage;
import com.xiaomeijr.mhdxh.ui.widget.ChatListView;
import com.xiaomeijr.mhdxh.ui.widget.DialogProgress;
import com.xiaomeijr.mhdxh.ui.widget.InputPanel2;
import com.xiaomeijr.mhdxh.ui.widget.myAlertDialog;
import com.xiaomeijr.mhdxh.ui.widget.myDialog;
import com.xiaomeijr.mhdxh.utils.ACache;
import com.xiaomeijr.mhdxh.utils.Constant;
import com.xiaomeijr.mhdxh.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * 手机直播播放端界面
 */
public class LivePlayActivity extends BaseActivity implements View.OnClickListener, Handler.Callback,
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnVideoSizeChangedListener,
        PLMediaPlayer.OnErrorListener, UIDataListener {
    private ViewGroup background;
    private ChatListView chatListView;
    private BottomPanelFragment_liveplay bottomPanel;
    private ImageView btnGift;
    private ImageView btnHeart;
    private HeartLayout heartLayout;

    private Random random = new Random();
    private Handler handler = new Handler(this);
    private ChatListAdapter chatListAdapter;
    private String roomId;
    private PLVideoTextureView mVideoView;
    private ImageView mCross;
    private boolean isPortrait = true;
    private ImageView mBack;
    private ImageView mHead;
    private ImageView btnShare;
    private String token;
    private String url;
    private String shareTitle;
    private String imageUrl;
    private String teacherAccount;
    private String shareContent;
    private String shareUrl;
    private TextView mNum;
    private Dialog dialog;
    private NetWorkRequest request;
    private ACache mAcache;
    private RUserInfo rUserInfo;
    private TextView mName;
    private TextView mLike;
    private int focusState = 0;
    private RUserInfo mZhuBoInfo;
    private Handler handler2 = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {//刷新人数
            getNum();
//            LogUtils.d("获取人数");
            handler2.postDelayed(this, 2000);
        }
    };
    private Handler checkremoveHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_live_play;
    }

    @Override
    protected void initUI() {
        background = (ViewGroup) findViewById(R.id.background);
        chatListView = (ChatListView) findViewById(R.id.chat_listview);
        bottomPanel = (BottomPanelFragment_liveplay) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        btnGift = (ImageView) bottomPanel.getView().findViewById(R.id.btn_gift);
        btnHeart = (ImageView) bottomPanel.getView().findViewById(R.id.btn_heart);
        btnShare = (ImageView) bottomPanel.getView().findViewById(R.id.btn_share);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mCross = (ImageView) findViewById(R.id.pc_videoplay_cross);
        mBack = (ImageView) findViewById(R.id.img_back);
        mHead = (ImageView) findViewById(R.id.img_head);
        mVideoView = (PLVideoTextureView) findViewById(R.id.liveplay_PLVideoTextureView);

        mNum = (TextView) findViewById(R.id.text_num);
        mName = (TextView) findViewById(R.id.text_name);
        mLike = (TextView) findViewById(R.id.tv_guanzhu);

        GlideImgManager.glideLoader(this, "http://img.besoo.com/file/201705/27/0925236345908.png", R.mipmap.ic_launcher, R.mipmap.ic_launcher, mHead, 0);

        background.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnHeart.setOnClickListener(this);
        mCross.setOnClickListener(this);
        mBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        mLike.setOnClickListener(this);
        bottomPanel.setInputPanelListener(new InputPanel2.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                if (getDialog()) {
                    return;
                }
                final TextMessage content = TextMessage.obtain(text);
                LiveKit.sendMessage(content);
            }
        });

//        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
//                final myDialog myDialog = new myDialog(LivePlayActivity.this);
//
//                if (!chatListAdapter.getuserId(i).equals(rUserInfo.getUserId())) {
//                    myDialog.setContenttext(chatListAdapter.getuserName(i));
//                    myDialog.setImg(chatListAdapter.getuserImg(i));
//                    myDialog.setYesOnclickListener("禁言", new myDialog.onYesOnclickListener() {
//                        @Override
//                        public void onYesClick() {
//                            opolUserId = chatListAdapter.getuserId(i);
//                            Map map = new HashMap();
//                            map.put("type", "0");
//                            map.put("token", rUserInfo.getToken());
//                            map.put("beUserId", chatListAdapter.getuserId(i));
//                            request.doPostRequest(6, true, Constant.GetQuanxian, map);
//                            myDialog.dismiss();
//                        }
//                    });
//                    myDialog.setNoOnclickListener("踢出频道", new myDialog.onNoOnclickListener() {
//                        @Override
//                        public void onNoClick() {
//                            //踢出
//                            opolUserId = chatListAdapter.getuserId(i);
//                            Map map = new HashMap();
//                            map.put("type", "1");
//                            map.put("token", rUserInfo.getToken());
//                            map.put("beUserId", chatListAdapter.getuserId(i));
//                            request.doPostRequest(7, true, Constant.GetQuanxian, map);
//                            myDialog.dismiss();
//                        }
//                    });
//                    myDialog.show();
//                }
//            }
//        });

    }

    @Override
    protected void initData() {

        dialog = DialogProgress.createLoadingDialog(LivePlayActivity.this, "", this);
        request = new NetWorkRequest(LivePlayActivity.this, this);
        mAcache = ACache.get(LivePlayActivity.this);
        rUserInfo = (RUserInfo) mAcache.getAsObject("UserInfo");

        chatListAdapter = new ChatListAdapter(LivePlayActivity.this,rUserInfo,request);
        chatListView.setAdapter(chatListAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra("json")) {
            try {
                String json = intent.getStringExtra("json");
                JSONObject object = JSONObject.parseObject(json);
                url = object.getString("url");
                imageUrl = object.getString("imageUrl");
                shareTitle = object.getString("shareTitle");
                shareContent = object.getString("shareContent");
                shareUrl = object.getString("shareUrl");
                teacherAccount = object.getString("teacherAccount");
                token = object.getString("token");
            }catch (Exception e){
                LogUtils.d(e.getMessage());
            }
        }

        //获取主播详细信息
        Map map = new HashMap();
//        map.put("token", rUserInfo.getToken() + "");
        map.put("loginNo", teacherAccount);
        request.doPostRequest(0, true, Constant.GetShowInfos, map);

        LiveKit.addEventHandler(handler);
        startLiveShow();

        handler2.postDelayed(runnable, 2000);

        //轮询封禁禁言成员
        checkremoveHandler = new Handler();
        checkremoveHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FakeServer.checkRemove(Constant.CheckRemove, teacherAccount, new HttpUtil.OnResponse() {
                    @Override
                    public void onResponse(int code, String body) {
                        Removebean result = JSON.parseObject(body, new TypeReference<Removebean>() {
                        });
                        if (result.getCode() == 200) {
                            ArrayList<Removebean.UsersBean> userList = (ArrayList) result.getUsers();
                            for (Removebean.UsersBean b : userList) {
                                if (b.getUserId().equals(rUserInfo.getUserId())) {
                                    final myAlertDialog myAlertDialog = new myAlertDialog(LivePlayActivity.this);
                                    myAlertDialog.setContenttext("你已经被禁止进入该直播间!");
                                    myAlertDialog.setShowNo(false);
                                    myAlertDialog.setYesOnclickListener("确定", new myAlertDialog.onYesOnclickListener() {
                                        @Override
                                        public void onYesClick() {
                                            myAlertDialog.dismiss();
                                            finish();
                                        }
                                    });
                                    myAlertDialog.show();
                                }
                            }
                        }
                    }
                });
                checkremoveHandler.postDelayed(this,30000);
            }
        },30000);
    }

    private void startLiveShow() {
        roomId = teacherAccount;
        joinChatRoom(roomId);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);//设置画面预览模式
        mVideoView.setDisplayOrientation(0); // 旋转90度
//        mVideoView.setMirror(true);//设置播放画面镜像变换
//        mVideoView.setVideoPath("rtmp://pili-live-rtmp.test.mfc.com.cn/cz-test/test001");
//        mVideoView.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        mVideoView.setVideoPath(url);
        LogUtils.d(url);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnVideoSizeChangedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        if (!bottomPanel.onBackAction()) {
            finish();
            return;
        }
    }

    private void joinChatRoom(final String roomId) {
        LiveKit.joinChatRoom(roomId, 2, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                final InformationNotificationMessage content = InformationNotificationMessage.obtain("进入了直播间");
                LiveKit.sendMessage(content);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.d(errorCode);
                if (errorCode== RongIMClient.ErrorCode.KICKED_FROM_CHATROOM){
                    if (mVideoView!=null){
                        mVideoView.stopPlayback();
                    }
                    final myAlertDialog myAlertDialog = new myAlertDialog(LivePlayActivity.this);
                    myAlertDialog.setShowNo(false);
                    myAlertDialog .setContenttext("你已经被禁止进入该直播间!");
                    myAlertDialog.setYesOnclickListener("确定", new myAlertDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            myAlertDialog.dismiss();
                            finish();
                        }
                    });
                    myAlertDialog.show();
                }
                Toast.makeText(LivePlayActivity.this, "聊天室加入失败!", Toast.LENGTH_SHORT).show();
            }
        });
        getNum();
    }

    private void getNum() {
        RongIMClient.getInstance().getChatRoomInfo(roomId, 2, ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC, new RongIMClient.ResultCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo chatRoomInfo) {
                mNum.setText("人数:" + chatRoomInfo.getTotalMemberCount());
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(background)) {
            bottomPanel.onBackAction();
        } else if (v.equals(btnGift)) {//送花
            if (getDialog()) {
                return;
            }
            Map map = new HashMap();
            map.put("token", rUserInfo.getToken() + "");
            map.put("type", 2 + "");
            map.put("number", 1 + "");
            map.put("userId", mZhuBoInfo.getUserId());
            request.doPostRequest(3, false, Constant.sendGifts, map);
        } else if (v.equals(btnHeart)) {
            heartLayout.post(new Runnable() {
                @Override
                public void run() {
                    int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                    heartLayout.addHeart(rgb);
                }
            });
            GiftMessage msg = new GiftMessage("1", "为您点赞");
            LiveKit.sendMessage(msg);
        } else if (v.equals(mCross)) {
            if (isPortrait) {//设置横屏
                isPortrait = false;
                mCross.setImageResource(R.drawable.btn_suoxiao);
                mVideoView.setDisplayOrientation(-90); // 旋转90度
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                isPortrait = true;
                mCross.setImageResource(R.drawable.btn_fangda);
                mVideoView.setDisplayOrientation(0); // 旋转-90度
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if (v.equals(mBack)) {
            onBackPressed();
        } else if (v.equals(btnShare)) {
            if (!shareUrl.startsWith("http://")) {
                shareUrl = "http://" + shareUrl;
            }
            LogUtils.d(shareUrl+"/"+shareTitle+"/"+imageUrl+"/"+shareContent);
            UMWeb web = new UMWeb(shareUrl);
            web.setTitle(shareTitle);//标题
//            UMImage image = new UMImage(LivePlayActivity.this, imageUrl);//资源文件
//            web.setThumb(image);  //缩略图
            web.setDescription(shareContent);//描述
            new ShareAction(LivePlayActivity.this)
                    .withMedia(web)
                    .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                    .setCallback(umShareListener).open();
        } else if (v.equals(mLike)) {
            if (getDialog()) {
                return;
            }
            //关注/取消关注
            Map map = new HashMap();
            map.put("token", rUserInfo.getToken() + "");
            map.put("expertId", mZhuBoInfo.getUserId());
            map.put("status", (focusState == 0 ? 1 : 0) + "");
            request.doPostRequest(2, true, Constant.GetFocus, map);
        }
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case LiveKit.MESSAGE_ARRIVED: {
                MessageContent content = (MessageContent) msg.obj;
                chatListAdapter.addMessage(content);
                break;
            }
            case LiveKit.MESSAGE_SENT: {
                MessageContent content = (MessageContent) msg.obj;
                chatListAdapter.addMessage(content);
                break;
            }
            case LiveKit.MESSAGE_SEND_ERROR: {
                if (msg.arg1 == 23408) {
                    final myAlertDialog myAlertDialog = new myAlertDialog(LivePlayActivity.this);
                    myAlertDialog.setContenttext("你已被禁言!");
                    myAlertDialog.setShowNo(false);
                    myAlertDialog.setYesOnclickListener("确定", new myAlertDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            myAlertDialog.dismiss();
                        }
                    });
                    myAlertDialog.show();
                }
                break;
            }
            default:
        }
        chatListAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    protected void onDestroy() {
        LiveKit.quitChatRoom(new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LiveKit.removeEventHandler(handler);
                LiveKit.logout();
                Toast.makeText(LivePlayActivity.this, "退出聊天室成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LiveKit.removeEventHandler(handler);
                LiveKit.logout();
                Toast.makeText(LivePlayActivity.this, "退出聊天室失败!", Toast.LENGTH_SHORT).show();
            }
        });
        mVideoView.stopPlayback();
        if (handler2 != null && runnable != null) {
            handler2.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {

    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
        return false;
    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        plMediaPlayer.start();
    }

    @Override
    public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1, int i2, int i3) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);

            Toast.makeText(LivePlayActivity.this, "分享成功!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LivePlayActivity.this, "分享失败!", Toast.LENGTH_SHORT).show();
                }
            });
            LogUtils.e(t);
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(LivePlayActivity.this, "分享取消!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void loadDataFinish(int code, Object data) {
        if (code == 0) {
            mZhuBoInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mZhuBoInfo);
            if (mZhuBoInfo != null) {
                GlideImgManager.glideLoader(LivePlayActivity.this, mZhuBoInfo.getUserImage(), R.drawable.rc_image_error, R.drawable.rc_image_error, mHead, 0);
                mName.setText(mZhuBoInfo.getNickName());
                //获取关注状态
                Map map2 = new HashMap();
                map2.put("token", rUserInfo.getToken() + "");
                map2.put("expertId", mZhuBoInfo.getUserId());
                request.doPostRequest(1, true, Constant.GetFocusState, map2);
            }
        } else if (code == 1) {
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                focusState = Integer.parseInt(mUserInfo.getFocus());
                if (mUserInfo.getFocus().equals("0")) {
                    mLike.setText("+ 关注");
                    mLike.setBackgroundResource(R.drawable.like_back);
                } else if (mUserInfo.getFocus().equals("1")) {
                    mLike.setText("已关注");
                    mLike.setBackgroundResource(R.drawable.like_back2);
                }
            }
        } else if (code == 2) {
            if (focusState == 1) {
                focusState = 0;
                mLike.setText("+ 关注");
                mLike.setBackgroundResource(R.drawable.like_back);
            } else if (focusState == 0) {
                focusState = 1;
                mLike.setText("已关注");
                mLike.setBackgroundResource(R.drawable.like_back2);
            }
        } else if (code == 3) {
            GiftMessage msg = new GiftMessage("2", "");
            LiveKit.sendMessage(msg);
        } else if (code == 4) {
            LogUtils.d(data);
            JSONObject object = JSONObject.parseObject(data.toString());
            if (object.containsKey("code")){
                int codes = object.getIntValue("code");
                if (code==200){
                    showToast("操作成功！");
                }else {
                    showToast("操作失败！");
                }
            }else {
                showToast("操作失败！");
            }
        }else if (code == 6){//禁言
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                String status = mUserInfo.getStatus();
                if (status.equals("1")){
                    //禁言
                    FakeServer.remove(Constant.NotTalk, chatListAdapter.getOpolUserId(), roomId + "", Constant.NotTalkTime, new HttpUtil.OnResponse() {
                        @Override
                        public void onResponse(int code, String body) {
                            LogUtils.d(code+body);
                            if (code==200){
                                showToast("禁言成功！");
                            }else {
                                showToast("禁言失败！");
                            }
                        }
                    });
                }else {
                    showToast("无操作权限！");
                }
            }
        }else if (code == 7){//封禁
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                String status = mUserInfo.getStatus();
                if (status.equals("1")){
                    FakeServer.remove(Constant.Remove, chatListAdapter.getOpolUserId(), roomId + "", Constant.RemoveTime, new HttpUtil.OnResponse() {
                        @Override
                        public void onResponse(int code, String body) {
                            LogUtils.d(code+body);
                            if (code==200){
                                showToast("踢出成功！");
                            }else {
                                showToast("踢出失败！");
                            }
                        }
                    });
                }else {
                    showToast("无操作权限！");
                }
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(LivePlayActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog() {
        if (dialog != null && !isFinishing())
            dialog.show();
    }

    @Override
    public void dismissDialog() {
        if (dialog != null && !isFinishing())
            dialog.dismiss();
    }

    @Override
    public void onError(String errorCode, String errorMessage) {
        showToast(errorMessage);
    }

    @Override
    public void cancelRequest() {
        request.CancelPost();
    }

    private boolean getDialog() {
        if (TextUtils.isEmpty(rUserInfo.getToken())) {
            final myAlertDialog myAlertDialog = new myAlertDialog(LivePlayActivity.this);
            myAlertDialog .setContenttext("您还未登录，请先登录！");
            myAlertDialog .setNoOnclickListener("取消", new myAlertDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    myAlertDialog.dismiss();
                }
            });
            myAlertDialog.setYesOnclickListener("确定", new myAlertDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    myAlertDialog.dismiss();
                    finish();
                }
            });
            myAlertDialog.show();
            return true;
        }
        return false;
    }
}
