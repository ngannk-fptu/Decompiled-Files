/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class CancellableFuture<T>
extends CompletableFuture<T> {
    private final CountDownLatch cancellationCompleted = new CountDownLatch(1);
    private final AtomicReference<T> finalResult = new AtomicReference();
    private final AtomicReference<Throwable> throwable = new AtomicReference();

    public CompletableFuture<T> whenCompleteOrCancelledAsync(BiConsumer<? super T, ? super Throwable> action, long timeout, TimeUnit timeUnit) {
        return this.whenCompleteOrCancelledAsync(action, timeout, timeUnit, ForkJoinPool.commonPool());
    }

    public CompletableFuture<T> whenCompleteOrCancelledAsync(BiConsumer<? super T, ? super Throwable> action, long timeout, TimeUnit timeUnit, Executor executor) {
        BiConsumer<Object, Throwable> wrapperAction = (r, t) -> {
            if (this.isCancelled()) {
                try {
                    if (this.cancellationCompleted.await(timeout, timeUnit)) {
                        Throwable underlying = this.throwable.get();
                        action.accept((T)this.finalResult.get(), underlying == null ? t : underlying);
                    }
                    action.accept((T)r, (Throwable)t);
                }
                catch (InterruptedException e) {
                    throw new UncheckedInterruptedException(e);
                }
            } else {
                action.accept((T)r, (Throwable)t);
            }
        };
        return super.whenCompleteAsync(wrapperAction, executor);
    }

    @Override
    public boolean complete(T value) {
        this.finalResult.compareAndSet(null, value);
        this.cancellationCompleted.countDown();
        return super.complete(value);
    }

    @Override
    public boolean completeExceptionally(Throwable ex) {
        this.throwable.compareAndSet(null, ex);
        this.cancellationCompleted.countDown();
        return super.completeExceptionally(ex);
    }
}

