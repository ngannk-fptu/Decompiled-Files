/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.concurrent.atomic.AtomicLong;
import org.apfloat.spi.Util;

public abstract class ParallelRunnable
implements Runnable {
    private static final int MINIMUM_BATCH_SIZE = 16;
    private long length;
    private long preferredBatchSize;
    private AtomicLong started;
    private AtomicLong completed;

    protected ParallelRunnable(long length) {
        this.preferredBatchSize = Util.sqrt4down(length);
        this.length = length;
        this.started = new AtomicLong();
        this.completed = new AtomicLong();
    }

    @Override
    public final void run() {
        while (this.runBatch()) {
        }
        while (this.isWorkToBeCompleted()) {
            Thread.yield();
        }
    }

    public final boolean runBatch() {
        long startValue;
        long batchSize;
        long length;
        boolean isRun = false;
        if (this.isWorkToBeStarted() && (length = Math.min(batchSize = Math.max(16L, this.getPreferredBatchSize()), this.length - (startValue = this.started.getAndAdd(batchSize)))) > 0L) {
            Runnable runnable = this.getRunnable(startValue, length);
            runnable.run();
            this.completed.addAndGet(length);
            isRun = true;
        }
        return isRun;
    }

    public boolean isWorkToBeStarted() {
        return this.started.get() < this.length;
    }

    public boolean isWorkToBeCompleted() {
        return this.completed.get() < this.length;
    }

    protected Runnable getRunnable(int startValue, int length) {
        throw new UnsupportedOperationException("Not implemented");
    }

    protected Runnable getRunnable(long startValue, long length) {
        if (startValue <= Integer.MAX_VALUE - length) {
            return this.getRunnable((int)startValue, (int)length);
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    protected long getPreferredBatchSize() {
        return this.preferredBatchSize;
    }
}

