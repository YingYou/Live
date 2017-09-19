package com.xiaomeijr.mhdxh.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.apkfuns.logutils.LogUtils;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
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
import com.xiaomeijr.mhdxh.ui.fragment.BottomPanelFragment_pc;
import com.xiaomeijr.mhdxh.ui.message.GiftMessage;
import com.xiaomeijr.mhdxh.ui.widget.ChatListView;
import com.xiaomeijr.mhdxh.ui.widget.DialogProgress;
import com.xiaomeijr.mhdxh.ui.widget.InputPanel_pc;
import com.xiaomeijr.mhdxh.ui.widget.myAlertDialog;
import com.xiaomeijr.mhdxh.ui.widget.myDialog;
import com.xiaomeijr.mhdxh.utils.ACache;
import com.xiaomeijr.mhdxh.utils.Constant;
import com.xiaomeijr.mhdxh.utils.DanmuAdapter;
import com.xiaomeijr.mhdxh.utils.DanmuEntity;
import com.xiaomeijr.mhdxh.utils.GlideImgManager;
import com.xiaomeijr.mhdxh.utils.ScreenUtils;

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
 * 电脑直播播放端界面
 */
public class PcVideoPlayActivity extends BaseActivity implements View.OnKeyListener, View.OnClickListener, Handler.Callback,
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnVideoSizeChangedListener,
        PLMediaPlayer.OnErrorListener,
        UIDataListener {
    private ViewGroup background;
    private ChatListView chatListView;
    private BottomPanelFragment_pc bottomPanel;
    private ImageView btnGift;
    private ImageView btnHeart;
    private HeartLayout heartLayout;

    private Random random = new Random();
    private Handler handler = new Handler(this);
    private ChatListAdapter chatListAdapter;
    private String roomId;
    private PLVideoTextureView mVideoView;
    private ImageView mCross;
    private ImageView mBack;
    private TextView mTitle;

    private boolean isPortrait = true;
    private boolean isStart = true;
    private DanmuContainerView mDanmu;
    private boolean isDm = false;
    private TextView mLiaotian;
    private TextView mZhubo;
    private View mLiaotian_line;
    private View mZhubo_line;
    private LinearLayout mRl_zhubo;
    private LinearLayout mLl_head;
    private LinearLayout mLl_title;
    private LinearLayout mLl_title2;
    private LinearLayout mBottomIcom;
    private ImageView mhead;
    private LinearLayout mLl_cross;
    private TextView mNum;
    private LinearLayout mLl_Bottom;
    private ImageView mShare1;
    private ImageView mShare2;
    private TextView btnSend;
    private ImageView mZanting1;
    private ImageView mZanting2;
    private ImageView mShuaxin;
    private EditText inputEt;
    private PopupWindow popWnd;
    private String token;
    private String url;
    private String teacherAccount;
    private String imageUrl;
    private String shareTitle;
    private String shareContent;
    private String shareUrl;
    private Dialog dialog;
    private NetWorkRequest request;
    private ACache mAcache;
    private RUserInfo rUserInfo;
    private TextView mName;
    private TextView mDesc;
    private TextView mLike;
    private int focusState = 0;
    private RUserInfo mZhuBoInfo;
    private ImageView mLike2;
    private EffectInVisiableHandler mtimeHandler;
    private final int MOBILE_QUERY = 1;
    private Handler handler2 = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {//刷新人数
            getNum();
//            LogUtils.d("获取人数");
            handler2.postDelayed(this, 2000);
        }
    };
    private com.xiaomeijr.mhdxh.ui.widget.myAlertDialog myAlertDialog;
    private LinearLayout mSpace;
    private LinearLayout mSpace2;
    private Handler checkremoveHandler;
    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        //全屏，保持屏幕常亮
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_pc_video_play;
    }

    @Override
    protected void initData() {
        mtimeHandler = new EffectInVisiableHandler();
        Message msg = mtimeHandler.obtainMessage(MOBILE_QUERY);
        mtimeHandler.sendMessageDelayed(msg, 5000);

        View.OnTouchListener touchCenterLayoutListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setViewVisible(View.VISIBLE);
                        resetTime();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        };
        mVideoView.setOnTouchListener(touchCenterLayoutListener);

        dialog = DialogProgress.createLoadingDialog(PcVideoPlayActivity.this, "", this);
        request = new NetWorkRequest(PcVideoPlayActivity.this, this);
        mAcache = ACache.get(PcVideoPlayActivity.this);
        rUserInfo = (RUserInfo) mAcache.getAsObject("UserInfo");

        chatListAdapter = new ChatListAdapter(PcVideoPlayActivity.this,rUserInfo,request);
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
            } catch (Exception e) {
                LogUtils.d(e.getMessage());
            }
        }

        //获取主播详细信息
        Map map = new HashMap();
