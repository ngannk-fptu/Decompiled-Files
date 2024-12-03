/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpObject;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultHttpContent
extends DefaultHttpObject
implements HttpContent {
    private final ByteBuf content;

    public DefaultHttpContent(ByteBuf content) {
        this.content = (ByteBuf)ObjectUtil.checkNotNull((Object)content, (String)"content");
    }

    public ByteBuf content() {
        return this.content;
    }

    @Override
    public HttpContent copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public HttpContent duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public HttpContent retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public HttpContent replace(ByteBuf content) {
        return new DefaultHttpContent(content);
    }

    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public HttpContent retain() {
        this.content.retain();
        return this;
    }

    @Override
    public HttpContent retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    @Override
    public HttpContent touch() {
        this.content.touch();
        return this;
    }

    @Override
    public HttpContent touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    public boolean release() {
        return this.content.release();
    }

    public boolean release(int decrement) {
        return this.content.release(decrement);
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + "(data: " + this.content() + ", decoderResult: " + this.decoderResult() + ')';
    }
}

