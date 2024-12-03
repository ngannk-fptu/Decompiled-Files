/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.base.Function
 *  com.google.common.util.concurrent.ForwardingListenableFuture
 *  com.google.common.util.concurrent.FutureCallback
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Effect;
import com.atlassian.util.concurrent.Promise;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.FutureCallback;

@Beta
public abstract class ForwardingPromise<A>
extends ForwardingListenableFuture<A>
implements Promise<A> {
    protected ForwardingPromise() {
    }

    protected abstract Promise<A> delegate();

    @Override
    public A claim() {
        return this.delegate().claim();
    }

    @Override
    public Promise<A> done(Effect<? super A> e) {
        this.delegate().done(e);
        return this;
    }

    @Override
    public Promise<A> fail(Effect<Throwable> e) {
        this.delegate().fail(e);
        return this;
    }

    @Override
    public Promise<A> then(FutureCallback<? super A> callback) {
        this.delegate().then(callback);
        return this;
    }

    @Override
    public <B> Promise<B> map(Function<? super A, ? extends B> function) {
        return this.delegate().map(function);
    }

    @Override
    public <B> Promise<B> flatMap(Function<? super A, ? extends Promise<? extends B>> function) {
        return this.delegate().flatMap(function);
    }

    @Override
    public Promise<A> recover(Function<Throwable, ? extends A> handleThrowable) {
        return this.delegate().recover(handleThrowable);
    }

    @Override
    public <B> Promise<B> fold(Function<Throwable, ? extends B> handleThrowable, Function<? super A, ? extends B> function) {
        return this.delegate().fold(handleThrowable, function);
    }
}

