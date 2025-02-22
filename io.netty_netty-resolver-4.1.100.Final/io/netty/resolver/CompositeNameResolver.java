/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.FutureListener
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.resolver;

import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.List;

public final class CompositeNameResolver<T>
extends SimpleNameResolver<T> {
    private final NameResolver<T>[] resolvers;

    public CompositeNameResolver(EventExecutor executor, NameResolver<T> ... resolvers) {
        super(executor);
        ObjectUtil.checkNotNull(resolvers, (String)"resolvers");
        for (int i = 0; i < resolvers.length; ++i) {
            ObjectUtil.checkNotNull(resolvers[i], (String)("resolvers[" + i + ']'));
        }
        if (resolvers.length < 2) {
            throw new IllegalArgumentException("resolvers: " + Arrays.asList(resolvers) + " (expected: at least 2 resolvers)");
        }
        this.resolvers = (NameResolver[])resolvers.clone();
    }

    @Override
    protected void doResolve(String inetHost, Promise<T> promise) throws Exception {
        this.doResolveRec(inetHost, promise, 0, null);
    }

    private void doResolveRec(final String inetHost, final Promise<T> promise, final int resolverIndex, Throwable lastFailure) throws Exception {
        if (resolverIndex >= this.resolvers.length) {
            promise.setFailure(lastFailure);
        } else {
            NameResolver<T> resolver = this.resolvers[resolverIndex];
            resolver.resolve(inetHost).addListener((GenericFutureListener)new FutureListener<T>(){

                public void operationComplete(Future<T> future) throws Exception {
                    if (future.isSuccess()) {
                        promise.setSuccess(future.getNow());
                    } else {
                        CompositeNameResolver.this.doResolveRec(inetHost, promise, resolverIndex + 1, future.cause());
                    }
                }
            });
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<T>> promise) throws Exception {
        this.doResolveAllRec(inetHost, promise, 0, null);
    }

    private void doResolveAllRec(final String inetHost, final Promise<List<T>> promise, final int resolverIndex, Throwable lastFailure) throws Exception {
        if (resolverIndex >= this.resolvers.length) {
            promise.setFailure(lastFailure);
        } else {
            NameResolver<T> resolver = this.resolvers[resolverIndex];
            resolver.resolveAll(inetHost).addListener((GenericFutureListener)new FutureListener<List<T>>(){

                public void operationComplete(Future<List<T>> future) throws Exception {
                    if (future.isSuccess()) {
                        promise.setSuccess(future.getNow());
                    } else {
                        CompositeNameResolver.this.doResolveAllRec(inetHost, promise, resolverIndex + 1, future.cause());
                    }
                }
            });
        }
    }
}

