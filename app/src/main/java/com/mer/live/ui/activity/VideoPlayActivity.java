package com.mer.live.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mer.live.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.CenterLayout;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 播放手机录制视频，支持多种格式
 *
 */
public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private VideoView mVideoView;
    private MediaController mMediaController;
    private TextView huanchong;
    private TextView wangsu;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    private String sdPath = "";
    private boolean isPortrait = true;
    private FrameLayout mContent;
    private ImageView mCross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);

        initView();
    }


    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.videoplay_vitamio);
        huanchong = (TextView) findViewById(R.id.videoplay_huanchong);
        wangsu = (TextView) findViewById(R.id.videoplay_wangsu);
        mMediaController = (MediaController) findViewById(R.id.videoplay_mMediaController);
        mContent = (FrameLayout) findViewById(R.id.videoplay_content);
        mCross = (ImageView) findViewById(R.id.videoplay_cross);

        findViewById(R.id.videoplay_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        initVidio();
    }


    /**
     * 设置vitamio各项参数
     * 设置视频地址并播放
     */
    private void initVidio() {
        mVideoView.setVideoURI(Uri.parse("http://ooj767wvo.bkt.clouddn.com/20170502%E6%9C%9F%E5%B8%82%E6%94%B6%E8%AF%84%EF%BC%88%E7%9B%B4%E6%92%AD%EF%BC%89.flv"));
        //实例化控制器
        mMediaController.setAnchorView(mVideoView);
        mMediaController.setMediaPlayer(mVideoView);
//        mMediaController.show(5000);//控制器显示5s后自动隐藏
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
                }
            };
        }
        mVideoView.setOnPreparedListener(onPreparedListener);
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
        huanchong.setText(percent + "%");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    huanchong.setText("");
                    wangsu.setText("");
                    huanchong.setVisibility(View.VISIBLE);
                    wangsu.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                huanchong.setVisibility(View.GONE);
                wangsu.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                wangsu.setText("" + extra + "kb/s" + "  ");
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
        mCross.setImageResource(R.drawable.big);
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
        mCross.setImageResource(R.drawable.small);
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

}
