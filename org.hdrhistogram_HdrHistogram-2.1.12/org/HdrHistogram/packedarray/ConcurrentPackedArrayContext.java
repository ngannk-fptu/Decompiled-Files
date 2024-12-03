/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongArray;
import org.HdrHistogram.packedarray.AbstractPackedArrayContext;
import org.HdrHistogram.packedarray.PackedArrayContext;

class ConcurrentPackedArrayContext
extends PackedArrayContext {
    private AtomicLongArray array;
    private volatile int populatedShortLength;
    private static final AtomicIntegerFieldUpdater<ConcurrentPackedArrayContext> populatedShortLengthUpdater = AtomicIntegerFieldUpdater.newUpdater(ConcurrentPackedArrayContext.class, "populatedShortLength");

    ConcurrentPackedArrayContext(int virtualLength, int initialPhysicalLength, boolean allocateArray) {
        super(virtualLength, initialPhysicalLength, false);
        if (allocateArray) {
            this.array = new AtomicLongArray(this.getPhysicalLength());
            this.init(virtualLength);
        }
    }

    ConcurrentPackedArrayContext(int virtualLength, int initialPhysicalLength) {
        this(virtualLength, initialPhysicalLength, true);
    }

    ConcurrentPackedArrayContext(int newVirtualCountsArraySize, AbstractPackedArrayContext from, int arrayLength) {
        this(newVirtualCountsArraySize, arrayLength);
        if (this.isPacked()) {
            this.populateEquivalentEntriesWithZerosFromOther(from);
        }
    }

    @Override
    int length() {
        return this.array.length();
    }

    @Override
    int getPopulatedShortLength() {
        return this.populatedShortLength;
    }

    @Override
    boolean casPopulatedShortLength(int expectedPopulatedShortLength, int newPopulatedShortLength) {
        return populatedShortLengthUpdater.compareAndSet(this, expectedPopulatedShortLength, newPopulatedShortLength);
    }

    @Override
    boolean casPopulatedLongLength(int expectedPopulatedLongLength, int newPopulatedLongLength) {
        int existingShortLength = this.getPopulatedShortLength();
        int existingLongLength = existingShortLength + 3 >> 2;
        if (existingLongLength != expectedPopulatedLongLength) {
            return false;
        }
        return this.casPopulatedShortLength(existingShortLength, newPopulatedLongLength << 2);
    }

    @Override
    long getAtLongIndex(int longIndex) {
        return this.array.get(longIndex);
    }

    @Override
    boolean casAtLongIndex(int longIndex, long expectedValue, long newValue) {
        return this.array.compareAndSet(longIndex, expectedValue, newValue);
    }

    @Override
    void lazySetAtLongIndex(int longIndex, long newValue) {
        this.array.lazySet(longIndex, newValue);
    }

    @Override
    void clearContents() {
        for (int i = 0; i < this.array.length(); ++i) {
            this.array.lazySet(i, 0L);
        }
        this.init(this.getVirtualLength());
    }

    @Override
    void resizeArray(int newLength) {
        AtomicLongArray newArray = new AtomicLongArray(newLength);
        int copyLength = Math.min(this.array.length(), newLength);
        for (int i = 0; i < copyLength; ++i) {
            newArray.lazySet(i, this.array.get(i));
        }
        this.array = newArray;
    }

    @Override
    long getAtUnpackedIndex(int index) {
        return this.array.get(index);
    }

    @Override
    void setAtUnpackedIndex(int index, long newValue) {
        this.array.set(index, newValue);
    }

    @Override
    void lazysetAtUnpackedIndex(int index, long newValue) {
        this.array.lazySet(index, newValue);
    }

    @Override
    long incrementAndGetAtUnpackedIndex(int index) {
        return this.array.incrementAndGet(index);
    }

    @Override
    long addAndGetAtUnpackedIndex(int index, long valueToAdd) {
        return this.array.addAndGet(index, valueToAdd);
    }

    @Override
    String unpackedToString() {
        return this.array.toString();
    }
}

