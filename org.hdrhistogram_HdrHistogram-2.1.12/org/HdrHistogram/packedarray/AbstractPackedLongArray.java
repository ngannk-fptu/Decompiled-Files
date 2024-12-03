/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.HdrHistogram.packedarray.AbstractPackedArrayContext;
import org.HdrHistogram.packedarray.IterationValue;
import org.HdrHistogram.packedarray.ResizeException;

abstract class AbstractPackedLongArray
implements Iterable<Long>,
Serializable {
    private static final int NUMBER_OF_SETS = 8;
    private AbstractPackedArrayContext arrayContext;
    private long startTimeStampMsec = Long.MAX_VALUE;
    private long endTimeStampMsec = 0L;
    static final int NUMBER_OF_NON_ZEROS_TO_HASH = 8;

    AbstractPackedLongArray() {
    }

    AbstractPackedArrayContext getArrayContext() {
        return this.arrayContext;
    }

    void setArrayContext(AbstractPackedArrayContext newArrayContext) {
        this.arrayContext = newArrayContext;
    }

    public long getStartTimeStamp() {
        return this.startTimeStampMsec;
    }

    public void setStartTimeStamp(long timeStampMsec) {
        this.startTimeStampMsec = timeStampMsec;
    }

    public long getEndTimeStamp() {
        return this.endTimeStampMsec;
    }

    public void setEndTimeStamp(long timeStampMsec) {
        this.endTimeStampMsec = timeStampMsec;
    }

    public abstract void setVirtualLength(int var1);

    public abstract AbstractPackedLongArray copy();

    abstract void resizeStorageArray(int var1);

    abstract void clearContents();

    abstract long criticalSectionEnter();

    abstract void criticalSectionExit(long var1);

    public String toString() {
        String output = "PackedArray:\n";
        AbstractPackedArrayContext arrayContext = this.getArrayContext();
        output = output + arrayContext.toString();
        return output;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long get(int index) {
        long value = 0L;
        for (int byteNum = 0; byteNum < 8; ++byteNum) {
            int packedIndex = 0;
            long byteValueAtPackedIndex = 0L;
            do {
                int newArraySize = 0;
                long criticalValue = this.criticalSectionEnter();
                try {
                    AbstractPackedArrayContext arrayContext = this.getArrayContext();
                    if (!arrayContext.isPacked()) {
                        long l = arrayContext.getAtUnpackedIndex(index);
                        return l;
                    }
                    packedIndex = arrayContext.getPackedIndex(byteNum, index, false);
                    if (packedIndex < 0) {
                        long l = value;
                        return l;
                    }
                    byteValueAtPackedIndex = ((long)arrayContext.getAtByteIndex(packedIndex) & 0xFFL) << (byteNum << 3);
                }
                catch (ResizeException ex) {
                    newArraySize = ex.getNewSize();
                }
                finally {
                    this.criticalSectionExit(criticalValue);
                    if (newArraySize != 0) {
                        this.resizeStorageArray(newArraySize);
                    }
                }
            } while (packedIndex == 0);
            value += byteValueAtPackedIndex;
        }
        return value;
    }

    public void increment(int index) {
        this.add(index, 1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(int index, long value) {
        if (value == 0L) {
            return;
        }
        long remainingValueToAdd = value;
        while (true) {
            try {
                long byteMask = 255L;
                int byteNum = 0;
                int byteShift = 0;
                while (byteNum < 8) {
                    long criticalValue = this.criticalSectionEnter();
                    try {
                        AbstractPackedArrayContext arrayContext = this.getArrayContext();
                        if (!arrayContext.isPacked()) {
                            arrayContext.addAndGetAtUnpackedIndex(index, remainingValueToAdd);
                            return;
                        }
                        int packedIndex = arrayContext.getPackedIndex(byteNum, index, true);
                        long amountToAddAtSet = remainingValueToAdd & byteMask;
                        byte byteToAdd = (byte)(amountToAddAtSet >> byteShift);
                        long afterAddByteValue = arrayContext.addAtByteIndex(packedIndex, byteToAdd);
                        remainingValueToAdd -= amountToAddAtSet;
                        long carryAmount = afterAddByteValue & 0x100L;
                        if ((remainingValueToAdd += carryAmount << byteShift) == 0L) {
                            return;
                        }
                    }
                    finally {
                        this.criticalSectionExit(criticalValue);
                    }
                    ++byteNum;
                    byteShift += 8;
                    byteMask <<= 8;
                }
                return;
            }
            catch (ResizeException ex) {
                this.resizeStorageArray(ex.getNewSize());
                continue;
            }
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int index, long value) {
        int bytesAlreadySet = 0;
        while (true) {
            long valueForNextLevels = value;
            try {
                for (int byteNum = 0; byteNum < 8; ++byteNum) {
                    long criticalValue = this.criticalSectionEnter();
                    try {
                        int packedIndex;
                        AbstractPackedArrayContext arrayContext = this.getArrayContext();
                        if (!arrayContext.isPacked()) {
                            arrayContext.setAtUnpackedIndex(index, value);
                            return;
                        }
                        if (valueForNextLevels == 0L && (packedIndex = arrayContext.getPackedIndex(byteNum, index, false)) < 0) {
                            return;
                        }
                        packedIndex = arrayContext.getPackedIndex(byteNum, index, true);
                        byte byteToWrite = (byte)(valueForNextLevels & 0xFFL);
                        valueForNextLevels >>= 8;
                        if (byteNum < bytesAlreadySet) continue;
                        arrayContext.setAtByteIndex(packedIndex, byteToWrite);
                        ++bytesAlreadySet;
                        continue;
                    }
                    finally {
                        this.criticalSectionExit(criticalValue);
                    }
                }
                return;
            }
            catch (ResizeException ex) {
                this.resizeStorageArray(ex.getNewSize());
                continue;
            }
            break;
        }
    }

    public void add(AbstractPackedLongArray other) {
        for (IterationValue v : other.nonZeroValues()) {
            this.add(v.getIndex(), v.getValue());
        }
    }

    public void clear() {
        this.clearContents();
    }

    public int getPhysicalLength() {
        return this.getArrayContext().length();
    }

    public int length() {
        return this.getArrayContext().getVirtualLength();
    }

    @Override
    public Iterator<Long> iterator() {
        return new AllValuesIterator();
    }

    public Iterable<IterationValue> nonZeroValues() {
        return this.getArrayContext().nonZeroValues();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPackedLongArray)) {
            return false;
        }
        AbstractPackedLongArray that = (AbstractPackedLongArray)other;
        if (this.length() != that.length()) {
            return false;
        }
        if (this.arrayContext.isPacked() || that.arrayContext.isPacked()) {
            for (IterationValue v : this.nonZeroValues()) {
                if (that.get(v.getIndex()) == v.getValue()) continue;
                return false;
            }
            for (IterationValue v : that.nonZeroValues()) {
                if (this.get(v.getIndex()) == v.getValue()) continue;
                return false;
            }
        } else {
            for (int i = 0; i < this.length(); ++i) {
                if (this.get(i) == that.get(i)) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = this.oneAtATimeHashStep(h, this.length());
        int count = 0;
        for (IterationValue v : this.nonZeroValues()) {
            if (++count > 8) break;
            h = this.oneAtATimeHashStep(h, v.getIndex());
            h = this.oneAtATimeHashStep(h, (int)v.getValue());
        }
        h += h << 3;
        h ^= h >> 11;
        h += h << 15;
        return h;
    }

    private int oneAtATimeHashStep(int incomingHash, int v) {
        int h = incomingHash;
        h += v;
        h += h << 10;
        h ^= h >> 6;
        return h;
    }

    class AllValuesIterator
    implements Iterator<Long> {
        int nextVirtrualIndex = 0;

        AllValuesIterator() {
        }

        @Override
        public Long next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return AbstractPackedLongArray.this.get(this.nextVirtrualIndex++);
        }

        @Override
        public boolean hasNext() {
            return this.nextVirtrualIndex >= 0 && this.nextVirtrualIndex < AbstractPackedLongArray.this.length();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

