package com.mer.live.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.mer.live.R;
import com.mer.live.base.LiveKit;
import com.mer.live.controller.ChatListAdapter;
import com.mer.live.ui.animation.HeartLayout;
import com.mer.live.ui.fragment.BottomPanelFragment;
import com.mer.live.ui.message.GiftMessage;
import com.mer.live.ui.widget.ChatListView;
import com.mer.live.ui.widget.InputPanel;
import com.mer.live.ui.widget.myDialog;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;

import java.net.URISyntaxException;
import java.util.Random;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * 主播界面
 */
public class LiveShowActivity extends FragmentActivity implements View.OnClickListener, Handler.Callback, StreamingStateChangedListener {

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
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_liveshow);
        LiveKit.addEventHandler(handler);
        initView();
        startLiveShow();
    }

    private void initView() {
        background = (ViewGroup) findViewById(R.id.background);
        chatListView = (ChatListView) findViewById(R.id.chat_listview);
        bottomPanel = (BottomPanelFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        btnGift = (ImageView) bottomPanel.getView().findViewById(R.id.btn_gift);
        btnHeart = (ImageView) bottomPanel.getView().findViewById(R.id.btn_heart);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);

        btnGift.setVisibility(View.GONE);

        chatListAdapter = new ChatListAdapter();
        chatListView.setAdapter(chatListAdapter);
        background.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnHeart.setOnClickListener(this);
        bottomPanel.setInputPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                final TextMessage content = TextMessage.obtain(text);
                LiveKit.sendMessage(content);
            }
        });

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final myDialog myDialog = new myDialog(LiveShowActivity.this);

                myDialog.setContenttext(chatListAdapter.getuserName(i));
                myDialog.setYesOnclickListener("禁言", new myDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        myDialog.dismiss();
//                        http://api.cn.ronghub.com/chatroom/user/gag/add.[format]
                    }
                });
                myDialog.setNoOnclickListener("踢出频道", new myDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        myDialog.dismiss();
                    }
                });
                myDialog.show();
            }
        });

    }

    private void startLiveShow() {
        roomId = "ChatRoom01";
        joinChatRoom(roomId);

        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        int meiyan = sharedPreferences.getInt("meiyan",0);
        int shexiangtou = sharedPreferences.getInt("shexiangtou",0);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        // Decide FULL screen or real size
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
//        String streamJsonStrFromServer = getIntent().getStringExtra("stream_json_str");
        try {
//            mJSONObject = new JSONObject(streamJsonStrFromServer);
//            StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
            mProfile = new StreamingProfile();
            mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
//                    .setStream(stream);  // You can invoke this before startStreaming, but not in initialization phase.
            mProfile.setPublishUrl("rtmp://pili-publish.test.mfc.com.cn/cz-test/test001");

            CameraStreamingSetting setting = new CameraStreamingSetting();

            if (meiyan == 1){
                setting.setBuiltInFaceBeautyEnabled(true);
            }else {
                setting.setBuiltInFaceBeautyEnabled(false);
            }

            if (shexiangtou ==1){
                setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
            }else {
                setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }

            setting.setContinuousFocusModeEnabled(true)
                    .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                    .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
            mMediaStreamingManager = new MediaStreamingManager(this, afl, glSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
            mMediaStreamingManager.prepare(setting, mProfile);
            mMediaStreamingManager.setStreamingStateListener(this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
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
                final InformationNotificationMessage content = InformationNotificationMessage.obtain("来啦");
                LiveKit.sendMessage(content);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Toast.makeText(LiveShowActivity.this, "聊天室加入失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!bottomPanel.onBackAction()) {
            finish();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(background)) {
            bottomPanel.onBackAction();
        } else if (v.equals(btnGift)) {
            GiftMessage msg = new GiftMessage("2", "送您一个礼物");
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
        super.onDestroy();
    }
}
