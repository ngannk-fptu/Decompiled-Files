/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promise$TryConsumer
 *  javax.annotation.Nonnull
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.httpclient.api.ResponseTransformation;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.Promise;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;

final class WrappingResponsePromise
implements ResponsePromise {
    private final Promise<Response> delegate;

    WrappingResponsePromise(Promise<Response> delegate) {
        this.delegate = (Promise)Preconditions.checkNotNull(delegate);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegate.cancel(mayInterruptIfRunning);
    }

    public Response claim() {
        return (Response)this.delegate.claim();
    }

    public Promise<Response> done(Consumer<? super Response> consumer) {
        return this.delegate.done(consumer);
    }

    public Promise<Response> fail(Consumer<Throwable> consumer) {
        return this.delegate.fail(consumer);
    }

    public <B> Promise<B> flatMap(Function<? super Response, ? extends Promise<? extends B>> function) {
        return this.delegate.flatMap(function);
    }

    public <B> Promise<B> fold(Function<Throwable, ? extends B> function, Function<? super Response, ? extends B> function1) {
        return this.delegate.fold(function, function1);
    }

    public Response get() throws InterruptedException, ExecutionException {
        return (Response)this.delegate.get();
    }

    public Response get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (Response)this.delegate.get(timeout, unit);
    }

    public boolean isCancelled() {
        return this.delegate.isCancelled();
    }

    public boolean isDone() {
        return this.delegate.isDone();
    }

    public <B> Promise<B> map(Function<? super Response, ? extends B> function) {
        return this.delegate.map(function);
    }

    public Promise<Response> recover(Function<Throwable, ? extends Response> function) {
        return this.delegate.recover(function);
    }

    public Promise<Response> then(Promise.TryConsumer<? super Response> tryConsumer) {
        return this.delegate.then(tryConsumer);
    }

    @Override
    public <T> Promise<T> transform(ResponseTransformation<T> transformation) {
        return this.delegate.fold(transformation.getFailFunction(), transformation.getSuccessFunctions());
    }
}

