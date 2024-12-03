/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import org.HdrHistogram.packedarray.AbstractPackedArrayContext;
import org.HdrHistogram.packedarray.AbstractPackedLongArray;
import org.HdrHistogram.packedarray.IterationValue;
import org.HdrHistogram.packedarray.PackedArrayContext;

public class PackedLongArray
extends AbstractPackedLongArray {
    PackedLongArray() {
    }

    public PackedLongArray(int virtualLength) {
        this(virtualLength, 16);
    }

    public PackedLongArray(int virtualLength, int initialPhysicalLength) {
        this.setArrayContext(new PackedArrayContext(virtualLength, initialPhysicalLength));
    }

    @Override
    void resizeStorageArray(int newPhysicalLengthInLongs) {
        AbstractPackedArrayContext oldArrayContext = this.getArrayContext();
        PackedArrayContext newArrayContext = new PackedArrayContext(oldArrayContext.getVirtualLength(), oldArrayContext, newPhysicalLengthInLongs);
        this.setArrayContext(newArrayContext);
        for (IterationValue v : oldArrayContext.nonZeroValues()) {
            this.set(v.getIndex(), v.getValue());
        }
    }

    @Override
    public void setVirtualLength(int newVirtualArrayLength) {
        if (newVirtualArrayLength < this.length()) {
            throw new IllegalArgumentException("Cannot set virtual length, as requested length " + newVirtualArrayLength + " is smaller than the current virtual length " + this.length());
        }
        AbstractPackedArrayContext currentArrayContext = this.getArrayContext();
        if (currentArrayContext.isPacked() && currentArrayContext.determineTopLevelShiftForVirtualLength(newVirtualArrayLength) == currentArrayContext.getTopLevelShift()) {
            currentArrayContext.setVirtualLength(newVirtualArrayLength);
            return;
        }
        AbstractPackedArrayContext oldArrayContext = currentArrayContext;
        this.setArrayContext(new PackedArrayContext(newVirtualArrayLength, oldArrayContext, oldArrayContext.length()));
        for (IterationValue v : oldArrayContext.nonZeroValues()) {
            this.set(v.getIndex(), v.getValue());
        }
    }

    @Override
    public PackedLongArray copy() {
        PackedLongArray copy = new PackedLongArray(this.length(), this.getPhysicalLength());
        copy.add(this);
        return copy;
    }

    @Override
    void clearContents() {
        this.getArrayContext().clearContents();
    }

    @Override
    long criticalSectionEnter() {
        return 0L;
    }

    @Override
    void criticalSectionExit(long criticalValueAtEnter) {
    }
}

