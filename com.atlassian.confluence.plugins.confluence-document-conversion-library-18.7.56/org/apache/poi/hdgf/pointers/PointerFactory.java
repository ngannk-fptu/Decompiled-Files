/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.pointers;

import org.apache.poi.hdgf.exceptions.OldVisioFormatException;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.pointers.PointerV5;
import org.apache.poi.hdgf.pointers.PointerV6;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class PointerFactory {
    private static final int MAX_NUMBER_OF_POINTERS = 100000;
    private final int version;

    public PointerFactory(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public Pointer createPointer(byte[] data, int offset) {
        if (this.version >= 6) {
            PointerV6 p = new PointerV6();
            p.setType(LittleEndian.getInt(data, offset));
            p.setAddress((int)LittleEndian.getUInt(data, offset + 4));
            p.setOffset((int)LittleEndian.getUInt(data, offset + 8));
            p.setLength((int)LittleEndian.getUInt(data, offset + 12));
            p.setFormat(LittleEndian.getShort(data, offset + 16));
            return p;
        }
        if (this.version == 5) {
            PointerV5 p = new PointerV5();
            p.setType(LittleEndian.getShort(data, offset));
            p.setFormat(LittleEndian.getShort(data, offset + 2));
            p.setAddress((int)LittleEndian.getUInt(data, offset + 4));
            p.setOffset((int)LittleEndian.getUInt(data, offset + 8));
            p.setLength((int)LittleEndian.getUInt(data, offset + 12));
            return p;
        }
        throw new OldVisioFormatException("Visio files with versions below 5 are not supported, yours was " + this.version);
    }

    public Pointer[] createContainerPointers(Pointer parent, byte[] data) {
        int numPointersOffset = parent.getNumPointersOffset(data);
        int numPointers = parent.getNumPointers(numPointersOffset, data);
        int skip = parent.getPostNumPointersSkip();
        if (numPointers < 0) {
            throw new IllegalArgumentException("Cannot create container pointers with negative count: " + numPointers);
        }
        IOUtils.safelyAllocateCheck(numPointers, 100000);
        int pos = numPointersOffset + skip;
        Pointer[] childPointers = new Pointer[numPointers];
        for (int i = 0; i < numPointers; ++i) {
            childPointers[i] = this.createPointer(data, pos);
            pos += childPointers[i].getSizeInBytes();
        }
        return childPointers;
    }
}

