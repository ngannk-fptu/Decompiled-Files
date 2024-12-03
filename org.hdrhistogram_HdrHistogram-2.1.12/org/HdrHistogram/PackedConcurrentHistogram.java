/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.Base64Helper;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.WriterReaderPhaser;
import org.HdrHistogram.packedarray.ConcurrentPackedLongArray;

public class PackedConcurrentHistogram
extends ConcurrentHistogram {
    @Override
    ConcurrentHistogram.ConcurrentArrayWithNormalizingOffset allocateArray(int length, int normalizingIndexOffset) {
        return new ConcurrentPackedArrayWithNormalizingOffset(length, normalizingIndexOffset);
    }

    @Override
    void clearCounts() {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            for (int i = 0; i < this.activeCounts.length(); ++i) {
                this.activeCounts.lazySet(i, 0L);
                this.inactiveCounts.lazySet(i, 0L);
            }
            totalCountUpdater.set(this, 0L);
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    @Override
    public PackedConcurrentHistogram copy() {
        PackedConcurrentHistogram copy = new PackedConcurrentHistogram(this);
        copy.add(this);
        return copy;
    }

    @Override
    public PackedConcurrentHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        PackedConcurrentHistogram toHistogram = new PackedConcurrentHistogram(this);
        toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return toHistogram;
    }

    @Override
    public long getTotalCount() {
        return totalCountUpdater.get(this);
    }

    @Override
    void setTotalCount(long totalCount) {
        totalCountUpdater.set(this, totalCount);
    }

    @Override
    void incrementTotalCount() {
        totalCountUpdater.incrementAndGet(this);
    }

    @Override
    void addToTotalCount(long value) {
        totalCountUpdater.addAndGet(this, value);
    }

    @Override
    int _getEstimatedFootprintInBytes() {
        try {
            this.wrp.readerLock();
            int n = 128 + this.activeCounts.getEstimatedFootprintInBytes() + this.inactiveCounts.getEstimatedFootprintInBytes();
            return n;
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    public PackedConcurrentHistogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public PackedConcurrentHistogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public PackedConcurrentHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, true);
    }

    public PackedConcurrentHistogram(AbstractHistogram source) {
        this(source, true);
    }

    PackedConcurrentHistogram(AbstractHistogram source, boolean allocateCountsArray) {
        super(source, false);
        if (allocateCountsArray) {
            this.activeCounts = new ConcurrentPackedArrayWithNormalizingOffset(this.countsArrayLength, 0);
            this.inactiveCounts = new ConcurrentPackedArrayWithNormalizingOffset(this.countsArrayLength, 0);
        }
        this.wordSizeInBytes = 8;
    }

    PackedConcurrentHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits, boolean allocateCountsArray) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, false);
        if (allocateCountsArray) {
            this.activeCounts = new ConcurrentPackedArrayWithNormalizingOffset(this.countsArrayLength, 0);
            this.inactiveCounts = new ConcurrentPackedArrayWithNormalizingOffset(this.countsArrayLength, 0);
        }
        this.wordSizeInBytes = 8;
    }

    public static PackedConcurrentHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return PackedConcurrentHistogram.decodeFromByteBuffer(buffer, PackedConcurrentHistogram.class, minBarForHighestTrackableValue);
    }

    public static PackedConcurrentHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return PackedConcurrentHistogram.decodeFromCompressedByteBuffer(buffer, PackedConcurrentHistogram.class, minBarForHighestTrackableValue);
    }

    public static PackedConcurrentHistogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return PackedConcurrentHistogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
        this.wrp = new WriterReaderPhaser();
    }

    @Override
    synchronized void fillBufferFromCountsArray(ByteBuffer buffer) {
        try {
            this.wrp.readerLock();
            super.fillBufferFromCountsArray(buffer);
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    static class ConcurrentPackedArrayWithNormalizingOffset
    implements ConcurrentHistogram.ConcurrentArrayWithNormalizingOffset,
    Serializable {
        private ConcurrentPackedLongArray packedCounts;
        private int normalizingIndexOffset;
        private double doubleToIntegerValueConversionRatio;

        ConcurrentPackedArrayWithNormalizingOffset(int length, int normalizingIndexOffset) {
            this.packedCounts = new ConcurrentPackedLongArray(length);
            this.normalizingIndexOffset = normalizingIndexOffset;
        }

        @Override
        public int getNormalizingIndexOffset() {
            return this.normalizingIndexOffset;
        }

        @Override
        public void setNormalizingIndexOffset(int normalizingIndexOffset) {
            this.normalizingIndexOffset = normalizingIndexOffset;
        }

        @Override
        public double getDoubleToIntegerValueConversionRatio() {
            return this.doubleToIntegerValueConversionRatio;
        }

        @Override
        public void setDoubleToIntegerValueConversionRatio(double doubleToIntegerValueConversionRatio) {
            this.doubleToIntegerValueConversionRatio = doubleToIntegerValueConversionRatio;
        }

        @Override
        public long get(int index) {
            return this.packedCounts.get(index);
        }

        @Override
        public void atomicIncrement(int index) {
            this.packedCounts.increment(index);
        }

        @Override
        public void atomicAdd(int index, long valueToAdd) {
            this.packedCounts.add(index, valueToAdd);
        }

        @Override
        public void lazySet(int index, long newValue) {
            this.packedCounts.set(index, newValue);
        }

        @Override
        public int length() {
            return this.packedCounts.length();
        }

        @Override
        public int getEstimatedFootprintInBytes() {
            return 128 + 8 * this.packedCounts.getPhysicalLength();
        }
    }
}

