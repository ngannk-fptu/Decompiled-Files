/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import java.util.concurrent.atomic.AtomicLong;
import org.HdrHistogram.WriterReaderPhaser;
import org.HdrHistogram.packedarray.PackedLongArray;

public class PackedArraySingleWriterRecorder {
    private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
    private final long instanceId = instanceIdSequencer.getAndIncrement();
    private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
    private volatile PackedLongArray activeArray;

    public PackedArraySingleWriterRecorder(int virtualLength) {
        this.activeArray = new InternalPackedLongArray(this.instanceId, virtualLength);
        this.activeArray.setStartTimeStamp(System.currentTimeMillis());
    }

    public PackedArraySingleWriterRecorder(int virtualLength, int initialPhysicalLength) {
        this.activeArray = new InternalPackedLongArray(this.instanceId, virtualLength, initialPhysicalLength);
        this.activeArray.setStartTimeStamp(System.currentTimeMillis());
    }

    public int length() {
        return this.activeArray.length();
    }

    public void setVirtualLength(int newVirtualLength) {
        try {
            this.recordingPhaser.readerLock();
            this.activeArray.setVirtualLength(newVirtualLength);
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void increment(int index) throws ArrayIndexOutOfBoundsException {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.activeArray.increment(index);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(int index, long valueToAdd) throws ArrayIndexOutOfBoundsException {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.activeArray.add(index, valueToAdd);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    public synchronized PackedLongArray getIntervalArray() {
        return this.getIntervalArray(null);
    }

    public synchronized PackedLongArray getIntervalArray(PackedLongArray arrayToRecycle) {
        return this.getIntervalArray(arrayToRecycle, true);
    }

    public synchronized PackedLongArray getIntervalArray(PackedLongArray arrayToRecycle, boolean enforeContainingInstance) {
        this.validateFitAsReplacementArray(arrayToRecycle, enforeContainingInstance);
        PackedLongArray sampledArray = this.performIntervalSample(arrayToRecycle);
        return sampledArray;
    }

    public synchronized void reset() {
        this.performIntervalSample(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private PackedLongArray performIntervalSample(PackedLongArray arrayToRecycle) {
        PackedLongArray inactiveArray = arrayToRecycle;
        try {
            this.recordingPhaser.readerLock();
            if (inactiveArray == null) {
                if (!(this.activeArray instanceof InternalPackedLongArray)) throw new IllegalStateException("Unexpected internal array type for activeArray");
                inactiveArray = new InternalPackedLongArray(this.instanceId, this.activeArray.length());
            } else {
                inactiveArray.clear();
            }
            PackedLongArray tempArray = inactiveArray;
            inactiveArray = this.activeArray;
            this.activeArray = tempArray;
            long now = System.currentTimeMillis();
            this.activeArray.setStartTimeStamp(now);
            inactiveArray.setEndTimeStamp(now);
            this.recordingPhaser.flipPhase(500000L);
            return inactiveArray;
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    private void validateFitAsReplacementArray(PackedLongArray replacementArray, boolean enforeContainingInstance) {
        boolean bad = true;
        if (replacementArray == null) {
            bad = false;
        } else if (replacementArray instanceof InternalPackedLongArray && this.activeArray instanceof InternalPackedLongArray && (!enforeContainingInstance || ((InternalPackedLongArray)replacementArray).containingInstanceId == ((InternalPackedLongArray)this.activeArray).containingInstanceId)) {
            bad = false;
        }
        if (bad) {
            throw new IllegalArgumentException("replacement array must have been obtained via a previous getIntervalArray() call from this " + this.getClass().getName() + (enforeContainingInstance ? " insatnce" : " class"));
        }
    }

    private static class InternalPackedLongArray
    extends PackedLongArray {
        private final long containingInstanceId;

        private InternalPackedLongArray(long id, int virtualLength, int initialPhysicalLength) {
            super(virtualLength, initialPhysicalLength);
            this.containingInstanceId = id;
        }

        private InternalPackedLongArray(long id, int virtualLength) {
            super(virtualLength);
            this.containingInstanceId = id;
        }
    }
}

