/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import java.util.Arrays;
import org.HdrHistogram.packedarray.AbstractPackedArrayContext;

class PackedArrayContext
extends AbstractPackedArrayContext {
    private long[] array;
    private int populatedShortLength = 0;

    PackedArrayContext(int virtualLength, int initialPhysicalLength, boolean allocateArray) {
        super(virtualLength, initialPhysicalLength);
        if (allocateArray) {
            this.array = new long[this.getPhysicalLength()];
            this.init(virtualLength);
        }
    }

    PackedArrayContext(int virtualLength, int initialPhysicalLength) {
        this(virtualLength, initialPhysicalLength, true);
    }

    PackedArrayContext(int virtualLength, AbstractPackedArrayContext from, int newPhysicalArrayLength) {
        this(virtualLength, newPhysicalArrayLength);
        if (this.isPacked()) {
            this.populateEquivalentEntriesWithZerosFromOther(from);
        }
    }

    @Override
    int length() {
        return this.array.length;
    }

    @Override
    int getPopulatedShortLength() {
        return this.populatedShortLength;
    }

    @Override
    boolean casPopulatedShortLength(int expectedPopulatedShortLength, int newPopulatedShortLength) {
        if (this.populatedShortLength != expectedPopulatedShortLength) {
            return false;
        }
        this.populatedShortLength = newPopulatedShortLength;
        return true;
    }

    @Override
    boolean casPopulatedLongLength(int expectedPopulatedLongLength, int newPopulatedLongLength) {
        if (this.getPopulatedLongLength() != expectedPopulatedLongLength) {
            return false;
        }
        return this.casPopulatedShortLength(this.populatedShortLength, newPopulatedLongLength << 2);
    }

    @Override
    long getAtLongIndex(int longIndex) {
        return this.array[longIndex];
    }

    @Override
    boolean casAtLongIndex(int longIndex, long expectedValue, long newValue) {
        if (this.array[longIndex] != expectedValue) {
            return false;
        }
        this.array[longIndex] = newValue;
        return true;
    }

    @Override
    void lazySetAtLongIndex(int longIndex, long newValue) {
        this.array[longIndex] = newValue;
    }

    @Override
    void clearContents() {
        Arrays.fill(this.array, 0L);
        this.init(this.getVirtualLength());
    }

    @Override
    void resizeArray(int newLength) {
        this.array = Arrays.copyOf(this.array, newLength);
    }

    @Override
    long getAtUnpackedIndex(int index) {
        return this.array[index];
    }

    @Override
    void setAtUnpackedIndex(int index, long newValue) {
        this.array[index] = newValue;
    }

    @Override
    void lazysetAtUnpackedIndex(int index, long newValue) {
        this.array[index] = newValue;
    }

    @Override
    long incrementAndGetAtUnpackedIndex(int index) {
        int n = index;
        this.array[n] = this.array[n] + 1L;
        return this.array[index];
    }

    @Override
    long addAndGetAtUnpackedIndex(int index, long valueToAdd) {
        int n = index;
        this.array[n] = this.array[n] + valueToAdd;
        return this.array[index];
    }

    @Override
    String unpackedToString() {
        return Arrays.toString(this.array);
    }
}

