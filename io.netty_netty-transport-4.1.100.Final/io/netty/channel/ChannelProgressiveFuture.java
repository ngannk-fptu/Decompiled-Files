/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.ProgressiveFuture
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;

public interface ChannelProgressiveFuture
extends ChannelFuture,
ProgressiveFuture<Void> {
    @Override
    public ChannelProgressiveFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public ChannelProgressiveFuture addListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelProgressiveFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1);

    @Override
    public ChannelProgressiveFuture removeListeners(GenericFutureListener<? extends Future<? super Void>> ... var1);

    @Override
    public ChannelProgressiveFuture sync() throws InterruptedException;

    @Override
    public ChannelProgressiveFuture syncUninterruptibly();

    @Override
    public ChannelProgressiveFuture await() throws InterruptedException;

    @Override
    public ChannelProgressiveFuture awaitUninterruptibly();
}