//        map.put("token", rUserInfo.getToken() + "");
        map.put("loginNo", teacherAccount);
        request.doPostRequest(0, true, Constant.GetShowInfos, map);

        LiveKit.addEventHandler(handler);
        roomId = teacherAccount;
        joinChatRoom(roomId);
        startLiveShow(url);

        handler2.postDelayed(runnable, 2000);

        View contentView = LayoutInflater.from(PcVideoPlayActivity.this).inflate(R.layout.popwindow_pc, null);
        popWnd = new PopupWindow(PcVideoPlayActivity.this);
        popWnd.setContentView(contentView);
        popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setOutsideTouchable(true);

        contentView.findViewById(R.id.shang).setOnClickListener(this);
        contentView.findViewById(R.id.xia).setOnClickListener(this);
        contentView.findViewById(R.id.quan).setOnClickListener(this);
        contentView.findViewById(R.id.guan).setOnClickListener(this);


        //轮询封禁与禁言成员
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
                                    final myAlertDialog myAlertDialog = new myAlertDialog(PcVideoPlayActivity.this);
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
                checkremoveHandler.postDelayed(this, 30000);
            }
        }, 30000);
    }

    @Override
    protected void initUI() {
        background = (ViewGroup) findViewById(R.id.background);
        chatListView = (ChatListView) findViewById(R.id.chat_listview);
        bottomPanel = (BottomPanelFragment_pc) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        btnGift = (ImageView) bottomPanel.getView().findViewById(R.id.btn_gift);
        btnSend = (TextView) bottomPanel.getView().findViewById(R.id.input_send);
        inputEt = (EditText) bottomPanel.getView().findViewById(R.id.input_editor);
        btnHeart = (ImageView) bottomPanel.getView().findViewById(R.id.btn_heart);
        mShare1 = (ImageView) bottomPanel.getView().findViewById(R.id.img_share1);
        mSpace = (LinearLayout) bottomPanel.getView().findViewById(R.id.space);
        mSpace2 = (LinearLayout) bottomPanel.getView().findViewById(R.id.space2);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mDanmu = (DanmuContainerView) findViewById(R.id.danmuContainerView);

        mVideoView = (PLVideoTextureView) findViewById(R.id.PLVideoTextureView);

        mLiaotian = (TextView) findViewById(R.id.text_liaotian);
        mZhubo = (TextView) findViewById(R.id.text_zhubo);
        mName = (TextView) findViewById(R.id.text_name);
        mDesc = (TextView) findViewById(R.id.text_desc);
        mLike = (TextView) findViewById(R.id.text_like);
        mRl_zhubo = (LinearLayout) findViewById(R.id.rl_zhubo);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        mLl_head = (LinearLayout) findViewById(R.id.ll_head);
        mLl_title = (LinearLayout) findViewById(R.id.ll_title);
        mLl_title2 = (LinearLayout) findViewById(R.id.ll_title2);
        mBottomIcom = (LinearLayout) findViewById(R.id.ll_bottomIcon);
        mLl_Bottom = (LinearLayout) findViewById(R.id.bottom_bar_ll);
        mLl_cross = (LinearLayout) findViewById(R.id.ll_cross);
        mLiaotian_line = findViewById(R.id.view_liaotian);
        mZhubo_line = findViewById(R.id.view_zhubo);

        mCross = (ImageView) findViewById(R.id.pc_videoplay_cross);
        mBack = (ImageView) findViewById(R.id.btn_back);
        mhead = (ImageView) findViewById(R.id.img_head);
        mShare2 = (ImageView) findViewById(R.id.img_share2);
        mZanting1 = (ImageView) findViewById(R.id.pc_videoplay_zanting1);
        mZanting2 = (ImageView) findViewById(R.id.img_zanting2);
        mShuaxin = (ImageView) findViewById(R.id.img_shuaxin);
        mLike2 = (ImageView) findViewById(R.id.btn_like);
        mTitle = (TextView) findViewById(R.id.text_title);
        mNum = (TextView) findViewById(R.id.text_num);

        GlideImgManager.glideLoader(this, "http://img.besoo.com/file/201705/27/0925236345908.png", R.mipmap.ic_launcher, R.mipmap.ic_launcher, mhead, 0);

        inputEt.setOnKeyListener(this);


        mBack.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        mCross.setOnClickListener(this);
        mLiaotian.setOnClickListener(this);
        mZhubo.setOnClickListener(this);
        mZanting1.setOnClickListener(this);
        mShuaxin.setOnClickListener(this);
        mShare1.setOnClickListener(this);
        mShare2.setOnClickListener(this);
        mLike.setOnClickListener(this);
        mLike2.setOnClickListener(this);

        background.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnHeart.setOnClickListener(this);
        bottomPanel.setInputPanelListener(new InputPanel_pc.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                LogUtils.d("2222" + isPortrait + text);
                if (isPortrait) {
                    if (getDialog()) {
                        return;
                    }
//                    if (IsNotTalk){
//                        final myAlertDialog myAlertDialog = new myAlertDialog(PcVideoPlayActivity.this);
//                        myAlertDialog.setContenttext("你已被禁言!");
//                        myAlertDialog.setShowNo(false);
//                        myAlertDialog.setYesOnclickListener("确定", new myAlertDialog.onYesOnclickListener() {
//                            @Override
//                            public void onYesClick() {
//                                myAlertDialog.dismiss();
//                            }
//                        });
//                        myAlertDialog.show();
//                        return;
//                    }
                    final TextMessage content = TextMessage.obtain(text);
                    if (!TextUtils.isEmpty(content.getContent())) {
                        LiveKit.sendMessage(content);
                        inputEt.getText().clear();
                    }
                }
            }
        });
        setDanmu();

