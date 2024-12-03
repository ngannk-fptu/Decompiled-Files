/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.resolver;

import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class SimpleNameResolver<T>
implements NameResolver<T> {
    private final EventExecutor executor;

    protected SimpleNameResolver(EventExecutor executor) {
        this.executor = (EventExecutor)ObjectUtil.checkNotNull((Object)executor, (String)"executor");
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    @Override
    public final Future<T> resolve(String inetHost) {
        Promise promise = this.executor().newPromise();
        return this.resolve(inetHost, promise);
    }

    @Override
    public Future<T> resolve(String inetHost, Promise<T> promise) {
        ObjectUtil.checkNotNull(promise, (String)"promise");
        try {
            this.doResolve(inetHost, promise);
            return promise;
        }
        catch (Exception e) {
            return promise.setFailure((Throwable)e);
        }
    }

    @Override
    public final Future<List<T>> resolveAll(String inetHost) {
        Promise promise = this.executor().newPromise();
        return this.resolveAll(inetHost, promise);
    }

    @Override
    public Future<List<T>> resolveAll(String inetHost, Promise<List<T>> promise) {
        ObjectUtil.checkNotNull(promise, (String)"promise");
        try {
            this.doResolveAll(inetHost, promise);
            return promise;
        }
        catch (Exception e) {
            return promise.setFailure((Throwable)e);
        }
    }

    protected abstract void doResolve(String var1, Promise<T> var2) throws Exception;

    protected abstract void doResolveAll(String var1, Promise<List<T>> var2) throws Exception;

    @Override
    public void close() {
    }
}

