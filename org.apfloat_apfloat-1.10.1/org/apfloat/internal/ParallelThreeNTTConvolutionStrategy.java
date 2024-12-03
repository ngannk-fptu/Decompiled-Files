/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apfloat.ApfloatContext;
import org.apfloat.internal.Parallelizable;
import org.apfloat.internal.ThreeNTTConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;

public class ParallelThreeNTTConvolutionStrategy
extends ThreeNTTConvolutionStrategy {
    private static Map<Object, Lock> locks = new WeakHashMap<Object, Lock>();
    private Object key;

    public ParallelThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy) {
        super(radix, nttStrategy);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void lock(long length) {
        ApfloatContext ctx;
        assert (this.key == null);
        if (this.nttStrategy instanceof Parallelizable && this.carryCRTStrategy instanceof Parallelizable && this.stepStrategy instanceof Parallelizable && length > (ctx = ApfloatContext.getContext()).getSharedMemoryTreshold() / (long)ctx.getBuilderFactory().getElementSize()) {
            this.key = ctx.getSharedMemoryLock();
            if (this.key != null) {
                Lock lock;
                Map<Object, Lock> map = locks;
                synchronized (map) {
                    lock = locks.computeIfAbsent(this.key, k -> new ReentrantLock());
                }
                ctx.wait(new LockFuture(lock));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void unlock() {
        if (this.key != null) {
            Map<Object, Lock> map = locks;
            synchronized (map) {
                locks.get(this.key).unlock();
            }
        }
    }

    private static class LockFuture
    extends FutureTask<Void> {
        private static final Callable<Void> VOID_CALLABLE = () -> null;
        private Thread thread = Thread.currentThread();
        private Lock lock;
        private boolean done;

        public LockFuture(Lock lock) {
            super(VOID_CALLABLE);
            this.lock = lock;
        }

        @Override
        public synchronized boolean isDone() {
            if (!this.done && Thread.currentThread().equals(this.thread)) {
                this.done = this.lock.tryLock();
            }
            return this.done;
        }
    }
}

