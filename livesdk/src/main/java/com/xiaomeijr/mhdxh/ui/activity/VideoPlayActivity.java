package com.xiaomeijr.mhdxh.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.base.BaseActivity;
import com.xiaomeijr.mhdxh.ui.widget.DialogProgress;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.CenterLayout;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 播放手机录制视频，支持多种格式
 */
public class VideoPlayActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private VideoView mVideoView;
    private MediaController mMediaController;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    private String sdPath = "";
    private boolean isPortrait = true;
    private FrameLayout mContent;
    private ImageView mCross;
    private String url;
    private String name;
    private TextView mTitle;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        Vitamio.isInitialized(getApplicationContext());

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
//                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        return R.layout.activity_video_play;
    }

    @Override
    protected void initUI() {
        mVideoView = (VideoView) findViewById(R.id.videoplay_vitamio);
        mTitle = (TextView) findViewById(R.id.title);
        mMediaController = (MediaController) findViewById(R.id.videoplay_mMediaController);
        mContent = (FrameLayout) findViewById(R.id.videoplay_content);
        mCross = (ImageView) findViewById(R.id.videoplay_cross);

        dialog = DialogProgress.createLoadingDialog(this,"正在缓冲...");
        dialog.show();
        findViewById(R.id.videoplay_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPortrait){
                    setPortraitScape();
                }else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isPortrait){
            setPortraitScape();
        }else {
            finish();
        }
    }

    @Override
    protected void initData() {
        mCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPortrait) {
                    setLandScape();
                } else {
                    setPortraitScape();
                }
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("url"))
            url = intent.getStringExtra("url");
        if (intent.hasExtra("name")){
            name = intent.getStringExtra("name");
            mTitle.setText(name.toString());
        }

        initVidio();
    }

    /**
     * 设置vitamio各项参数
     * 设置视频地址并播放
     */
    private void initVidio() {
//        mVideoView.setVideoURI(Uri.parse("http://ooj767wvo.bkt.clouddn.com/20170502%E6%9C%9F%E5%B8%82%E6%94%B6%E8%AF%84%EF%BC%88%E7%9B%B4%E6%92%AD%EF%BC%89.flv"));
        mVideoView.setVideoURI(Uri.parse(url));
        //实例化控制器
        mMediaController.setAnchorView(mVideoView);
        mMediaController.setMediaPlayer(mVideoView);
        mMediaController.show(5000);//控制器显示5s后自动隐藏
        mVideoView.setMediaController(mMediaController);//绑定控制器
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);//设置播放画质 高画质
        mVideoView.requestFocus();//取得焦点
        mVideoView.setOnInfoListener(this);//网速
        mVideoView.setOnBufferingUpdateListener(this);//缓冲
        if (onPreparedListener == null) {
            onPreparedListener = new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
//                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                    mVideoView.start();
                    dialog.dismiss();
                }
            };
        }
        mVideoView.setOnPreparedListener(onPreparedListener);
        mMediaController.setOnHiddenListener(new MediaController.OnHiddenListener() {
            @Override
            public void onHidden() {
                full(true);
            }
        });
        mMediaController.setOnShownListener(new MediaController.OnShownListener() {
            @Override
            public void onShown() {
                full(false);
            }
        });
//        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                return true;
//            }
//        });
//        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                initVidio();
//            }
//        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                break;
        }
        return true;
    }

    /**
     * 设置横屏
     */
    private void setLandScape() {
        FrameLayout.LayoutParams ll_lp = new FrameLayout.LayoutParams(
                getHeightPixel(VideoPlayActivity.this),
                getWidthPixel(VideoPlayActivity.this)
        );
        CenterLayout.LayoutParams fl_lp = new CenterLayout.LayoutParams(
                getHeightPixel(VideoPlayActivity.this),
                getWidthPixel(VideoPlayActivity.this)
        );
        mContent.setLayoutParams(ll_lp);
        mVideoView.setLayoutParams(fl_lp);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        isPortrait = false;
        mCross.setImageResource(R.drawable.btn_suoxiao);
    }

    /**
     * 设置竖屏
     */
    private void setPortraitScape() {
        FrameLayout.LayoutParams ll_lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        CenterLayout.LayoutParams fl_lp = new CenterLayout.LayoutParams(
                CenterLayout.LayoutParams.MATCH_PARENT, CenterLayout.LayoutParams.MATCH_PARENT);
        mContent.setLayoutParams(ll_lp);
        mVideoView.setLayoutParams(fl_lp);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        isPortrait = true;
        mCross.setImageResource(R.drawable.btn_fangda);
    }

    public int getHeightPixel(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }

    public int getWidthPixel(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    /**
     * 设置隐藏状态栏
     * @param enable
     */
    private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
