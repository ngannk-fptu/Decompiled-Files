/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.DecoratingHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2MaxRstFrameListener;
import io.netty.util.internal.ObjectUtil;

final class Http2MaxRstFrameDecoder
extends DecoratingHttp2ConnectionDecoder {
    private final int maxRstFramesPerWindow;
    private final int secondsPerWindow;

    Http2MaxRstFrameDecoder(Http2ConnectionDecoder delegate, int maxRstFramesPerWindow, int secondsPerWindow) {
        super(delegate);
        this.maxRstFramesPerWindow = ObjectUtil.checkPositive((int)maxRstFramesPerWindow, (String)"maxRstFramesPerWindow");
        this.secondsPerWindow = ObjectUtil.checkPositive((int)secondsPerWindow, (String)"secondsPerWindow");
    }

    @Override
    public void frameListener(Http2FrameListener listener) {
        if (listener != null) {
            super.frameListener(new Http2MaxRstFrameListener(listener, this.maxRstFramesPerWindow, this.secondsPerWindow));
        } else {
            super.frameListener(null);
        }
    }

    @Override
    public Http2FrameListener frameListener() {
        Http2FrameListener frameListener = this.frameListener0();
        if (frameListener instanceof Http2MaxRstFrameListener) {
            return ((Http2MaxRstFrameListener)frameListener).listener;
        }
        return frameListener;
    }

    Http2FrameListener frameListener0() {
        return super.frameListener();
    }
}

