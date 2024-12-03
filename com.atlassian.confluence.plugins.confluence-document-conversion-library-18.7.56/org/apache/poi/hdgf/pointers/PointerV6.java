/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.pointers;

import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.util.LittleEndian;

public final class PointerV6
extends Pointer {
    @Override
    public boolean destinationHasStrings() {
        return this.isFormatBetween(64, 80);
    }

    @Override
    public boolean destinationHasPointers() {
        return this.getType() == 20 || this.isFormatBetween(29, 31) || this.isFormatBetween(80, 96);
    }

    @Override
    public boolean destinationHasChunks() {
        return this.isFormatBetween(208, 223);
    }

    @Override
    public boolean destinationCompressed() {
        return (this.getFormat() & 2) > 0;
    }

    @Override
    public int getSizeInBytes() {
        return 18;
    }

    @Override
    public int getNumPointersOffset(byte[] data) {
        return PointerV6.getNumPointersOffsetV6(data);
    }

    public static int getNumPointersOffsetV6(byte[] data) {
        return Math.toIntExact(LittleEndian.getUInt(data, 0));
    }

    @Override
    public int getNumPointers(int offset, byte[] data) {
        return PointerV6.getNumPointersV6(offset, data);
    }

    public static int getNumPointersV6(int offset, byte[] data) {
        return Math.toIntExact(LittleEndian.getUInt(data, offset));
    }

    @Override
    public int getPostNumPointersSkip() {
        return PointerV6.getPostNumPointersSkipV6();
    }

    public static int getPostNumPointersSkipV6() {
        return 8;
    }
}

