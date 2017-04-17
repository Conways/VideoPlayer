package com.conways.videoplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Conways on 2017/4/6.
 */

public class XPlayer extends SurfaceView implements XPlayerControl {

    private int currentPosition = 0;
    private MediaPlayer mediaPlayer;
    private XplayerStateChangeLisenter xplayerStateCallLisenter;

    public XPlayer(Context context) {
        super(context);
        init();
    }

    public XPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(callback);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(preparedListener);
        mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }


    @Override
    public void start(String path) throws IOException {
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
    }

    @Override
    public void pause() {
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    @Override
    public void stop() {
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
    }

    @Override
    public void restart() throws IOException {
        mediaPlayer.prepare();
        mediaPlayer.seekTo(currentPosition);
    }

    @Override
    public void destroy() {
        currentPosition = -1;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void setXplayerStateCallLisenter(XplayerStateChangeLisenter xplayerStateCallLisenter) {
        this.xplayerStateCallLisenter = xplayerStateCallLisenter;
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
        }
    };


    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            xplayerStateCallLisenter.onBuffUpdate(percent);
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            xplayerStateCallLisenter.onComplete();
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return xplayerStateCallLisenter.onError(what,extra);
        }
    };

    private MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {

        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            xplayerStateCallLisenter.onSizeChanged(width,height);
        }
    };

}
