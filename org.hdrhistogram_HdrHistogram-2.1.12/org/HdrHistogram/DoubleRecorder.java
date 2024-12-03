/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.concurrent.atomic.AtomicLong;
import org.HdrHistogram.ConcurrentDoubleHistogram;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleValueRecorder;
import org.HdrHistogram.PackedConcurrentDoubleHistogram;
import org.HdrHistogram.WriterReaderPhaser;

public class DoubleRecorder
implements DoubleValueRecorder {
    private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
    private final long instanceId = instanceIdSequencer.getAndIncrement();
    private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
    private volatile ConcurrentDoubleHistogram activeHistogram;
    private ConcurrentDoubleHistogram inactiveHistogram;

    public DoubleRecorder(int numberOfSignificantValueDigits, boolean packed) {
        this.activeHistogram = packed ? new PackedInternalConcurrentDoubleHistogram(this.instanceId, numberOfSignificantValueDigits) : new InternalConcurrentDoubleHistogram(this.instanceId, numberOfSignificantValueDigits);
        this.inactiveHistogram = null;
        this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
    }

    public DoubleRecorder(int numberOfSignificantValueDigits) {
        this(numberOfSignificantValueDigits, false);
    }

    public DoubleRecorder(long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
        this.activeHistogram = new InternalConcurrentDoubleHistogram(this.instanceId, highestToLowestValueRatio, numberOfSignificantValueDigits);
        this.inactiveHistogram = null;
        this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordValue(double value) {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.activeHistogram.recordValue(value);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordValueWithCount(double value, long count) throws ArrayIndexOutOfBoundsException {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.activeHistogram.recordValueWithCount(value, count);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordValueWithExpectedInterval(double value, double expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.activeHistogram.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    public synchronized DoubleHistogram getIntervalHistogram() {
        return this.getIntervalHistogram(null);
    }

    public synchronized DoubleHistogram getIntervalHistogram(DoubleHistogram histogramToRecycle) {
        return this.getIntervalHistogram(histogramToRecycle, true);
    }

    public synchronized DoubleHistogram getIntervalHistogram(DoubleHistogram histogramToRecycle, boolean enforeContainingInstance) {
        this.validateFitAsReplacementHistogram(histogramToRecycle, enforeContainingInstance);
        this.inactiveHistogram = (ConcurrentDoubleHistogram)histogramToRecycle;
        this.performIntervalSample();
        ConcurrentDoubleHistogram sampledHistogram = this.inactiveHistogram;
        this.inactiveHistogram = null;
        return sampledHistogram;
    }

    public synchronized void getIntervalHistogramInto(DoubleHistogram targetHistogram) {
        this.performIntervalSample();
        this.inactiveHistogram.copyInto(targetHistogram);
    }

    @Override
    public synchronized void reset() {
        this.performIntervalSample();
        this.performIntervalSample();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void performIntervalSample() {
        try {
            this.recordingPhaser.readerLock();
            if (this.inactiveHistogram == null) {
                if (this.activeHistogram instanceof InternalConcurrentDoubleHistogram) {
                    this.inactiveHistogram = new InternalConcurrentDoubleHistogram((InternalConcurrentDoubleHistogram)this.activeHistogram);
                } else if (this.activeHistogram instanceof PackedInternalConcurrentDoubleHistogram) {
                    this.inactiveHistogram = new PackedInternalConcurrentDoubleHistogram(this.instanceId, this.activeHistogram.getNumberOfSignificantValueDigits());
                } else {
                    throw new IllegalStateException("Unexpected internal histogram type for activeHistogram");
                }
            }
            this.inactiveHistogram.reset();
            ConcurrentDoubleHistogram tempHistogram = this.inactiveHistogram;
            this.inactiveHistogram = this.activeHistogram;
            this.activeHistogram = tempHistogram;
            long now = System.currentTimeMillis();
            this.activeHistogram.setStartTimeStamp(now);
            this.inactiveHistogram.setEndTimeStamp(now);
            this.recordingPhaser.flipPhase(500000L);
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    private void validateFitAsReplacementHistogram(DoubleHistogram replacementHistogram, boolean enforeContainingInstance) {
        boolean bad = true;
        if (replacementHistogram == null) {
            bad = false;
        } else if (replacementHistogram instanceof InternalConcurrentDoubleHistogram && (!enforeContainingInstance || ((InternalConcurrentDoubleHistogram)replacementHistogram).containingInstanceId == ((InternalConcurrentDoubleHistogram)this.activeHistogram).containingInstanceId)) {
            bad = false;
        } else if (replacementHistogram instanceof PackedInternalConcurrentDoubleHistogram && (!enforeContainingInstance || ((PackedInternalConcurrentDoubleHistogram)replacementHistogram).containingInstanceId == ((PackedInternalConcurrentDoubleHistogram)this.activeHistogram).containingInstanceId)) {
            bad = false;
        }
        if (bad) {
            throw new IllegalArgumentException("replacement histogram must have been obtained via a previous getIntervalHistogram() call from this " + this.getClass().getName() + " instance");
        }
    }

    private static class PackedInternalConcurrentDoubleHistogram
    extends PackedConcurrentDoubleHistogram {
        private final long containingInstanceId;

        private PackedInternalConcurrentDoubleHistogram(long id, int numberOfSignificantValueDigits) {
            super(numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }
    }

    private static class InternalConcurrentDoubleHistogram
    extends ConcurrentDoubleHistogram {
        private final long containingInstanceId;

        private InternalConcurrentDoubleHistogram(long id, int numberOfSignificantValueDigits) {
            super(numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }

        private InternalConcurrentDoubleHistogram(long id, long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
            super(highestToLowestValueRatio, numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }

        private InternalConcurrentDoubleHistogram(InternalConcurrentDoubleHistogram source) {
            super(source);
            this.containingInstanceId = source.containingInstanceId;
        }
    }
}

