package com.xiaomeijr.mhdxh.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.apkfuns.logutils.LogUtils;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;
import com.xiaomeijr.mhdxh.base.LiveKit;
import com.xiaomeijr.mhdxh.controller.ChatListAdapter;
import com.xiaomeijr.mhdxh.data.RUserInfo;
import com.xiaomeijr.mhdxh.fakeserver.FakeServer;
import com.xiaomeijr.mhdxh.fakeserver.HttpUtil;
import com.xiaomeijr.mhdxh.model.NetWorkRequest;
import com.xiaomeijr.mhdxh.model.UIDataListener;
import com.xiaomeijr.mhdxh.ui.animation.HeartLayout;
import com.xiaomeijr.mhdxh.ui.fragment.BottomPanelFragment;
import com.xiaomeijr.mhdxh.ui.message.GiftMessage;
import com.xiaomeijr.mhdxh.ui.widget.ChatListView;
import com.xiaomeijr.mhdxh.ui.widget.DialogProgress;
import com.xiaomeijr.mhdxh.ui.widget.InputPanel;
import com.xiaomeijr.mhdxh.ui.widget.myAlertDialog;
import com.xiaomeijr.mhdxh.ui.widget.myDialog;
import com.xiaomeijr.mhdxh.utils.ACache;
import com.xiaomeijr.mhdxh.utils.Constant;
import com.xiaomeijr.mhdxh.utils.DanmuEntity;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * 主播界面
 */
public class LiveShowActivity extends BaseActivity implements View.OnClickListener, Handler.Callback, StreamingStateChangedListener, UIDataListener {

    private ViewGroup background;
    private ChatListView chatListView;
    private BottomPanelFragment bottomPanel;
    private ImageView btnGift;
    private ImageView btnHeart;
    private HeartLayout heartLayout;

    private Random random = new Random();
    private Handler handler = new Handler(this);
    private ChatListAdapter chatListAdapter;
    private String roomId;
    private StreamingProfile mProfile;
    private MediaStreamingManager mMediaStreamingManager;
    private ImageView mBack;
    private ImageView mShanguang;
    private ImageView mShexiangtou;
    private ImageView mZimu;
    private TextView mNum;
    private CameraStreamingSetting setting;
    private int shanguang = 0;
    private int meiyan = 0;
    private ImageView mMeiyan;
    private AspectFrameLayout afl;
    private GLSurfaceView glSurfaceView;
    private EditText inputEt;
    private Dialog dialog;
    private NetWorkRequest request;
    private ACache mAcache;
    private RUserInfo rUserInfo;
    private Handler handler2 = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {//刷新人数
            getNum();
//            LogUtils.d("获取人数");
            handler2.postDelayed(this, 2000);
        }
    };
