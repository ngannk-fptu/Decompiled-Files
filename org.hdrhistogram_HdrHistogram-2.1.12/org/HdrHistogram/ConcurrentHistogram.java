/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.Base64Helper;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.WriterReaderPhaser;

public class ConcurrentHistogram
extends Histogram {
    static final AtomicLongFieldUpdater<ConcurrentHistogram> totalCountUpdater = AtomicLongFieldUpdater.newUpdater(ConcurrentHistogram.class, "totalCount");
    volatile long totalCount;
    volatile ConcurrentArrayWithNormalizingOffset activeCounts;
    volatile ConcurrentArrayWithNormalizingOffset inactiveCounts;
    transient WriterReaderPhaser wrp = new WriterReaderPhaser();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void setIntegerToDoubleValueConversionRatio(double integerToDoubleValueConversionRatio) {
        try {
            this.wrp.readerLock();
            this.inactiveCounts.setDoubleToIntegerValueConversionRatio(1.0 / integerToDoubleValueConversionRatio);
            ConcurrentArrayWithNormalizingOffset tmp = this.activeCounts;
            this.activeCounts = this.inactiveCounts;
            this.inactiveCounts = tmp;
            this.wrp.flipPhase();
            this.inactiveCounts.setDoubleToIntegerValueConversionRatio(1.0 / integerToDoubleValueConversionRatio);
            tmp = this.activeCounts;
            this.activeCounts = this.inactiveCounts;
            this.inactiveCounts = tmp;
            this.wrp.flipPhase();
        }
        finally {
            this.wrp.readerUnlock();
        }
        super.setIntegerToDoubleValueConversionRatio(integerToDoubleValueConversionRatio);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    long getCountAtIndex(int index) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            long activeCount = this.activeCounts.get(this.normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()));
            long inactiveCount = this.inactiveCounts.get(this.normalizeIndex(index, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length()));
            long l = activeCount + inactiveCount;
            return l;
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    long getCountAtNormalizedIndex(int index) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            long activeCount = this.activeCounts.get(index);
            long inactiveCount = this.inactiveCounts.get(index);
            long l = activeCount + inactiveCount;
            return l;
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void incrementCountAtIndex(int index) {
        long criticalValue = this.wrp.writerCriticalSectionEnter();
        try {
            this.activeCounts.atomicIncrement(this.normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()));
        }
        finally {
            this.wrp.writerCriticalSectionExit(criticalValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void addToCountAtIndex(int index, long value) {
        long criticalValue = this.wrp.writerCriticalSectionEnter();
        try {
            this.activeCounts.atomicAdd(this.normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()), value);
        }
        finally {
            this.wrp.writerCriticalSectionExit(criticalValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void setCountAtIndex(int index, long value) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            this.activeCounts.lazySet(this.normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()), value);
            this.inactiveCounts.lazySet(this.normalizeIndex(index, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length()), 0L);
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void setCountAtNormalizedIndex(int index, long value) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            this.inactiveCounts.lazySet(index, value);
            this.activeCounts.lazySet(index, 0L);
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void recordConvertedDoubleValue(double value) {
        long criticalValue = this.wrp.writerCriticalSectionEnter();
        try {
            long integerValue = (long)(value * this.activeCounts.getDoubleToIntegerValueConversionRatio());
            int index = this.countsArrayIndex(integerValue);
            this.activeCounts.atomicIncrement(this.normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()));
            this.updateMinAndMax(integerValue);
            this.incrementTotalCount();
        }
        finally {
            this.wrp.writerCriticalSectionExit(criticalValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordConvertedDoubleValueWithCount(double value, long count) throws ArrayIndexOutOfBoundsException {
        long criticalValue = this.wrp.writerCriticalSectionEnter();
        try {
            long integerValue = (long)(value * this.activeCounts.getDoubleToIntegerValueConversionRatio());
            int index = this.countsArrayIndex(integerValue);
            this.activeCounts.atomicAdd(this.normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()), count);
            this.updateMinAndMax(integerValue);
            this.addToTotalCount(count);
        }
        finally {
            this.wrp.writerCriticalSectionExit(criticalValue);
        }
    }

    @Override
    int getNormalizingIndexOffset() {
        return this.activeCounts.getNormalizingIndexOffset();
    }

    @Override
    void setNormalizingIndexOffset(int normalizingIndexOffset) {
        this.setNormalizingIndexOffset(normalizingIndexOffset, 0, false, this.getIntegerToDoubleValueConversionRatio());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setNormalizingIndexOffset(int newNormalizingIndexOffset, int shiftedAmount, boolean lowestHalfBucketPopulated, double newIntegerToDoubleValueConversionRatio) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            assert (this.activeCounts.getNormalizingIndexOffset() == this.inactiveCounts.getNormalizingIndexOffset());
            if (newNormalizingIndexOffset == this.activeCounts.getNormalizingIndexOffset()) {
                return;
            }
            this.setNormalizingIndexOffsetForInactive(newNormalizingIndexOffset, shiftedAmount, lowestHalfBucketPopulated, newIntegerToDoubleValueConversionRatio);
            ConcurrentArrayWithNormalizingOffset tmp = this.activeCounts;
            this.activeCounts = this.inactiveCounts;
            this.inactiveCounts = tmp;
            this.wrp.flipPhase();
            this.setNormalizingIndexOffsetForInactive(newNormalizingIndexOffset, shiftedAmount, lowestHalfBucketPopulated, newIntegerToDoubleValueConversionRatio);
            tmp = this.activeCounts;
            this.activeCounts = this.inactiveCounts;
            this.inactiveCounts = tmp;
            this.wrp.flipPhase();
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    private void setNormalizingIndexOffsetForInactive(int newNormalizingIndexOffset, int shiftedAmount, boolean lowestHalfBucketPopulated, double newIntegerToDoubleValueConversionRatio) {
        int zeroIndex = this.normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
        long inactiveZeroValueCount = this.inactiveCounts.get(zeroIndex);
        this.inactiveCounts.lazySet(zeroIndex, 0L);
        this.inactiveCounts.setNormalizingIndexOffset(newNormalizingIndexOffset);
        if (shiftedAmount > 0 && lowestHalfBucketPopulated) {
            this.shiftLowestInactiveHalfBucketContentsLeft(shiftedAmount, zeroIndex);
        }
        zeroIndex = this.normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
        this.inactiveCounts.lazySet(zeroIndex, inactiveZeroValueCount);
        this.inactiveCounts.setDoubleToIntegerValueConversionRatio(1.0 / newIntegerToDoubleValueConversionRatio);
    }

    private void shiftLowestInactiveHalfBucketContentsLeft(int shiftAmount, int preShiftZeroIndex) {
        int numberOfBinaryOrdersOfMagnitude = shiftAmount >> this.subBucketHalfCountMagnitude;
        for (int fromIndex = 1; fromIndex < this.subBucketHalfCount; ++fromIndex) {
            long toValue = this.valueFromIndex(fromIndex) << numberOfBinaryOrdersOfMagnitude;
            int toIndex = this.countsArrayIndex(toValue);
            int normalizedToIndex = this.normalizeIndex(toIndex, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
            long countAtFromIndex = this.inactiveCounts.get(fromIndex + preShiftZeroIndex);
            this.inactiveCounts.lazySet(normalizedToIndex, countAtFromIndex);
            this.inactiveCounts.lazySet(fromIndex + preShiftZeroIndex, 0L);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated, double newIntegerToDoubleValueConversionRatio) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            int newNormalizingIndexOffset = this.getNormalizingIndexOffset() + offsetToAdd;
            this.setNormalizingIndexOffset(newNormalizingIndexOffset, offsetToAdd, lowestHalfBucketPopulated, newIntegerToDoubleValueConversionRatio);
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    ConcurrentArrayWithNormalizingOffset allocateArray(int length, int normalizingIndexOffset) {
        return new AtomicLongArrayWithNormalizingOffset(length, normalizingIndexOffset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void resize(long newHighestTrackableValue) {
        try {
            this.wrp.readerLock();
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
            int newArrayLength = this.determineArrayLengthNeeded(newHighestTrackableValue);
            int countsDelta = newArrayLength - this.countsArrayLength;
            if (countsDelta <= 0) {
                return;
            }
            ConcurrentArrayWithNormalizingOffset newInactiveCounts1 = this.allocateArray(newArrayLength, this.inactiveCounts.getNormalizingIndexOffset());
            ConcurrentArrayWithNormalizingOffset newInactiveCounts2 = this.allocateArray(newArrayLength, this.activeCounts.getNormalizingIndexOffset());
            ConcurrentArrayWithNormalizingOffset oldInactiveCounts = this.inactiveCounts;
            this.inactiveCounts = newInactiveCounts1;
            this.copyInactiveCountsContentsOnResize(oldInactiveCounts, countsDelta);
            ConcurrentArrayWithNormalizingOffset tmp = this.activeCounts;
            this.activeCounts = this.inactiveCounts;
            this.inactiveCounts = tmp;
            this.wrp.flipPhase();
            oldInactiveCounts = this.inactiveCounts;
            this.inactiveCounts = newInactiveCounts2;
            this.copyInactiveCountsContentsOnResize(oldInactiveCounts, countsDelta);
            tmp = this.activeCounts;
            this.activeCounts = this.inactiveCounts;
            this.inactiveCounts = tmp;
            this.wrp.flipPhase();
            this.establishSize(newHighestTrackableValue);
            assert (this.countsArrayLength == this.activeCounts.length());
            assert (this.countsArrayLength == this.inactiveCounts.length());
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    void copyInactiveCountsContentsOnResize(ConcurrentArrayWithNormalizingOffset oldInactiveCounts, int countsDelta) {
        int oldNormalizedZeroIndex = this.normalizeIndex(0, oldInactiveCounts.getNormalizingIndexOffset(), oldInactiveCounts.length());
        if (oldNormalizedZeroIndex == 0) {
            for (int i = 0; i < oldInactiveCounts.length(); ++i) {
                this.inactiveCounts.lazySet(i, oldInactiveCounts.get(i));
            }
        } else {
            int fromIndex;
            for (fromIndex = 0; fromIndex < oldNormalizedZeroIndex; ++fromIndex) {
                this.inactiveCounts.lazySet(fromIndex, oldInactiveCounts.get(fromIndex));
            }
            for (fromIndex = oldNormalizedZeroIndex; fromIndex < oldInactiveCounts.length(); ++fromIndex) {
                int toIndex = fromIndex + countsDelta;
                this.inactiveCounts.lazySet(toIndex, oldInactiveCounts.get(fromIndex));
            }
        }
    }

    @Override
    public void setAutoResize(boolean autoResize) {
        this.autoResize = true;
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
    public ConcurrentHistogram copy() {
        ConcurrentHistogram copy = new ConcurrentHistogram(this);
        copy.add(this);
        return copy;
    }

    @Override
    public ConcurrentHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        ConcurrentHistogram toHistogram = new ConcurrentHistogram(this);
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
        return 512 + 16 * this.activeCounts.length();
    }

    public ConcurrentHistogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public ConcurrentHistogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public ConcurrentHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, true);
    }

    public ConcurrentHistogram(AbstractHistogram source) {
        this(source, true);
    }

    ConcurrentHistogram(AbstractHistogram source, boolean allocateCountsArray) {
        super(source, false);
        if (allocateCountsArray) {
            this.activeCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
            this.inactiveCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
        }
        this.wordSizeInBytes = 8;
    }

    ConcurrentHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits, boolean allocateCountsArray) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, false);
        if (allocateCountsArray) {
            this.activeCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
            this.inactiveCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
        }
        this.wordSizeInBytes = 8;
    }

    public static ConcurrentHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return ConcurrentHistogram.decodeFromByteBuffer(buffer, ConcurrentHistogram.class, minBarForHighestTrackableValue);
    }

    public static ConcurrentHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return ConcurrentHistogram.decodeFromCompressedByteBuffer(buffer, ConcurrentHistogram.class, minBarForHighestTrackableValue);
    }

    public static ConcurrentHistogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return ConcurrentHistogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
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

    static class AtomicLongArrayWithNormalizingOffset
    extends AtomicLongArray
    implements ConcurrentArrayWithNormalizingOffset {
        private int normalizingIndexOffset;
        private double doubleToIntegerValueConversionRatio;

        AtomicLongArrayWithNormalizingOffset(int length, int normalizingIndexOffset) {
            super(length);
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
        public int getEstimatedFootprintInBytes() {
            return 256 + 8 * this.length();
        }

        @Override
        public void atomicIncrement(int index) {
            this.incrementAndGet(index);
        }

        @Override
        public void atomicAdd(int index, long valueToAdd) {
            this.addAndGet(index, valueToAdd);
        }
    }

    static interface ConcurrentArrayWithNormalizingOffset {
        public int getNormalizingIndexOffset();

        public void setNormalizingIndexOffset(int var1);

        public double getDoubleToIntegerValueConversionRatio();

        public void setDoubleToIntegerValueConversionRatio(double var1);

        public int getEstimatedFootprintInBytes();

        public long get(int var1);

        public void atomicIncrement(int var1);

        public void atomicAdd(int var1, long var2);

        public void lazySet(int var1, long var2);

        public int length();
    }
}

