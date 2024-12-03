/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelPromise
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class ZlibEncoder
extends MessageToByteEncoder<ByteBuf> {
    protected ZlibEncoder() {
        super(false);
    }

    public abstract boolean isClosed();

    public abstract ChannelFuture close();

    public abstract ChannelFuture close(ChannelPromise var1);
}

