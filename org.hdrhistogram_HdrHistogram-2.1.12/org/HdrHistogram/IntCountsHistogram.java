/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.Base64Helper;

public class IntCountsHistogram
extends AbstractHistogram {
    long totalCount;
    int[] counts;
    int normalizingIndexOffset;

    @Override
    long getCountAtIndex(int index) {
        return this.counts[this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength)];
    }

    @Override
    long getCountAtNormalizedIndex(int index) {
        return this.counts[index];
    }

    @Override
    void incrementCountAtIndex(int index) {
        int normalizedIndex = this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
        int currentCount = this.counts[normalizedIndex];
        int newCount = currentCount + 1;
        if (newCount < 0) {
            throw new IllegalStateException("would overflow integer count");
        }
        this.counts[normalizedIndex] = newCount;
    }

    @Override
    void addToCountAtIndex(int index, long value) {
        int normalizedIndex = this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
        long currentCount = this.counts[normalizedIndex];
        long newCount = currentCount + value;
        if (newCount < Integer.MIN_VALUE || newCount > Integer.MAX_VALUE) {
            throw new IllegalStateException("would overflow integer count");
        }
        this.counts[normalizedIndex] = (int)newCount;
    }

    @Override
    void setCountAtIndex(int index, long value) {
        this.setCountAtNormalizedIndex(this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength), value);
    }

    @Override
    void setCountAtNormalizedIndex(int index, long value) {
        if (value < 0L || value > Integer.MAX_VALUE) {
            throw new IllegalStateException("would overflow integer count");
        }
        this.counts[index] = (int)value;
    }

    @Override
    int getNormalizingIndexOffset() {
        return this.normalizingIndexOffset;
    }

    @Override
    void setNormalizingIndexOffset(int normalizingIndexOffset) {
        this.normalizingIndexOffset = normalizingIndexOffset;
    }

    @Override
    void setIntegerToDoubleValueConversionRatio(double integerToDoubleValueConversionRatio) {
        this.nonConcurrentSetIntegerToDoubleValueConversionRatio(integerToDoubleValueConversionRatio);
    }

    @Override
    void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated, double newIntegerToDoubleValueConversionRatio) {
        this.nonConcurrentNormalizingIndexShift(offsetToAdd, lowestHalfBucketPopulated);
    }

    @Override
    void clearCounts() {
        Arrays.fill(this.counts, 0);
        this.totalCount = 0L;
    }

    @Override
    public IntCountsHistogram copy() {
        IntCountsHistogram copy = new IntCountsHistogram(this);
        copy.add(this);
        return copy;
    }

    @Override
    public IntCountsHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        IntCountsHistogram toHistogram = new IntCountsHistogram(this);
        toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return toHistogram;
    }

    @Override
    public long getTotalCount() {
        return this.totalCount;
    }

    @Override
    void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    void incrementTotalCount() {
        ++this.totalCount;
    }

    @Override
    void addToTotalCount(long value) {
        this.totalCount += value;
    }

    @Override
    int _getEstimatedFootprintInBytes() {
        return 512 + 4 * this.counts.length;
    }

    @Override
    void resize(long newHighestTrackableValue) {
        int oldNormalizedZeroIndex = this.normalizeIndex(0, this.normalizingIndexOffset, this.countsArrayLength);
        this.establishSize(newHighestTrackableValue);
        int countsDelta = this.countsArrayLength - this.counts.length;
        this.counts = Arrays.copyOf(this.counts, this.countsArrayLength);
        if (oldNormalizedZeroIndex != 0) {
            int newNormalizedZeroIndex = oldNormalizedZeroIndex + countsDelta;
            int lengthToCopy = this.countsArrayLength - countsDelta - oldNormalizedZeroIndex;
            System.arraycopy(this.counts, oldNormalizedZeroIndex, this.counts, newNormalizedZeroIndex, lengthToCopy);
            Arrays.fill(this.counts, oldNormalizedZeroIndex, newNormalizedZeroIndex, 0);
        }
    }

    public IntCountsHistogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public IntCountsHistogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public IntCountsHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
        this.counts = new int[this.countsArrayLength];
        this.wordSizeInBytes = 4;
    }

    public IntCountsHistogram(AbstractHistogram source) {
        super(source);
        this.counts = new int[this.countsArrayLength];
        this.wordSizeInBytes = 4;
    }

    public static IntCountsHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return IntCountsHistogram.decodeFromByteBuffer(buffer, IntCountsHistogram.class, minBarForHighestTrackableValue);
    }

    public static IntCountsHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return IntCountsHistogram.decodeFromCompressedByteBuffer(buffer, IntCountsHistogram.class, minBarForHighestTrackableValue);
    }

    public static IntCountsHistogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return IntCountsHistogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
    }
}

