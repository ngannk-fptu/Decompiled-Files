/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.concurrent.atomic.AtomicLong;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleValueRecorder;
import org.HdrHistogram.PackedDoubleHistogram;
import org.HdrHistogram.WriterReaderPhaser;

public class SingleWriterDoubleRecorder
implements DoubleValueRecorder {
    private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
    private final long instanceId = instanceIdSequencer.getAndIncrement();
    private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
    private volatile DoubleHistogram activeHistogram;
    private DoubleHistogram inactiveHistogram;

    public SingleWriterDoubleRecorder(int numberOfSignificantValueDigits, boolean packed) {
        this.activeHistogram = packed ? new PackedInternalDoubleHistogram(this.instanceId, numberOfSignificantValueDigits) : new InternalDoubleHistogram(this.instanceId, numberOfSignificantValueDigits);
        this.inactiveHistogram = null;
        this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
    }

    public SingleWriterDoubleRecorder(int numberOfSignificantValueDigits) {
        this(numberOfSignificantValueDigits, false);
    }

    public SingleWriterDoubleRecorder(long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
        this.activeHistogram = new InternalDoubleHistogram(this.instanceId, highestToLowestValueRatio, numberOfSignificantValueDigits);
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
        this.inactiveHistogram = histogramToRecycle;
        this.performIntervalSample();
        DoubleHistogram sampledHistogram = this.inactiveHistogram;
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
                if (this.activeHistogram instanceof InternalDoubleHistogram) {
                    this.inactiveHistogram = new InternalDoubleHistogram((InternalDoubleHistogram)this.activeHistogram);
                } else if (this.activeHistogram instanceof PackedInternalDoubleHistogram) {
                    this.inactiveHistogram = new PackedInternalDoubleHistogram(this.instanceId, this.activeHistogram.getNumberOfSignificantValueDigits());
                } else {
                    throw new IllegalStateException("Unexpected internal histogram type for activeHistogram");
                }
            }
            this.inactiveHistogram.reset();
            DoubleHistogram tempHistogram = this.inactiveHistogram;
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
        } else if (replacementHistogram instanceof InternalDoubleHistogram && (!enforeContainingInstance || ((InternalDoubleHistogram)replacementHistogram).containingInstanceId == ((InternalDoubleHistogram)this.activeHistogram).containingInstanceId)) {
            bad = false;
        } else if (replacementHistogram instanceof PackedInternalDoubleHistogram && (!enforeContainingInstance || ((PackedInternalDoubleHistogram)replacementHistogram).containingInstanceId == ((PackedInternalDoubleHistogram)this.activeHistogram).containingInstanceId)) {
            bad = false;
        }
        if (bad) {
            throw new IllegalArgumentException("replacement histogram must have been obtained via a previous getIntervalHistogram() call from this " + this.getClass().getName() + " instance");
        }
    }

    private class PackedInternalDoubleHistogram
    extends PackedDoubleHistogram {
        private final long containingInstanceId;

        private PackedInternalDoubleHistogram(long id, int numberOfSignificantValueDigits) {
            super(numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }
    }

    private class InternalDoubleHistogram
    extends DoubleHistogram {
        private final long containingInstanceId;

        private InternalDoubleHistogram(long id, int numberOfSignificantValueDigits) {
            super(numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }

        private InternalDoubleHistogram(long id, long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
            super(highestToLowestValueRatio, numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }

        private InternalDoubleHistogram(InternalDoubleHistogram source) {
            super(source);
            this.containingInstanceId = source.containingInstanceId;
        }
    }
}

