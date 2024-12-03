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
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2StreamFrame;

public interface Http2UnknownFrame
extends Http2StreamFrame,
ByteBufHolder {
    @Override
    public Http2FrameStream stream();

    @Override
    public Http2UnknownFrame stream(Http2FrameStream var1);

    public byte frameType();

    public Http2Flags flags();

    public Http2UnknownFrame copy();

    public Http2UnknownFrame duplicate();

    public Http2UnknownFrame retainedDuplicate();

    public Http2UnknownFrame replace(ByteBuf var1);

    public Http2UnknownFrame retain();

    public Http2UnknownFrame retain(int var1);

    public Http2UnknownFrame touch();

    public Http2UnknownFrame touch(Object var1);
}