//    private DanmuContainerView mDanmu;
    private boolean isDm = true;

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
        return R.layout.activity_liveshow;
    }

    @Override
    protected void initUI() {
        afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);

        background = (ViewGroup) findViewById(R.id.background);
        chatListView = (ChatListView) findViewById(R.id.chat_listview);
        bottomPanel = (BottomPanelFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        btnGift = (ImageView) bottomPanel.getView().findViewById(R.id.btn_gift);
        btnHeart = (ImageView) bottomPanel.getView().findViewById(R.id.btn_heart);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        inputEt = (EditText) bottomPanel.getView().findViewById(R.id.input_editor);

//        mDanmu = (DanmuContainerView) findViewById(R.id.danmuContainerView);

        mBack = (ImageView) findViewById(R.id.btn_back);
        mShanguang = (ImageView) findViewById(R.id.btn_shanguang);
        mShexiangtou = (ImageView) findViewById(R.id.btn_shexiangtou);
        mZimu = (ImageView) findViewById(R.id.btn_zimu);
        mMeiyan = (ImageView) findViewById(R.id.btn_meiyan);
        mNum = (TextView) findViewById(R.id.text_num);

        btnGift.setVisibility(View.GONE);


        background.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnHeart.setOnClickListener(this);

        mBack.setOnClickListener(this);
        mShanguang.setOnClickListener(this);
        mShexiangtou.setOnClickListener(this);
        mZimu.setOnClickListener(this);
        mMeiyan.setOnClickListener(this);


        dialog = DialogProgress.createLoadingDialog(LiveShowActivity.this, "", this);
        request = new NetWorkRequest(LiveShowActivity.this, this);
        mAcache = ACache.get(LiveShowActivity.this);
        rUserInfo = (RUserInfo) mAcache.getAsObject("UserInfo");

        //获取推流地址
        Map map = new HashMap();
        map.put("token", rUserInfo.getToken());
        map.put("userId", rUserInfo.getUserId());
        request.doPostRequest(0, true, Constant.GetShowUrl, map);

        Map map2 = new HashMap();
        map2.put("token", rUserInfo.getToken());
        request.doPostRequest(2, true, Constant.GetUserInfo, map);

    }


    @Override
    protected void initData() {
//        setDanmu();
        LiveKit.addEventHandler(handler);
        setVedioConfig();

        chatListAdapter = new ChatListAdapter(LiveShowActivity.this,rUserInfo,request);
        chatListView.setAdapter(chatListAdapter);
        bottomPanel.setInputPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                final TextMessage content = TextMessage.obtain(text);
                LiveKit.sendMessage(content);
                inputEt.getText().clear();
            }
        });

