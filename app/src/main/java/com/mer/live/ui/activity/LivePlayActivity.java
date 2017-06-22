package com.mer.live.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
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
public class LivePlayActivity extends FragmentActivity implements View.OnClickListener, Handler.Callback,
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play);
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

    }

    private void startLiveShow() {
        roomId = "ChatRoom01";
        joinChatRoom(roomId);
         mVideoView =  (PLVideoTextureView) findViewById(R.id.PLVideoTextureView);
//        mVideoView = (PLVideoTextureView) findViewById(R.id.PLVideoTextureView);
//        View loadingView = findViewById(R.id.LoadingView);
//        mVideoView.setBufferingIndicator(loadingView);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);//设置画面预览模式
        mVideoView.setDisplayOrientation(0); // 旋转90度
//        mVideoView.setMirror(true);//设置播放画面镜像变换
        mVideoView.setVideoPath("rtmp://pili-live-rtmp.test.mfc.com.cn/cz-test/test001");

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
                Toast.makeText(LivePlayActivity.this, "聊天室加入失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(LivePlayActivity.this, "退出聊天室成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LiveKit.removeEventHandler(handler);
                LiveKit.logout();
                Toast.makeText(LivePlayActivity.this, "退出聊天室失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
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
