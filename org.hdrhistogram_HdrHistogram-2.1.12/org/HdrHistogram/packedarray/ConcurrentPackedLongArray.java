/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.HdrHistogram.WriterReaderPhaser;
import org.HdrHistogram.packedarray.AbstractPackedArrayContext;
import org.HdrHistogram.packedarray.ConcurrentPackedArrayContext;
import org.HdrHistogram.packedarray.IterationValue;
import org.HdrHistogram.packedarray.PackedLongArray;

public class ConcurrentPackedLongArray
extends PackedLongArray {
    transient WriterReaderPhaser wrp = new WriterReaderPhaser();

    public ConcurrentPackedLongArray(int virtualLength) {
        this(virtualLength, 16);
    }

    public ConcurrentPackedLongArray(int virtualLength, int initialPhysicalLength) {
        this.setArrayContext(new ConcurrentPackedArrayContext(virtualLength, initialPhysicalLength));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void resizeStorageArray(int newPhysicalLengthInLongs) {
        AbstractPackedArrayContext inactiveArrayContext;
        try {
            this.wrp.readerLock();
            ConcurrentPackedArrayContext newArrayContext = new ConcurrentPackedArrayContext(this.getArrayContext().getVirtualLength(), this.getArrayContext(), newPhysicalLengthInLongs);
            inactiveArrayContext = this.getArrayContext();
            this.setArrayContext(newArrayContext);
            this.wrp.flipPhase();
        }
        finally {
            this.wrp.readerUnlock();
        }
        for (IterationValue v : inactiveArrayContext.nonZeroValues()) {
            this.add(v.getIndex(), v.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVirtualLength(int newVirtualArrayLength) {
        AbstractPackedArrayContext inactiveArrayContext;
        if (newVirtualArrayLength < this.length()) {
            throw new IllegalArgumentException("Cannot set virtual length, as requested length " + newVirtualArrayLength + " is smaller than the current virtual length " + this.length());
        }
        try {
            this.wrp.readerLock();
            AbstractPackedArrayContext currentArrayContext = this.getArrayContext();
            if (currentArrayContext.isPacked() && currentArrayContext.determineTopLevelShiftForVirtualLength(newVirtualArrayLength) == currentArrayContext.getTopLevelShift()) {
                currentArrayContext.setVirtualLength(newVirtualArrayLength);
                return;
            }
            inactiveArrayContext = currentArrayContext;
            this.setArrayContext(new ConcurrentPackedArrayContext(newVirtualArrayLength, inactiveArrayContext, inactiveArrayContext.length()));
            this.wrp.flipPhase();
        }
        finally {
            this.wrp.readerUnlock();
        }
        for (IterationValue v : inactiveArrayContext.nonZeroValues()) {
            this.add(v.getIndex(), v.getValue());
        }
    }

    @Override
    public ConcurrentPackedLongArray copy() {
        ConcurrentPackedLongArray copy = new ConcurrentPackedLongArray(this.length(), this.getPhysicalLength());
        copy.add(this);
        return copy;
    }

    @Override
    void clearContents() {
        try {
            this.wrp.readerLock();
            this.getArrayContext().clearContents();
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    @Override
    long criticalSectionEnter() {
        return this.wrp.writerCriticalSectionEnter();
    }

    @Override
    void criticalSectionExit(long criticalValueAtEnter) {
        this.wrp.writerCriticalSectionExit(criticalValueAtEnter);
    }

    @Override
    public String toString() {
        try {
            this.wrp.readerLock();
            String string = super.toString();
            return string;
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    @Override
    public void clear() {
        try {
            this.wrp.readerLock();
            super.clear();
        }
        finally {
            this.wrp.readerUnlock();
        }
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
        this.wrp = new WriterReaderPhaser();
    }
}

