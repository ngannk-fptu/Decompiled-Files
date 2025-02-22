/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyRstStreamFrame
extends DefaultSpdyStreamFrame
implements SpdyRstStreamFrame {
    private SpdyStreamStatus status;

    public DefaultSpdyRstStreamFrame(int streamId, int statusCode) {
        this(streamId, SpdyStreamStatus.valueOf(statusCode));
    }

    public DefaultSpdyRstStreamFrame(int streamId, SpdyStreamStatus status) {
        super(streamId);
        this.setStatus(status);
    }

    @Override
    public SpdyRstStreamFrame setStreamId(int streamId) {
        super.setStreamId(streamId);
        return this;
    }

    @Override
    public SpdyRstStreamFrame setLast(boolean last) {
        super.setLast(last);
        return this;
    }

    @Override
    public SpdyStreamStatus status() {
        return this.status;
    }

    @Override
    public SpdyRstStreamFrame setStatus(SpdyStreamStatus status) {
        this.status = status;
        return this;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + StringUtil.NEWLINE + "--> Stream-ID = " + this.streamId() + StringUtil.NEWLINE + "--> Status: " + this.status();
    }
}

