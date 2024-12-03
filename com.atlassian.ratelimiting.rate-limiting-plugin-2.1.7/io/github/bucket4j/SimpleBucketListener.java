/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.BucketListener;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleBucketListener
implements BucketListener {
    private AtomicLong consumed = new AtomicLong();
    private AtomicLong rejected = new AtomicLong();
    private AtomicLong delayedNanos = new AtomicLong();
    private AtomicLong parkedNanos = new AtomicLong();
    private AtomicLong interrupted = new AtomicLong();

    @Override
    public void onConsumed(long tokens) {
        this.consumed.addAndGet(tokens);
    }

    @Override
    public void onRejected(long tokens) {
        this.rejected.addAndGet(tokens);
    }

    @Override
    public void onDelayed(long nanos) {
        this.delayedNanos.addAndGet(nanos);
    }

    @Override
    public void onParked(long nanos) {
        this.parkedNanos.addAndGet(nanos);
    }

    @Override
    public void onInterrupted(InterruptedException e) {
        this.interrupted.incrementAndGet();
    }

    public long getConsumed() {
        return this.consumed.get();
    }

    public long getRejected() {
        return this.rejected.get();
    }

    public long getDelayedNanos() {
        return this.delayedNanos.get();
    }

    public long getParkedNanos() {
        return this.parkedNanos.get();
    }

    public long getInterrupted() {
        return this.interrupted.get();
    }
}

