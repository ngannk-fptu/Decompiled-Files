/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class JdkFutureAdapters {
    public static <V> ListenableFuture<V> listenInPoolThread(Future<V> future) {
        if (future instanceof ListenableFuture) {
            return (ListenableFuture)future;
        }
        return new ListenableFutureAdapter<V>(future);
    }

    public static <V> ListenableFuture<V> listenInPoolThread(Future<V> future, Executor executor) {
        Preconditions.checkNotNull(executor);
        if (future instanceof ListenableFuture) {
            return (ListenableFuture)future;
        }
        return new ListenableFutureAdapter<V>(future, executor);
    }

    private JdkFutureAdapters() {
    }

    private static class ListenableFutureAdapter<V>
    extends ForwardingFuture<V>
    implements ListenableFuture<V> {
        private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ListenableFutureAdapter-thread-%d").build();
        private static final Executor defaultAdapterExecutor = Executors.newCachedThreadPool(threadFactory);
        private final Executor adapterExecutor;
        private final ExecutionList executionList = new ExecutionList();
        private final AtomicBoolean hasListeners = new AtomicBoolean(false);
        private final Future<V> delegate;

        ListenableFutureAdapter(Future<V> delegate) {
            this(delegate, defaultAdapterExecutor);
        }

        ListenableFutureAdapter(Future<V> delegate, Executor adapterExecutor) {
            this.delegate = Preconditions.checkNotNull(delegate);
            this.adapterExecutor = Preconditions.checkNotNull(adapterExecutor);
        }

        @Override
        protected Future<V> delegate() {
            return this.delegate;
        }

        @Override
        public void addListener(Runnable listener, Executor exec) {
            this.executionList.add(listener, exec);
            if (this.hasListeners.compareAndSet(false, true)) {
                if (this.delegate.isDone()) {
                    this.executionList.execute();
                    return;
                }
                this.adapterExecutor.execute(() -> {
                    try {
                        Uninterruptibles.getUninterruptibly(this.delegate);
                    }
                    catch (Error | RuntimeException | ExecutionException throwable) {
                        // empty catch block
                    }
                    this.executionList.execute();
                });
            }
        }
    }
}

