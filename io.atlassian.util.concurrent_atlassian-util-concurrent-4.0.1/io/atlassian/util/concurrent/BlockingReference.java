/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.BooleanLatch;
import io.atlassian.util.concurrent.NotNull;
import io.atlassian.util.concurrent.Nullable;
import io.atlassian.util.concurrent.PhasedLatch;
import io.atlassian.util.concurrent.ReusableLatch;
import io.atlassian.util.concurrent.Timeout;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.jcip.annotations.ThreadSafe;

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

            @Override
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
    public final V take() throws InterruptedException {
        Object result = null;
        while (result == null) {
            this.latch.await();
            result = this.ref.getAndSet(null);
        }
        return result;
    }

    @NotNull
    public final V take(long time, TimeUnit unit) throws TimeoutException, InterruptedException {
        Timeout timeout = Timeout.getNanosTimeout(time, unit);
        Object result = null;
        while (result == null) {
            timeout.await(this.latch);
            result = this.ref.getAndSet(null);
        }
        return result;
    }

    @NotNull
    public final V get() throws InterruptedException {
        V result = this.ref.get();
        while (result == null) {
            this.latch.await();
            result = this.ref.get();
        }
        return result;
    }

    @NotNull
    public final V get(long time, @NotNull TimeUnit unit) throws TimeoutException, InterruptedException {
        Timeout timeout = Timeout.getNanosTimeout(time, unit);
        V result = this.ref.get();
        while (result == null) {
            timeout.await(this.latch);
            result = this.ref.get();
        }
        return result;
    }

    public final void set(@NotNull V value) {
        Objects.requireNonNull(value, "value");
        this.internalSet(value);
    }

    public final boolean isEmpty() {
        return this.peek() == null;
    }

    @Nullable
    public final V peek() {
        return this.ref.get();
    }

    public final void clear() {
        this.internalSet(null);
    }

    private final void internalSet(@Nullable V value) {
        this.ref.set(value);
        this.latch.release();
    }
}

