/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.packedarray.PackedLongArray;

public class PackedHistogram
extends Histogram {
    private PackedLongArray packedCounts;

    @Override
    long getCountAtIndex(int index) {
        return this.getCountAtNormalizedIndex(this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength));
    }

    @Override
    long getCountAtNormalizedIndex(int index) {
        long count = this.packedCounts.get(index);
        return count;
    }

    @Override
    void incrementCountAtIndex(int index) {
        this.packedCounts.increment(this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength));
    }

    @Override
    void addToCountAtIndex(int index, long value) {
        this.packedCounts.add(this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength), value);
    }

    @Override
    void setCountAtIndex(int index, long value) {
        this.setCountAtNormalizedIndex(this.normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength), value);
    }

    @Override
    void setCountAtNormalizedIndex(int index, long value) {
        this.packedCounts.set(index, value);
    }

    @Override
    void clearCounts() {
        this.packedCounts.clear();
        this.packedCounts.setVirtualLength(this.countsArrayLength);
        this.totalCount = 0L;
    }

    @Override
    public PackedHistogram copy() {
        PackedHistogram copy = new PackedHistogram(this);
        copy.add(this);
        return copy;
    }

    @Override
    public PackedHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        PackedHistogram toHistogram = new PackedHistogram(this);
        toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return toHistogram;
    }

    @Override
    void resize(long newHighestTrackableValue) {
        int oldNormalizedZeroIndex = this.normalizeIndex(0, this.normalizingIndexOffset, this.countsArrayLength);
        int oldCountsArrayLength = this.countsArrayLength;
        this.establishSize(newHighestTrackableValue);
        if (oldNormalizedZeroIndex != 0) {
            PackedLongArray newPackedCounts = new PackedLongArray(this.countsArrayLength, this.packedCounts.getPhysicalLength());
            for (int fromIndex = 0; fromIndex < oldNormalizedZeroIndex; ++fromIndex) {
                long value = this.packedCounts.get(fromIndex);
                if (value == 0L) continue;
                newPackedCounts.set(fromIndex, value);
            }
            int countsDelta = this.countsArrayLength - oldCountsArrayLength;
            for (int fromIndex = oldNormalizedZeroIndex; fromIndex < oldCountsArrayLength; ++fromIndex) {
                long value = this.packedCounts.get(fromIndex);
                if (value == 0L) continue;
                int toIndex = fromIndex + countsDelta;
                newPackedCounts.set(toIndex, value);
            }
            this.packedCounts = newPackedCounts;
        } else {
            this.packedCounts.setVirtualLength(this.countsArrayLength);
        }
    }

    @Override
    int _getEstimatedFootprintInBytes() {
        return 192 + 8 * this.packedCounts.getPhysicalLength();
    }

    public PackedHistogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public PackedHistogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public PackedHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, false);
        this.packedCounts = new PackedLongArray(this.countsArrayLength);
        this.wordSizeInBytes = 8;
    }

    public PackedHistogram(AbstractHistogram source) {
        super(source, false);
        this.packedCounts = new PackedLongArray(this.countsArrayLength);
        this.wordSizeInBytes = 8;
    }

    public static PackedHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return PackedHistogram.decodeFromByteBuffer(buffer, PackedHistogram.class, minBarForHighestTrackableValue);
    }

    public static PackedHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return PackedHistogram.decodeFromCompressedByteBuffer(buffer, PackedHistogram.class, minBarForHighestTrackableValue);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
    }
}

