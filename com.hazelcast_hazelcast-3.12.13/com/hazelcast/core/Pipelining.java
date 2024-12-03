/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.spi.annotation.Beta;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

@Beta
public class Pipelining<E> {
    private final AtomicInteger permits = new AtomicInteger();
    private final List<ICompletableFuture<E>> futures = new ArrayList<ICompletableFuture<E>>();
    private Thread thread;

    public Pipelining(int depth) {
        Preconditions.checkPositive(depth, "depth must be positive");
        this.permits.set(depth);
    }

    public List<E> results() throws Exception {
        ArrayList result = new ArrayList(this.futures.size());
        for (ICompletableFuture<E> f : this.futures) {
            result.add(f.get());
        }
        return result;
    }

    public ICompletableFuture<E> add(ICompletableFuture<E> future) throws InterruptedException {
        Preconditions.checkNotNull(future, "future can't be null");
        this.thread = Thread.currentThread();
        this.down();
        this.futures.add(future);
        future.andThen(new ExecutionCallback<E>(){

            @Override
            public void onResponse(E response) {
                Pipelining.this.up();
            }

            @Override
            public void onFailure(Throwable t) {
                Pipelining.this.up();
            }
        }, ConcurrencyUtil.CALLER_RUNS);
        return future;
    }

    private void down() throws InterruptedException {
        int update;
        int current;
        while (!this.permits.compareAndSet(current = this.permits.get(), update = current - 1)) {
        }
        while (this.permits.get() == -1) {
            LockSupport.park();
            if (!Thread.interrupted()) continue;
            throw new InterruptedException();
        }
    }

    private void up() {
        int update;
        int current;
        while (!this.permits.compareAndSet(current = this.permits.get(), update = current + 1)) {
        }
        if (current == -1) {
            LockSupport.unpark(this.thread);
        }
    }
}

