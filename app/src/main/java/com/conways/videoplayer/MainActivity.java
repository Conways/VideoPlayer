package com.conways.videoplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements XplayerStateChangeLisenter {
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private ProgressBar progressBar;
    private XPlayer xPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        progressBar= (ProgressBar) findViewById(R.id.progressBar);
//        String uri = "https://www.iconfinder.com/icons/1105270/brand_connect_shape_square_icon#size=512%22%22%3E";
        String uri = Environment.getExternalStorageDirectory().getPath() + "/ddmsrec.mp4";
        xPlayer = (XPlayer) this.findViewById(R.id.xplayer);
        xPlayer.setXplayerStateCallLisenter(this);
        try {
            xPlayer.start(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        player=new MediaPlayer();
//        try {
//            player.setDataSource(this, Uri.parse(uri));
//            holder=surfaceView.getHolder();
//            holder.addCallback(new MyCallBack());
//            player.prepare();
//            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    player.start();
//                    player.setLooping(true);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onSizeChanged(int width, int height) {

    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "播放结束", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onError(int what, int extra) {
        return false;
    }

    @Override
    public void onBuffUpdate(int currentProgress) {

    }

    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }
}