//        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
//                final myDialog myDialog = new myDialog(PcVideoPlayActivity.this);
//
//                if (!chatListAdapter.getuserId(i).equals(rUserInfo.getUserId())) {
//                    myDialog.setContenttext(chatListAdapter.getuserName(i));
//                    myDialog.setImg(chatListAdapter.getuserImg(i));
//                    myDialog.setYesOnclickListener("禁言", new myDialog.onYesOnclickListener() {
//                        @Override
//                        public void onYesClick() {
//                            //禁言
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

    private void startLiveShow(String url) {


//        View loadingView = findViewById(R.id.LoadingView);
//        mVideoView.setBufferingIndicator(loadingView);
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);//设置画面预览模式
//        mVideoView.setDisplayOrientation(0); // 旋转90度
//        mVideoView.setMirror(true);//设置播放画面镜像变换
//        mVideoView.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
//        mVideoView.setVideoPath("rtmp://pili-live-rtmp.test.mfc.com.cn/cz-test/15721225578");
        mVideoView.setVideoPath(url);
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
            if (isPortrait) {
                finish();
            } else {
                onClick(mCross);
            }
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
                if (errorCode == RongIMClient.ErrorCode.KICKED_FROM_CHATROOM) {
                    if (mVideoView != null) {
                        mVideoView.stopPlayback();
                    }

                    final myAlertDialog myAlertDialog = new myAlertDialog(PcVideoPlayActivity.this);
                    myAlertDialog.setShowNo(false);
                    myAlertDialog.setContenttext("你已经被禁止进入该直播间!");
                    myAlertDialog.setYesOnclickListener("确定", new myAlertDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            myAlertDialog.dismiss();
                            finish();
                        }
                    });
                    myAlertDialog.show();
                }
                Toast.makeText(PcVideoPlayActivity.this, "聊天室加入失败!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //获取在线人数
    private void getNum() {
        RongIMClient.getInstance().getChatRoomInfo(roomId, 2, ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC, new RongIMClient.ResultCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo chatRoomInfo) {
                mNum.setText("人数：" + chatRoomInfo.getTotalMemberCount());
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private void setDanmu() {
        DanmuAdapter danmuAdapter = new DanmuAdapter(PcVideoPlayActivity.this);
        mDanmu.setAdapter(danmuAdapter);

        mDanmu.setSpeed(DanmuContainerView.HIGH_SPEED);

        mDanmu.setGravity(DanmuContainerView.GRAVITY_FULL);


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
        } else if (v.equals(mBack) || v.equals(ll_back)) {
            if (isPortrait) {
                onBackPressed();
            } else {
                onClick(mCross);
            }
        } else if (v.equals(mCross)) {
            Log.d("mCross", isPortrait + "");
            if (isPortrait) {//设置横屏
                isPortrait = false;
                mSpace.setVisibility(View.VISIBLE);
                mSpace2.setVisibility(View.VISIBLE);
                if (focusState == 0) {
                    mLike2.setImageResource(R.drawable.btn_guanzhu_h);
                } else if (focusState == 1) {
                    mLike2.setImageResource(R.drawable.btn_guanzhu_n);
                }
                mBack.setImageResource(R.drawable.btn_back);
                mDanmu.setVisibility(View.VISIBLE);
                mCross.setVisibility(View.GONE);
                mVideoView.setDisplayOrientation(-90); // 旋转90度
                FrameLayout.LayoutParams ll_lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                mVideoView.setLayoutParams(ll_lp);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                chatListView.setVisibility(View.GONE);
                isDm = true;
                mLl_head.setVisibility(View.GONE);
                mNum.setVisibility(View.GONE);
                mLl_cross.setVisibility(View.GONE);
                mLl_title.setBackgroundColor(getResources().getColor(R.color.black_back_transparent));
                mLl_title2.setVisibility(View.VISIBLE);
                mBottomIcom.setVisibility(View.VISIBLE);
                mLl_Bottom.setBackgroundColor(getResources().getColor(R.color.black_back_transparent));
//                mShare1.setVisibility(View.GONE);
                btnGift.setImageResource(R.drawable.btn_flower_bai);
                btnSend.setVisibility(View.GONE);
                mShare1.setImageResource(R.drawable.btn_danmu_quan);
//                btnSend.setBackgroundResource(R.drawable.btn_danmu_quan);
                Log.d("mCross", "SCREEN_ORIENTATION_LANDSCAPE");
            } else {                      //设置竖屏
                Log.d("mCross", "SCREEN_ORIENTATION_PORTRAIT");
                isPortrait = true;
                setViewVisible(View.VISIBLE);
                mSpace.setVisibility(View.GONE);
                mSpace2.setVisibility(View.GONE);
                if (focusState == 0) {
                    mLike.setText("+ 关注");
                    mLike.setBackgroundResource(R.drawable.like_back);
                } else if (focusState == 1) {
                    mLike.setText("已关注");
                    mLike.setBackgroundResource(R.drawable.like_back2);
                }
                FrameLayout.LayoutParams ll_lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ScreenUtils.dp2px(PcVideoPlayActivity.this, 250));
                mVideoView.setLayoutParams(ll_lp);
                mBack.setImageResource(R.drawable.btn_back_yuan);
                mDanmu.setVisibility(View.GONE);
                mCross.setVisibility(View.VISIBLE);
                mVideoView.setDisplayOrientation(0); // 旋转-90度
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                chatListView.setVisibility(View.VISIBLE);
                mLl_head.setVisibility(View.VISIBLE);
                isDm = false;
                mLl_title.setBackgroundColor(getResources().getColor(R.color.transparent));
                mLl_title2.setVisibility(View.GONE);
                mBottomIcom.setVisibility(View.GONE);
                mNum.setVisibility(View.VISIBLE);
                mLl_cross.setVisibility(View.VISIBLE);
                mLl_Bottom.setBackgroundColor(getResources().getColor(R.color.white));
//                mShare1.setVisibility(View.VISIBLE);
                btnGift.setImageResource(R.drawable.btn_flower_huang);
                mShare1.setImageResource(R.drawable.btn_share_lan);
//                btnSend.setBackgroundResource(R.drawable.btn_liaotian);
                btnSend.setVisibility(View.VISIBLE);
            }
        } else if (v.equals(mZhubo)) {
            mRl_zhubo.setVisibility(View.VISIBLE);
            mZhubo_line.setVisibility(View.VISIBLE);
            mLiaotian_line.setVisibility(View.INVISIBLE);
        } else if (v.equals(mLiaotian)) {
            mRl_zhubo.setVisibility(View.GONE);
            mZhubo_line.setVisibility(View.INVISIBLE);
            mLiaotian_line.setVisibility(View.VISIBLE);
        } else if (v.equals(mZanting1)) {
            if (isStart) {
                mVideoView.pause();
                mZanting1.setImageResource(R.drawable.btn_play_yuan);
                mZanting2.setImageResource(R.drawable.btn_bofang);
                isStart = false;
            } else {
                mVideoView.start();
                mZanting1.setImageResource(R.drawable.btn_pause_yuan);
                mZanting2.setImageResource(R.drawable.btn_zanting);
                isStart = true;
            }
        } else if (v.equals(mShuaxin)) {
            startLiveShow(url);
            mZanting1.setImageResource(R.drawable.btn_pause_yuan);
            mZanting2.setImageResource(R.drawable.btn_zanting);
        } else if (v.equals(mLike) || v.equals(mLike2)) {
            if (getDialog()) {
                return;
            }
            //关注/取消关注
            Map map = new HashMap();
            map.put("token", rUserInfo.getToken() + "");
            map.put("expertId", mZhuBoInfo.getUserId());
            map.put("status", (focusState == 0 ? 1 : 0) + "");
            request.doPostRequest(2, true, Constant.GetFocus, map);
        } else if (v.equals(mShare1) || v.equals(mShare2)) {

            if (v.equals(mShare1) && !isPortrait) {
                LogUtils.d("popWnd");
                popWnd.showAtLocation(btnSend, Gravity.BOTTOM, ScreenUtils.dp2px(PcVideoPlayActivity.this, 215), ScreenUtils.dp2px(PcVideoPlayActivity.this, 48));
            } else {
                if (!shareUrl.startsWith("http://")) {
                    shareUrl = "http://" + shareUrl;
                }
                UMWeb web = new UMWeb(shareUrl);
                web.setTitle(shareTitle);//标题
//                UMImage image = new UMImage(PcVideoPlayActivity.this, imageUrl);//资源文件
//                web.setThumb(image);  //缩略图
                web.setDescription(shareContent);//描述
                new ShareAction(PcVideoPlayActivity.this)
                        .withMedia(web)
                        .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener).open();
            }
        } else if (v.getId() == R.id.shang) {
            isDm = true;
            mDanmu.setGravity(DanmuContainerView.GRAVITY_TOP);
            mShare1.setImageResource(R.drawable.btn_danmu_shang);
            popWnd.dismiss();
        } else if (v.getId() == R.id.xia) {
            isDm = true;
            mDanmu.setGravity(DanmuContainerView.GRAVITY_BOTTOM);
            mShare1.setImageResource(R.drawable.btn_danmu_xia);
            popWnd.dismiss();
        } else if (v.getId() == R.id.quan) {
            isDm = true;
            mDanmu.setGravity(DanmuContainerView.GRAVITY_FULL);
            mShare1.setImageResource(R.drawable.btn_danmu_quan);
            popWnd.dismiss();
        } else if (v.getId() == R.id.guan) {
            isDm = false;
            mShare1.setImageResource(R.drawable.btn_danmu_guanbi);
            popWnd.dismiss();
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
                LogUtils.d(content);
                break;
            }
            case LiveKit.MESSAGE_SEND_ERROR: {
                if (msg.arg1 == 23408) {
                    final myAlertDialog myAlertDialog = new myAlertDialog(PcVideoPlayActivity.this);
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
        try {
            if (msg.what != LiveKit.MESSAGE_SEND_ERROR && isDm) {
                MessageContent content = (MessageContent) msg.obj;
                DanmuEntity danmuEntity = new DanmuEntity();
                if (content.getClass().getName().equals(TextMessage.class.getName())) {
                    danmuEntity.setContent(((TextMessage) content).getContent());
                    danmuEntity.setName((content.getUserInfo().getName()));
                    danmuEntity.setType(0);
                } else if (content.getClass().getName().equals(GiftMessage.class.getName())) {
                    danmuEntity.setContent(((GiftMessage) content).getContent());
                    danmuEntity.setName((content.getUserInfo().getName()));
                    danmuEntity.setType(1);
                }
                mDanmu.addDanmu(danmuEntity);
            }
        } catch (Exception e) {
            io.vov.vitamio.utils.Log.i("", e);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        LiveKit.quitChatRoom(new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LiveKit.removeEventHandler(handler);
                LiveKit.logout();
                Toast.makeText(PcVideoPlayActivity.this, "退出聊天室成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LiveKit.removeEventHandler(handler);
                LiveKit.logout();
                Toast.makeText(PcVideoPlayActivity.this, "退出聊天室失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
        if (handler2 != null && runnable != null) {
            handler2.removeCallbacks(runnable);
        }
        mVideoView.stopPlayback();
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
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);

            Toast.makeText(PcVideoPlayActivity.this, platform + " 分享成功!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(PcVideoPlayActivity.this, platform + " 分享失败!", Toast.LENGTH_SHORT).show();
            if (t != null) {
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(PcVideoPlayActivity.this, platform + " 分享取消!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            String s = inputEt.getText().toString();
            if (!TextUtils.isEmpty(s)) {
                if (getDialog()) {
                    return false;
                }
                final TextMessage content = TextMessage.obtain(s);
                LiveKit.sendMessage(content);
                inputEt.getText().clear();
            }
        }
        return false;
    }

    @Override
    public void loadDataFinish(int code, Object data) {
        if (code == 0) {
            mZhuBoInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mZhuBoInfo);
            if (mZhuBoInfo != null) {
                GlideImgManager.glideLoader(PcVideoPlayActivity.this, mZhuBoInfo.getUserImage(), R.drawable.rc_image_error, R.drawable.rc_image_error, mhead, 0);
                mName.setText(mZhuBoInfo.getNickName());
                mDesc.setText(mZhuBoInfo.getLiveIntroduce());

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
                mLike2.setImageResource(R.drawable.btn_guanzhu_h);
            } else if (focusState == 0) {
                focusState = 1;
                mLike.setText("已关注");
                mLike.setBackgroundResource(R.drawable.like_back2);
                mLike2.setImageResource(R.drawable.btn_guanzhu_n);
            }
        } else if (code == 3) {
            GiftMessage msg = new GiftMessage("2", "");
            LiveKit.sendMessage(msg);
        } else if (code == 6) {//禁言
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                String status = mUserInfo.getStatus();
                if (status.equals("1")) {
                    //禁言
                    FakeServer.remove(Constant.NotTalk, chatListAdapter.getOpolUserId(), roomId + "", Constant.NotTalkTime, new HttpUtil.OnResponse() {
                        @Override
                        public void onResponse(int code, String body) {
                            LogUtils.d(code + body);
                            if (code == 200) {
                                showToast("禁言成功！");
                            } else {
                                showToast("禁言失败！");
                            }
                        }
                    });
                } else {
                    showToast("无操作权限！");
                }
            }
        } else if (code == 7) {//封禁
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                String status = mUserInfo.getStatus();
                if (status.equals("1")) {
                    FakeServer.remove(Constant.Remove, chatListAdapter.getOpolUserId(), roomId + "", Constant.RemoveTime, new HttpUtil.OnResponse() {
                        @Override
                        public void onResponse(int code, String body) {
                            LogUtils.d(code + body);
                            if (code == 200) {
                                showToast("踢出成功！");
                            } else {
                                showToast("踢出失败！");
                            }
                        }
                    });
                } else {
                    showToast("无操作权限！");
                }
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(PcVideoPlayActivity.this, message, Toast.LENGTH_SHORT).show();
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
            myAlertDialog = new myAlertDialog(PcVideoPlayActivity.this);
            myAlertDialog.setContenttext("您还未登录，请先登录！");
            myAlertDialog.setNoOnclickListener("取消", new myAlertDialog.onNoOnclickListener() {
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

    private class EffectInVisiableHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MOBILE_QUERY:
                    setViewVisible(View.INVISIBLE);
                    LogUtils.d("隐藏");
                    break;

            }
        }
    }

    /**
     * 设置隐藏或显示
     *
     * @param v
     */
    private void setViewVisible(int v) {
        mLl_title.setVisibility(v);
        if (!isPortrait) {
            mLl_Bottom.setVisibility(v);
        } else {
            mNum.setVisibility(v);
            mLl_cross.setVisibility(v);
        }
        if (v == View.INVISIBLE) {
            if (popWnd != null)
                popWnd.dismiss();
        }
    }

    public void resetTime() {
        mtimeHandler.removeMessages(MOBILE_QUERY);
        Message msg = mtimeHandler.obtainMessage(MOBILE_QUERY);
        mtimeHandler.sendMessageDelayed(msg, 5000);
    }
}
