/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.PromiseNotificationUtil
 *  io.netty.util.internal.logging.InternalLogger
 *  io.netty.util.internal.logging.InternalLoggerFactory
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class DelegatingChannelPromiseNotifier
implements ChannelPromise,
ChannelFutureListener {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DelegatingChannelPromiseNotifier.class);
    private final ChannelPromise delegate;
    private final boolean logNotifyFailure;

    public DelegatingChannelPromiseNotifier(ChannelPromise delegate) {
        this(delegate, !(delegate instanceof VoidChannelPromise));
    }

    public DelegatingChannelPromiseNotifier(ChannelPromise delegate, boolean logNotifyFailure) {
        this.delegate = (ChannelPromise)ObjectUtil.checkNotNull((Object)delegate, (String)"delegate");
        this.logNotifyFailure = logNotifyFailure;
    }

    public void operationComplete(ChannelFuture future) throws Exception {
        InternalLogger internalLogger;
        Object object = internalLogger = this.logNotifyFailure ? logger : null;
        if (future.isSuccess()) {
            Void result = (Void)future.get();
            PromiseNotificationUtil.trySuccess((Promise)this.delegate, (Object)result, (InternalLogger)internalLogger);
        } else if (future.isCancelled()) {
            PromiseNotificationUtil.tryCancel((Promise)this.delegate, (InternalLogger)internalLogger);
        } else {
            Throwable cause = future.cause();
            PromiseNotificationUtil.tryFailure((Promise)this.delegate, (Throwable)cause, (InternalLogger)internalLogger);
        }
    }

    @Override
    public Channel channel() {
        return this.delegate.channel();
    }

    @Override
    public ChannelPromise setSuccess(Void result) {
        this.delegate.setSuccess(result);
        return this;
    }

    @Override
    public ChannelPromise setSuccess() {
        this.delegate.setSuccess();
        return this;
    }

    @Override
    public boolean trySuccess() {
        return this.delegate.trySuccess();
    }

    public boolean trySuccess(Void result) {
        return this.delegate.trySuccess(result);
    }

    @Override
    public ChannelPromise setFailure(Throwable cause) {
        this.delegate.setFailure(cause);
        return this;
    }

    @Override
    public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        this.delegate.addListener(listener);
        return this;
    }

    @Override
    public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        this.delegate.addListeners(listeners);
        return this;
    }

    @Override
    public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        this.delegate.removeListener(listener);
        return this;
    }

    @Override
    public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        this.delegate.removeListeners(listeners);
        return this;
    }

    public boolean tryFailure(Throwable cause) {
        return this.delegate.tryFailure(cause);
    }

    public boolean setUncancellable() {
        return this.delegate.setUncancellable();
    }

    @Override
    public ChannelPromise await() throws InterruptedException {
        this.delegate.await();
        return this;
    }

    @Override
    public ChannelPromise awaitUninterruptibly() {
        this.delegate.awaitUninterruptibly();
        return this;
    }

    @Override
    public boolean isVoid() {
        return this.delegate.isVoid();
    }

    @Override
    public ChannelPromise unvoid() {
        return this.isVoid() ? new DelegatingChannelPromiseNotifier(this.delegate.unvoid()) : this;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.await(timeout, unit);
    }

    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.delegate.await(timeoutMillis);
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return this.delegate.awaitUninterruptibly(timeout, unit);
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        return this.delegate.awaitUninterruptibly(timeoutMillis);
    }

    public Void getNow() {
        return (Void)this.delegate.getNow();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegate.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return this.delegate.isCancelled();
    }

    public boolean isDone() {
        return this.delegate.isDone();
    }

    public Void get() throws InterruptedException, ExecutionException {
        return (Void)this.delegate.get();
    }

    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (Void)this.delegate.get(timeout, unit);
    }

    @Override
    public ChannelPromise sync() throws InterruptedException {
        this.delegate.sync();
        return this;
    }

    @Override
    public ChannelPromise syncUninterruptibly() {
        this.delegate.syncUninterruptibly();
        return this;
    }

    public boolean isSuccess() {
        return this.delegate.isSuccess();
    }

    public boolean isCancellable() {
        return this.delegate.isCancellable();
    }

    public Throwable cause() {
        return this.delegate.cause();
    }
}

