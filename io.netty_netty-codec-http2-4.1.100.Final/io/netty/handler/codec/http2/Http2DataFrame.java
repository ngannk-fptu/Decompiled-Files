/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http2.Http2StreamFrame;

public interface Http2DataFrame
extends Http2StreamFrame,
ByteBufHolder {
    public int padding();

    public ByteBuf content();

    public int initialFlowControlledBytes();

    public boolean isEndStream();

    public Http2DataFrame copy();

    public Http2DataFrame duplicate();

    public Http2DataFrame retainedDuplicate();

    public Http2DataFrame replace(ByteBuf var1);

    public Http2DataFrame retain();

    public Http2DataFrame retain(int var1);

    public Http2DataFrame touch();

    public Http2DataFrame touch(Object var1);
}

