package com.conways.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

/**
 * Created by Conways on 2017/4/6.
 */

public class TPlayer extends SurfaceView implements MediaController.MediaPlayerControl {
    private static final int CMD_STOP_FASTPLAY = 199;
    private static String TAG = "VideoPlayer";
    private boolean pause;
    private boolean seekBackward;
    private boolean seekForward;
    private Uri videoUri;
    public MediaPlayer mMediaPlayer;
    private Context mContext;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private int mVideoHeight;
    private int mVideoWidth;
    private MediaController mediaController;
    protected SurfaceHolder surfaceHolder;
    private int mSeekWhenPrepared = 0;
    private boolean mIsPrepared = false;
    private boolean mStartWhenPrepared = false;
    private int mCurrentBufferPercentage = 0;

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                   int h) {
            if ((mMediaPlayer != null) && (mIsPrepared)
                    && (mVideoWidth == w)
                    && (mVideoHeight == h) && (mSeekWhenPrepared != 0)) {
                mMediaPlayer.seekTo(mSeekWhenPrepared);
                mSeekWhenPrepared = 0;
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            surfaceHolder = holder;

            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(surfaceHolder);
                resume();
            } else {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceHolder = null;
            if (mediaController != null) {
                mediaController.hide();
            }
            release(true);
        }
    };

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //重新播放

    public void resume() {
        if (surfaceHolder == null) {
            return;
        }
        if (mMediaPlayer != null) {
            return;
        }
        openVideo();
    }

    public TPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        this.initVideoView();
    }

    public TPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.initVideoView();
    }

    public TPlayer(Context context) {
        super(context);
        this.mContext = context;
        this.initVideoView();
    }

    @Override
    public boolean canPause() {
        return this.pause;
    }

    @Override
    public boolean canSeekBackward() {
        return this.seekBackward;
    }

    @Override
    public boolean canSeekForward() {
        return this.seekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        int i = 0;
        if (mMediaPlayer != null) {
            if (mCurrentBufferPercentage != 0) {
                i = mCurrentBufferPercentage;
            } else {
                i = 0;
            }
        }
        return i;
    }

    @Override
    public int getCurrentPosition() {
        int i = 0;
        if ((mMediaPlayer != null) && (mIsPrepared)) {
            i = mMediaPlayer.getCurrentPosition();
        }
        return i;
    }

    @Override
    public int getDuration() {
        int mDuration = 0;
        if ((mMediaPlayer != null) && (mIsPrepared)) {
            Log.i(TAG, "VideoPlayer is ready!");
            mDuration = mMediaPlayer.getDuration();
            Log.i(TAG, "mMediaPlayer.getCurrentPosition() = " + mMediaPlayer.getCurrentPosition());
            if (mDuration <= 0) {
                mDuration = -1;
            }
        } else {
            Log.e(TAG, "Can't find videoplayer!");
            mDuration = -1;
        }
        Log.i(TAG, "getDuration mDuration = " + mDuration);
        return mDuration;
    }

    @Override
    public boolean isPlaying() {
        boolean bool = false;
        if ((mMediaPlayer != null) && (mIsPrepared)) {
            bool = mMediaPlayer.isPlaying();
        }
        return bool;
    }

    @Override
    public void pause() {
        if ((mMediaPlayer != null) && (mIsPrepared)
                && (mMediaPlayer.isPlaying())) {
            Log.i(TAG, "VideoPlayer is paused now!");
            mMediaPlayer.pause();
        }
        mStartWhenPrepared = true;
    }

    public void play() {
        if ((mMediaPlayer != null) && (mIsPrepared)
                && (!mMediaPlayer.isPlaying())) {
            mMediaPlayer.start();
            Log.i(TAG, "VideoPlayer is playing now!");
        }
        Parcel localParcel = Parcel.obtain();
        localParcel.writeInterfaceToken("android.media.IMediaPlayer");
        localParcel.writeInt(CMD_STOP_FASTPLAY);
        invoke(localParcel, Parcel.obtain());
    }

    public int invoke(Parcel paramParcel1, Parcel paramParcel2) {
        int i = 0;
        if ((mMediaPlayer != null) && (mIsPrepared)) {
        }
        return i;
    }

    @Override
    public void seekTo(int mSec) {
        if ((mMediaPlayer != null) && (mIsPrepared))
            mMediaPlayer.seekTo(mSec);
        else {
            mSeekWhenPrepared = mSec;
        }
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if ((mMediaPlayer != null) && (mIsPrepared)) {
            mMediaPlayer.start();
            mStartWhenPrepared = false;
        } else {
            mStartWhenPrepared = true;
        }
        return;
    }

    public void setVideoURI(Uri uri) {
        this.videoUri = uri;
        openVideo();
        requestLayout();
        invalidate();
    }

    /**
     * 设置播放路径
     */
    public void setVideoPath(String paramString) {
        setVideoURI(Uri.parse(paramString));
        Log.i(TAG, "VideoPlayer's link ==" + paramString);
    }

    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer paramMediaPlayer) {
            mIsPrepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);

            }
            mVideoWidth = paramMediaPlayer.getVideoWidth();
            mVideoHeight = paramMediaPlayer.getVideoHeight();
            if ((mVideoWidth != 0) && (mVideoHeight != 0)) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                if (!mStartWhenPrepared) ;
                else {
                    mMediaPlayer.start();
                    mStartWhenPrepared = false;
                }
            }
            if (mSeekWhenPrepared != 0) {
                mMediaPlayer.seekTo(mSeekWhenPrepared);
                mSeekWhenPrepared = 0;
            }
            if (!mStartWhenPrepared)
                ;
            else {
                mMediaPlayer.start();
                mStartWhenPrepared = false;
            }
        }
    };


    private void openVideo() {
        if ((videoUri == null) || (surfaceHolder == null))
            ;
        else {
            Intent localIntent1 = new Intent(
                    "com.android.music.musicservicecommand");
            localIntent1.putExtra("command", "pause");
            mContext.sendBroadcast(localIntent1);
            Intent localIntent2 = new Intent("com.android.music.videoOpened");
            localIntent2.putExtra("flag", "true");
            mContext.sendBroadcast(localIntent2);
            if (mMediaPlayer != null) {
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
            }
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(mPreparedListener);
                mMediaPlayer
                        .setOnVideoSizeChangedListener(mSizeChangedListener);
                Log.e(TAG, "reset duration to -1 in openVideo");
                mIsPrepared = false;
                mCurrentBufferPercentage = 0;
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
                mMediaPlayer.setOnErrorListener(mErrorListener);// MediaPlayer.MEDIA_INFO_BUFFER_
                mMediaPlayer
                        .setOnBufferingUpdateListener(mBufferingUpdateListener);
                mMediaPlayer.setOnInfoListener(mInfoListener);
                mMediaPlayer.setDisplay(surfaceHolder);

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.e(TAG, "reset duration to -1 in openVideoAAA");
                mMediaPlayer.setDataSource(mContext, videoUri);
                mMediaPlayer.setScreenOnWhilePlaying(true);
                Log.e(TAG, "reset duration to -1 in openVideoAAA");
                mMediaPlayer.prepareAsync();
                attachMediaController();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mediaController != null) {
            mediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this
                    .getParent() : this;
            mediaController.setAnchorView(anchorView);
            mediaController.setEnabled(true);
        }
    }

    public void setMediaController(MediaController controller) {
        if (mediaController != null) {
            mediaController.hide();
        }
        mediaController = controller;
        attachMediaController();
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void setVideoScale(int paramInt1, int paramInt2) {
        ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
        localLayoutParams.height = paramInt2;
        localLayoutParams.width = paramInt1;
        setLayoutParams(localLayoutParams);
    }


    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(surfaceHolderCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /**
     * 停止播放，并释放播放资源，即mMediaPlayer先stop在release
     */
    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 销毁播放服务
     */
    public void destroyPlayer(int flag) {
        if (surfaceHolder != null && flag == 1) {
            surfaceHolder = null;
            Intent localIntent = new Intent("com.android.music.videoOpened");
            localIntent.putExtra("flag", "false");
            mContext.sendBroadcast(localIntent);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 销毁播放服务
     */
    public void destroyPlayer() {
        if (surfaceHolder != null) {
            surfaceHolder = null;
            Intent localIntent = new Intent("com.android.music.videoOpened");
            localIntent.putExtra("flag", "false");
            mContext.sendBroadcast(localIntent);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            }
            return false;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer paramMediaPlayer, int paramInt) {
            mCurrentBufferPercentage = paramInt;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(paramMediaPlayer, mCurrentBufferPercentage);
        }
    };

    public void setOnBufferingUpdateListener(
            MediaPlayer.OnBufferingUpdateListener paramOnPreparedListener) {
        mOnBufferingUpdateListener = paramOnPreparedListener;
    }

    //设置进度条结束时的回调函数

    public void setOnSeekCompleteListener(
            MediaPlayer.OnSeekCompleteListener paramOnPreparedListener) {
        mOnSeekCompleteListener = paramOnPreparedListener;
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer paramMediaPlayer) {
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1,
                               int paramInt2) {
            Log.e(TAG, "error" + "waht:" + paramInt1 + "sec:" + paramInt2);
            if ((mOnErrorListener != null)
                    && (mOnErrorListener.onError(mMediaPlayer, paramInt1,
                    paramInt2))) {
                return true;
            } else if (getWindowToken() == null) {
                return true;
            }
            return false;
        }
    };
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer paramMediaPlayer,
                                       int paramInt1, int paramInt2) {
        }
    };

    @SuppressWarnings("unused")
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (mOnSeekCompleteListener != null) {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };

    public void setOnCompletionListener(
            MediaPlayer.OnCompletionListener paramOnCompletionListener) {
        mOnCompletionListener = paramOnCompletionListener;
    }

    public void setOnErrorListener(
            MediaPlayer.OnErrorListener paramOnErrorListener) {
        mOnErrorListener = paramOnErrorListener;
    }

    public void setOnInfoListener(
            MediaPlayer.OnInfoListener paramOnPreparedListener) {
        mOnInfoListener = paramOnPreparedListener;
    }
}
