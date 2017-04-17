package com.conways.videoplayer;

/**
 * Created by Conways on 2017/4/13.
 */

public interface XplayerStateChangeLisenter {

    void onSizeChanged(int width, int height);

    void onComplete();

    boolean onError(int what, int extra);

    void onBuffUpdate(int currentProgress);

}
