/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFuture
extends Future<Void> {
    public Channel channel();

    public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1);

    public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

    public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    public ChannelFuture sync() throws InterruptedException;

    public ChannelFuture syncUninterruptibly();

    public ChannelFuture await() throws InterruptedException;

    public ChannelFuture awaitUninterruptibly();

    public boolean isVoid();
}

