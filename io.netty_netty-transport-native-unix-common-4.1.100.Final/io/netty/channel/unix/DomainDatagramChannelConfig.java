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

public interface DomainDatagramChannelConfig
extends ChannelConfig {
    public DomainDatagramChannelConfig setAllocator(ByteBufAllocator var1);

    public DomainDatagramChannelConfig setAutoClose(boolean var1);

    public DomainDatagramChannelConfig setAutoRead(boolean var1);

    public DomainDatagramChannelConfig setConnectTimeoutMillis(int var1);

    @Deprecated
    public DomainDatagramChannelConfig setMaxMessagesPerRead(int var1);

    public DomainDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

    public DomainDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    public DomainDatagramChannelConfig setSendBufferSize(int var1);

    public int getSendBufferSize();

    public DomainDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    public DomainDatagramChannelConfig setWriteSpinCount(int var1);
}

