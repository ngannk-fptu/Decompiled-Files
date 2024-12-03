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
import io.netty.handler.codec.http2.Http2Frame;

public interface Http2GoAwayFrame
extends Http2Frame,
ByteBufHolder {
    public long errorCode();

    public int extraStreamIds();

    public Http2GoAwayFrame setExtraStreamIds(int var1);

    public int lastStreamId();

    public ByteBuf content();

    public Http2GoAwayFrame copy();

    public Http2GoAwayFrame duplicate();

    public Http2GoAwayFrame retainedDuplicate();

    public Http2GoAwayFrame replace(ByteBuf var1);

    public Http2GoAwayFrame retain();

    public Http2GoAwayFrame retain(int var1);

    public Http2GoAwayFrame touch();

    public Http2GoAwayFrame touch(Object var1);
}

