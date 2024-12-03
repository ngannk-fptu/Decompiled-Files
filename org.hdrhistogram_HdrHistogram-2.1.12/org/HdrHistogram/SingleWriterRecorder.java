/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.concurrent.atomic.AtomicLong;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.PackedHistogram;
import org.HdrHistogram.ValueRecorder;
import org.HdrHistogram.WriterReaderPhaser;

public class SingleWriterRecorder
implements ValueRecorder {
    private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
    private final long instanceId = instanceIdSequencer.getAndIncrement();
    private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
    private volatile Histogram activeHistogram;
    private Histogram inactiveHistogram;

    public SingleWriterRecorder(int numberOfSignificantValueDigits, boolean packed) {
        this.activeHistogram = packed ? new PackedInternalHistogram(this.instanceId, numberOfSignificantValueDigits) : new InternalHistogram(this.instanceId, numberOfSignificantValueDigits);
        this.inactiveHistogram = null;
        this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
    }

    public SingleWriterRecorder(int numberOfSignificantValueDigits) {
        this(numberOfSignificantValueDigits, false);
    }

    public SingleWriterRecorder(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public SingleWriterRecorder(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        this.activeHistogram = new InternalHistogram(this.instanceId, lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
        this.inactiveHistogram = null;
        this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordValue(long value) {
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
    public void recordValueWithCount(long value, long count) throws ArrayIndexOutOfBoundsException {
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
    public void recordValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.activeHistogram.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    public synchronized Histogram getIntervalHistogram() {
        return this.getIntervalHistogram(null);
    }

    public synchronized Histogram getIntervalHistogram(Histogram histogramToRecycle) {
        return this.getIntervalHistogram(histogramToRecycle, true);
    }

    public synchronized Histogram getIntervalHistogram(Histogram histogramToRecycle, boolean enforeContainingInstance) {
        this.validateFitAsReplacementHistogram(histogramToRecycle, enforeContainingInstance);
        this.inactiveHistogram = histogramToRecycle;
        this.performIntervalSample();
        Histogram sampledHistogram = this.inactiveHistogram;
        this.inactiveHistogram = null;
        return sampledHistogram;
    }

    public synchronized void getIntervalHistogramInto(Histogram targetHistogram) {
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
                if (this.activeHistogram instanceof InternalHistogram) {
                    this.inactiveHistogram = new InternalHistogram((InternalHistogram)this.activeHistogram);
                } else if (this.activeHistogram instanceof PackedInternalHistogram) {
                    this.inactiveHistogram = new PackedInternalHistogram(this.instanceId, this.activeHistogram.getNumberOfSignificantValueDigits());
                } else {
                    throw new IllegalStateException("Unexpected internal histogram type for activeHistogram");
                }
            }
            this.inactiveHistogram.reset();
            Histogram tempHistogram = this.inactiveHistogram;
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

    private void validateFitAsReplacementHistogram(Histogram replacementHistogram, boolean enforeContainingInstance) {
        boolean bad = true;
        if (replacementHistogram == null) {
            bad = false;
        } else if (replacementHistogram instanceof InternalHistogram && (!enforeContainingInstance || ((InternalHistogram)replacementHistogram).containingInstanceId == ((InternalHistogram)this.activeHistogram).containingInstanceId)) {
            bad = false;
        } else if (replacementHistogram instanceof PackedInternalHistogram && (!enforeContainingInstance || ((PackedInternalHistogram)replacementHistogram).containingInstanceId == ((PackedInternalHistogram)this.activeHistogram).containingInstanceId)) {
            bad = false;
        }
        if (bad) {
            throw new IllegalArgumentException("replacement histogram must have been obtained via a previous getIntervalHistogram() call from this " + this.getClass().getName() + (enforeContainingInstance ? " insatnce" : " class"));
        }
    }

    private static class PackedInternalHistogram
    extends PackedHistogram {
        private final long containingInstanceId;

        private PackedInternalHistogram(long id, int numberOfSignificantValueDigits) {
            super(numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }
    }

    private static class InternalHistogram
    extends Histogram {
        private final long containingInstanceId;

        private InternalHistogram(long id, int numberOfSignificantValueDigits) {
            super(numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }

        private InternalHistogram(long id, long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
            super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
            this.containingInstanceId = id;
        }

        private InternalHistogram(InternalHistogram source) {
            super(source);
            this.containingInstanceId = source.containingInstanceId;
        }
    }
}

