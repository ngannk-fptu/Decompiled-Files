/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import com.atlassian.util.concurrent.BooleanLatch;
import com.atlassian.util.concurrent.NotNull;
import com.atlassian.util.concurrent.Nullable;
import com.atlassian.util.concurrent.PhasedLatch;
import com.atlassian.util.concurrent.ReusableLatch;
import com.atlassian.util.concurrent.Timeout;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.jcip.annotations.ThreadSafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ThreadSafe
public class BlockingReference<V> {
    private final AtomicReference<V> ref = new AtomicReference();
    private final ReusableLatch latch;

    public static <V> BlockingReference<V> newSRSW() {
        return BlockingReference.newSRSW(null);
    }

    public static <V> BlockingReference<V> newSRSW(V initialValue) {
        return new BlockingReference<V>(new BooleanLatch(), initialValue);
    }

    public static <V> BlockingReference<V> newMRSW() {
        return BlockingReference.newMRSW(null);
    }

    public static <V> BlockingReference<V> newMRSW(V initialValue) {
        return new BlockingReference<V>(new PhasedLatch(){
            private final AtomicInteger currentPhase = new AtomicInteger(super.getPhase());

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public synchronized int getPhase() {
                try {
                    int n = this.currentPhase.get();
                    return n;
                }
                finally {
                    this.currentPhase.set(super.getPhase());
                }
            }
        }, initialValue);
    }

    BlockingReference(ReusableLatch latch, V initialValue) {
        this.latch = latch;
        this.internalSet(initialValue);
    }

    @Deprecated
    public BlockingReference() {
        this(new BooleanLatch(), null);
    }

    @Deprecated
    public BlockingReference(@NotNull V value) {
        this(new BooleanLatch(), value);
    }

    @NotNull
    public V take() throws InterruptedException {
        Object result = null;
        while (result == null) {
            this.latch.await();
            result = this.ref.getAndSet(null);
        }
        return result;
    }

    @NotNull
    public V take(long time, TimeUnit unit) throws TimeoutException, InterruptedException {
        Timeout timeout = Timeout.getNanosTimeout(time, unit);
        Object result = null;
        while (result == null) {
            timeout.await(this.latch);
            result = this.ref.getAndSet(null);
        }
        return result;
    }

    @NotNull
    public V get() throws InterruptedException {
        V result = this.ref.get();
        while (result == null) {
            this.latch.await();
            result = this.ref.get();
        }
        return result;
    }

    @NotNull
    public V get(long time, @NotNull TimeUnit unit) throws TimeoutException, InterruptedException {
        Timeout timeout = Timeout.getNanosTimeout(time, unit);
        V result = this.ref.get();
        while (result == null) {
            timeout.await(this.latch);
            result = this.ref.get();
        }
        return result;
    }

    public void set(@NotNull V value) {
        Assertions.notNull("value", value);
        this.internalSet(value);
    }

    public boolean isEmpty() {
        return this.peek() == null;
    }

    @Nullable
    public V peek() {
        return this.ref.get();
    }

    public void clear() {
        this.internalSet(null);
    }

    private void internalSet(@Nullable V value) {
        this.ref.set(value);
        this.latch.release();
    }
}

