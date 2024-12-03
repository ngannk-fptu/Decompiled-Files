/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.spdy.SpdyStreamFrame;

public interface SpdyDataFrame
extends ByteBufHolder,
SpdyStreamFrame {
    @Override
    public SpdyDataFrame setStreamId(int var1);

    @Override
    public SpdyDataFrame setLast(boolean var1);

    public ByteBuf content();

    public SpdyDataFrame copy();

    public SpdyDataFrame duplicate();

    public SpdyDataFrame retainedDuplicate();

    public SpdyDataFrame replace(ByteBuf var1);

    public SpdyDataFrame retain();

    public SpdyDataFrame retain(int var1);

    public SpdyDataFrame touch();

    public SpdyDataFrame touch(Object var1);
}

