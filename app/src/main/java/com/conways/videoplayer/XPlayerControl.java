package com.conways.videoplayer;

import java.io.IOException;

/**
 * Created by Conways on 2017/4/6.
 */

public interface XPlayerControl {
    public void start(String path) throws IOException;

    public void pause();

    public void stop();

    public void restart() throws IOException;

    public void destroy();
}
