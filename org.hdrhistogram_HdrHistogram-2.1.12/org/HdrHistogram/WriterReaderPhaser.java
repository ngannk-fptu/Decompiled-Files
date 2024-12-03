/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;

public class WriterReaderPhaser {
    private volatile long startEpoch = 0L;
    private volatile long evenEndEpoch = 0L;
    private volatile long oddEndEpoch = Long.MIN_VALUE;
    private final ReentrantLock readerLock = new ReentrantLock();
    private static final AtomicLongFieldUpdater<WriterReaderPhaser> startEpochUpdater = AtomicLongFieldUpdater.newUpdater(WriterReaderPhaser.class, "startEpoch");
    private static final AtomicLongFieldUpdater<WriterReaderPhaser> evenEndEpochUpdater = AtomicLongFieldUpdater.newUpdater(WriterReaderPhaser.class, "evenEndEpoch");
    private static final AtomicLongFieldUpdater<WriterReaderPhaser> oddEndEpochUpdater = AtomicLongFieldUpdater.newUpdater(WriterReaderPhaser.class, "oddEndEpoch");

    public long writerCriticalSectionEnter() {
        return startEpochUpdater.getAndIncrement(this);
    }

    public void writerCriticalSectionExit(long criticalValueAtEnter) {
        (criticalValueAtEnter < 0L ? oddEndEpochUpdater : evenEndEpochUpdater).getAndIncrement(this);
    }

    public void readerLock() {
        this.readerLock.lock();
    }

    public void readerUnlock() {
        this.readerLock.unlock();
    }

    public void flipPhase(long yieldTimeNsec) {
        if (!this.readerLock.isHeldByCurrentThread()) {
            throw new IllegalStateException("flipPhase() can only be called while holding the readerLock()");
        }
        boolean nextPhaseIsEven = this.startEpoch < 0L;
        long initialStartValue = nextPhaseIsEven ? 0L : Long.MIN_VALUE;
        (nextPhaseIsEven ? evenEndEpochUpdater : oddEndEpochUpdater).lazySet(this, initialStartValue);
        long startValueAtFlip = startEpochUpdater.getAndSet(this, initialStartValue);
        while ((nextPhaseIsEven ? this.oddEndEpoch : this.evenEndEpoch) != startValueAtFlip) {
            if (yieldTimeNsec == 0L) {
                Thread.yield();
                continue;
            }
            try {
                TimeUnit.NANOSECONDS.sleep(yieldTimeNsec);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public void flipPhase() {
        this.flipPhase(0L);
    }
}

