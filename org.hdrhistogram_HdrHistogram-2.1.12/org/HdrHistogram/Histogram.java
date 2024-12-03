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

public class Histogram
extends AbstractHistogram {
    long totalCount;
    long[] counts;
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
        int n = this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
        this.counts[n] = this.counts[n] + 1L;
    }

    @Override
    void addToCountAtIndex(int index, long value) {
        int n = this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
        this.counts[n] = this.counts[n] + value;
    }

    @Override
    void setCountAtIndex(int index, long value) {
        this.counts[this.normalizeIndex((int)index, (int)this.normalizingIndexOffset, (int)this.countsArrayLength)] = value;
    }

    @Override
    void setCountAtNormalizedIndex(int index, long value) {
        this.counts[index] = value;
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
        Arrays.fill(this.counts, 0L);
        this.totalCount = 0L;
    }

    @Override
    public Histogram copy() {
        Histogram copy = new Histogram(this);
        copy.add(this);
        return copy;
    }

    @Override
    public Histogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        Histogram copy = new Histogram(this);
        copy.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return copy;
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
        return 512 + 8 * this.counts.length;
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
            Arrays.fill(this.counts, oldNormalizedZeroIndex, newNormalizedZeroIndex, 0L);
        }
    }

    public Histogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public Histogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public Histogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, true);
    }

    public Histogram(AbstractHistogram source) {
        this(source, true);
    }

    Histogram(AbstractHistogram source, boolean allocateCountsArray) {
        super(source);
        if (allocateCountsArray) {
            this.counts = new long[this.countsArrayLength];
        }
        this.wordSizeInBytes = 8;
    }

    Histogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits, boolean allocateCountsArray) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
        if (allocateCountsArray) {
            this.counts = new long[this.countsArrayLength];
        }
        this.wordSizeInBytes = 8;
    }

    public static Histogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return Histogram.decodeFromByteBuffer(buffer, Histogram.class, minBarForHighestTrackableValue);
    }

    public static Histogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return Histogram.decodeFromCompressedByteBuffer(buffer, Histogram.class, minBarForHighestTrackableValue);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
    }

    public static Histogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return Histogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
    }
}