//        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
//                final myDialog myDialog = new myDialog(LiveShowActivity.this);
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
        handler2.postDelayed(runnable, 2000);
    }

    private void setVedioConfig() {
        setting = new CameraStreamingSetting();
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        mMediaStreamingManager = new MediaStreamingManager(this, afl, glSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mMediaStreamingManager.setStreamingStateListener(this);
        mProfile = new StreamingProfile();
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
        setting.setContinuousFocusModeEnabled(true)
                .setBuiltInFaceBeautyEnabled(true)
                // FaceBeautySetting 中的参数依次为：beautyLevel，whiten，redden，即磨皮程度、美白程度以及红润程度，取值范围为[0.0f, 1.0f]
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0f, 0f, 0f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)

                .setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
        mMediaStreamingManager.prepare(setting, mProfile);
    }

    private void startLiveShow(String url) {
        try {
//            mProfile.setPublishUrl("rtmp://pili-publish.test.mfc.com.cn/cz-test/test001");
            mProfile.setPublishUrl(url);
            mMediaStreamingManager.setStreamingProfile(mProfile);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

//    private void setDanmu() {
//        DanmuAdapter danmuAdapter = new DanmuAdapter(LiveShowActivity.this);
//        mDanmu.setAdapter(danmuAdapter);
//
//        mDanmu.setSpeed(DanmuContainerView.HIGH_SPEED);
//
//        mDanmu.setGravity(DanmuContainerView.GRAVITY_FULL);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mMediaStreamingManager.resume();
        } catch (Exception e) {
            LogUtils.e(e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // You must invoke pause here.
        mMediaStreamingManager.pause();
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case PREPARING:
                break;
            case READY:
                // start streaming when READY
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaStreamingManager != null) {
                            mMediaStreamingManager.startStreaming();
                        }
                    }
                }).start();
                break;
            case CONNECTING:
                break;
            case STREAMING:
                // The av packet had been sent.
                break;
            case SHUTDOWN:
                // The streaming had been finished.
                break;
            case IOERROR:
                // Network connect error.
                break;
            case OPEN_CAMERA_FAIL:
                // Failed to open camera.
                break;
            case DISCONNECTED:
                // The socket is broken while streaming
                break;
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
                Toast.makeText(LiveShowActivity.this, "聊天室加入失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });

    }

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

    @Override
    public void onBackPressed() {
        if (!bottomPanel.onBackAction()) {
            final myAlertDialog myAlertDialog = new myAlertDialog(LiveShowActivity.this);
            myAlertDialog .setContenttext("退出直播间?");
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
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(background)) {
            bottomPanel.onBackAction();
        } else if (v.equals(btnGift)) {
            GiftMessage msg = new GiftMessage("2", "赠送给主播");
            LiveKit.sendMessage(msg);
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
        } else if (v.equals(mBack)) {
            onBackPressed();
        } else if (v.equals(mShanguang)) {
            if (shanguang == 0) {
                shanguang = 1;
                mMediaStreamingManager.turnLightOn();
                mShanguang.setImageResource(R.drawable.btn_shanguangdeng_n);
            } else {
                shanguang = 0;
                mMediaStreamingManager.turnLightOff();
                mShanguang.setImageResource(R.drawable.btn_shanguang_h);
            }

        } else if (v.equals(mShexiangtou)) {
            mMediaStreamingManager.switchCamera();
        } else if (v.equals(mZimu)) {
            if (isDm) {
                isDm = false;
                mZimu.setImageResource(R.drawable.btn_danmu_h);
                chatListView.setVisibility(View.INVISIBLE);
            } else {
                isDm = true;
                mZimu.setImageResource(R.drawable.btn_danmu_n);
                chatListView.setVisibility(View.VISIBLE);
            }
        } else if (v.equals(mMeiyan)) {
            if (meiyan == 0) {
                setting.setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                        .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
                meiyan = 1;
                mMediaStreamingManager.updateFaceBeautySetting(setting.getFaceBeautySetting());
                mMeiyan.setImageResource(R.drawable.btn_meiyan_n);
                Toast.makeText(LiveShowActivity.this, "开启美颜！", Toast.LENGTH_SHORT).show();
            } else {
                setting.setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0f, 0f, 0f))
                        .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
                meiyan = 0;
                mMediaStreamingManager.updateFaceBeautySetting(setting.getFaceBeautySetting());
                mMeiyan.setImageResource(R.drawable.btn_meiyan_h);
                Toast.makeText(LiveShowActivity.this, "关闭美颜！", Toast.LENGTH_SHORT).show();
            }
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
                    danmuEntity.setContent((content.getUserInfo().getName()) + ":" + ((TextMessage) content).getContent());
                } else if (content.getClass().getName().equals(GiftMessage.class.getName())) {
                    danmuEntity.setContent((content.getUserInfo().getName()) + ":" + ((GiftMessage) content).getContent());
                }
                danmuEntity.setType(1);
//                mDanmu.addDanmu(danmuEntity);
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
                Toast.makeText(LiveShowActivity.this, "退出聊天室成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LiveKit.removeEventHandler(handler);
                LiveKit.logout();
                Toast.makeText(LiveShowActivity.this, "退出聊天室失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
        if (handler2 != null && runnable != null) {
            handler2.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    @Override
    public void loadDataFinish(int code, Object data) {
        if (code == 0) {
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
//            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                startLiveShow(mUserInfo.getLiveAddress());
            }
        } else if (code == 1) {
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
        } else if (code == 2) {
            RUserInfo userInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(userInfo);
            if (userInfo != null) {
                roomId = userInfo.getLoginNo();
                joinChatRoom(roomId);
            }
        }else if (code == 6){//禁言
            RUserInfo mUserInfo = JSON.parseObject(((JSONObject) data).toJSONString(), new TypeReference<RUserInfo>() {
            });
            LogUtils.d(mUserInfo);
            if (mUserInfo != null) {
                String status = mUserInfo.getStatus();
                if (status.equals("1")){
                    //禁言
                    FakeServer.remove(Constant.NotTalk,chatListAdapter.getOpolUserId() , roomId + "", Constant.NotTalkTime, new HttpUtil.OnResponse() {
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
        Toast.makeText(LiveShowActivity.this, message, Toast.LENGTH_SHORT).show();
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
}
