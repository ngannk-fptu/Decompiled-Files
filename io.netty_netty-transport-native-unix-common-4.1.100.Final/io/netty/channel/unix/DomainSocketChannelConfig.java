/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.ChannelConfig
 *  io.netty.channel.MessageSizeEstimator
 *  io.netty.channel.RecvByteBufAllocator
 *  io.netty.channel.WriteBufferWaterMark
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.DomainSocketReadMode;

public interface DomainSocketChannelConfig
extends ChannelConfig {
    @Deprecated
    public DomainSocketChannelConfig setMaxMessagesPerRead(int var1);

    public DomainSocketChannelConfig setConnectTimeoutMillis(int var1);

    public DomainSocketChannelConfig setWriteSpinCount(int var1);

    public DomainSocketChannelConfig setAllocator(ByteBufAllocator var1);

    public DomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    public DomainSocketChannelConfig setAutoRead(boolean var1);

    public DomainSocketChannelConfig setAutoClose(boolean var1);

    @Deprecated
    public DomainSocketChannelConfig setWriteBufferHighWaterMark(int var1);

    @Deprecated
    public DomainSocketChannelConfig setWriteBufferLowWaterMark(int var1);

    public DomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    public DomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

    public DomainSocketChannelConfig setReadMode(DomainSocketReadMode var1);

    public DomainSocketReadMode getReadMode();
}

