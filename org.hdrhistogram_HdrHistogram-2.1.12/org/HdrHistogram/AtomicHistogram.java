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

public class AtomicHistogram
extends Histogram {
    static final AtomicLongFieldUpdater<AtomicHistogram> totalCountUpdater = AtomicLongFieldUpdater.newUpdater(AtomicHistogram.class, "totalCount");
    volatile long totalCount;
    volatile AtomicLongArray counts;

    @Override
    long getCountAtIndex(int index) {
        return this.counts.get(index);
    }

    @Override
    long getCountAtNormalizedIndex(int index) {
        return this.counts.get(index);
    }

    @Override
    void incrementCountAtIndex(int index) {
        this.counts.getAndIncrement(index);
    }

    @Override
    void addToCountAtIndex(int index, long value) {
        this.counts.getAndAdd(index, value);
    }

    @Override
    void setCountAtIndex(int index, long value) {
        this.counts.lazySet(index, value);
    }

    @Override
    void setCountAtNormalizedIndex(int index, long value) {
        this.counts.lazySet(index, value);
    }

    @Override
    int getNormalizingIndexOffset() {
        return 0;
    }

    @Override
    void setNormalizingIndexOffset(int normalizingIndexOffset) {
        if (normalizingIndexOffset != 0) {
            throw new IllegalStateException("AtomicHistogram does not support non-zero normalizing index settings. Use ConcurrentHistogram Instead.");
        }
    }

    @Override
    void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated, double newIntegerToDoubleValueConversionRatio) {
        throw new IllegalStateException("AtomicHistogram does not support Shifting operations. Use ConcurrentHistogram Instead.");
    }

    @Override
    void resize(long newHighestTrackableValue) {
        throw new IllegalStateException("AtomicHistogram does not support resizing operations. Use ConcurrentHistogram Instead.");
    }

    @Override
    public void setAutoResize(boolean autoResize) {
        throw new IllegalStateException("AtomicHistogram does not support AutoResize operation. Use ConcurrentHistogram Instead.");
    }

    @Override
    public boolean supportsAutoResize() {
        return false;
    }

    @Override
    void clearCounts() {
        for (int i = 0; i < this.counts.length(); ++i) {
            this.counts.lazySet(i, 0L);
        }
        totalCountUpdater.set(this, 0L);
    }

    @Override
    public AtomicHistogram copy() {
        AtomicHistogram copy = new AtomicHistogram(this);
        copy.add(this);
        return copy;
    }

    @Override
    public AtomicHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        AtomicHistogram toHistogram = new AtomicHistogram(this);
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
        return 512 + 8 * this.counts.length();
    }

    public AtomicHistogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public AtomicHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, false);
        this.counts = new AtomicLongArray(this.countsArrayLength);
        this.wordSizeInBytes = 8;
    }

    public AtomicHistogram(AbstractHistogram source) {
        super(source, false);
        this.counts = new AtomicLongArray(this.countsArrayLength);
        this.wordSizeInBytes = 8;
    }

    public static AtomicHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return AtomicHistogram.decodeFromByteBuffer(buffer, AtomicHistogram.class, minBarForHighestTrackableValue);
    }

    public static AtomicHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return AtomicHistogram.decodeFromCompressedByteBuffer(buffer, AtomicHistogram.class, minBarForHighestTrackableValue);
    }

    public static AtomicHistogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return AtomicHistogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
    }
}

