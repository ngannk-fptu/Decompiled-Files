/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;

@Deprecated
public interface OioServerSocketChannelConfig
extends ServerSocketChannelConfig {
    public OioServerSocketChannelConfig setSoTimeout(int var1);

    public int getSoTimeout();

    @Override
    public OioServerSocketChannelConfig setBacklog(int var1);

    @Override
    public OioServerSocketChannelConfig setReuseAddress(boolean var1);

    @Override
    public OioServerSocketChannelConfig setReceiveBufferSize(int var1);

    @Override
    public OioServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

    @Override
    public OioServerSocketChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    @Deprecated
    public OioServerSocketChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public OioServerSocketChannelConfig setWriteSpinCount(int var1);

    @Override
    public OioServerSocketChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public OioServerSocketChannelConfig setAutoRead(boolean var1);

    @Override
    public OioServerSocketChannelConfig setAutoClose(boolean var1);

    @Override
    public OioServerSocketChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public OioServerSocketChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public OioServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    @Override
    public OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

