package com.mer.live.ui.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.mer.live.R;
import com.mer.live.base.LiveKit;
import com.mer.live.controller.ChatListAdapter;
import com.mer.live.ui.animation.HeartLayout;
import com.mer.live.ui.fragment.BottomPanelFragment;
import com.mer.live.ui.message.GiftMessage;
import com.mer.live.ui.widget.ChatListView;
import com.mer.live.ui.widget.InputPanel;
import com.mer.live.utils.DanmuAdapter;
import com.mer.live.utils.DanmuEntity;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import java.util.Random;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * 直播播放端界面
 */
public class PcVideoPlayActivity extends FragmentActivity implements View.OnClickListener, Handler.Callback,
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnVideoSizeChangedListener,
        PLMediaPlayer.OnErrorListener {
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
    private PLVideoTextureView mVideoView;
    private ImageView mCross;
    private ImageView mBack;
    private TextView mTitle;

    private boolean isPortrait = true;
    private DanmuContainerView mDanmu;
    private boolean isDm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_video_play);
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
        mDanmu = (DanmuContainerView) findViewById(R.id.danmuContainerView);

        mCross = (ImageView) findViewById(R.id.pc_videoplay_cross);
        mBack = (ImageView) findViewById(R.id.btn_back);
        mTitle = (TextView) findViewById(R.id.text_title);

        mBack.setOnClickListener(this);
        mCross.setOnClickListener(this);

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
        setDanmu();
    }

    private void startLiveShow() {
        roomId = "ChatRoom01";
        joinChatRoom(roomId);
        mVideoView = (PLVideoTextureView) findViewById(R.id.PLVideoTextureView);
//        View loadingView = findViewById(R.id.LoadingView);
//        mVideoView.setBufferingIndicator(loadingView);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);//设置画面预览模式
        mVideoView.setDisplayOrientation(0); // 旋转90度
//        mVideoView.setMirror(true);//设置播放画面镜像变换
        mVideoView.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
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
                final InformationNotificationMessage content = InformationNotificationMessage.obtain("来啦");
                LiveKit.sendMessage(content);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Toast.makeText(PcVideoPlayActivity.this, "聊天室加入失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
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
        } else if (v.equals(mBack)) {
            finish();
        } else if (v.equals(mCross)) {
            Log.d("mCross", isPortrait + "");
            if (isPortrait) {//设置横屏
                isPortrait = false;
                mCross.setImageResource(R.drawable.small);
                mVideoView.setDisplayOrientation(-90); // 旋转90度
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                chatListView.setVisibility(View.GONE);
                isDm = true;
                Log.d("mCross", "SCREEN_ORIENTATION_LANDSCAPE");
            } else {
                Log.d("mCross", "SCREEN_ORIENTATION_PORTRAIT");
                isPortrait = true;
                mCross.setImageResource(R.drawable.big);
                mVideoView.setDisplayOrientation(0); // 旋转-90度
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                chatListView.setVisibility(View.VISIBLE);
                isDm = false;
            }
        }
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        LogUtils.d(msg.obj);
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
            if (msg.what!=LiveKit.MESSAGE_SEND_ERROR&&isDm) {
                MessageContent content = (MessageContent) msg.obj;
                DanmuEntity danmuEntity = new DanmuEntity();
                if (content.getClass().getName().equals(TextMessage.class.getName())){
                    danmuEntity.setContent((content.getUserInfo().getName())+":"+((TextMessage) content).getContent());
                }else if (content.getClass().getName().equals(GiftMessage.class.getName())){
                    danmuEntity.setContent((content.getUserInfo().getName())+":"+((GiftMessage) content).getContent());
                }
                danmuEntity.setType(1);
                mDanmu.addDanmu(danmuEntity);
            }
        } catch (Exception e) {
            com.mer.live.vitamio.utils.Log.i("",e);
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

}
