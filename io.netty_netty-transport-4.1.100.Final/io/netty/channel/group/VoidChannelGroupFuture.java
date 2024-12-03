/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 */
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupException;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

final class VoidChannelGroupFuture
implements ChannelGroupFuture {
    private static final Iterator<ChannelFuture> EMPTY = Collections.emptyList().iterator();
    private final ChannelGroup group;

    VoidChannelGroupFuture(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public ChannelGroup group() {
        return this.group;
    }

    @Override
    public ChannelFuture find(Channel channel) {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public ChannelGroupException cause() {
        return null;
    }

    @Override
    public boolean isPartialSuccess() {
        return false;
    }

    @Override
    public boolean isPartialFailure() {
        return false;
    }

    @Override
    public ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture await() {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture awaitUninterruptibly() {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture syncUninterruptibly() {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public ChannelGroupFuture sync() {
        throw VoidChannelGroupFuture.reject();
    }

    @Override
    public Iterator<ChannelFuture> iterator() {
        return EMPTY;
    }

    public boolean isCancellable() {
        return false;
    }

    public boolean await(long timeout, TimeUnit unit) {
        throw VoidChannelGroupFuture.reject();
    }

    public boolean await(long timeoutMillis) {
        throw VoidChannelGroupFuture.reject();
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        throw VoidChannelGroupFuture.reject();
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        throw VoidChannelGroupFuture.reject();
    }

    public Void getNow() {
        return null;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public Void get() {
        throw VoidChannelGroupFuture.reject();
    }

    public Void get(long timeout, TimeUnit unit) {
        throw VoidChannelGroupFuture.reject();
    }

    private static RuntimeException reject() {
        return new IllegalStateException("void future");
    }
}

