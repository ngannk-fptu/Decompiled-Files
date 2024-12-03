/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.pointers;

import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.util.LittleEndian;

public final class PointerV5
extends Pointer {
    @Override
    public boolean destinationHasStrings() {
        return this.isFormatBetween(64, 80);
    }

    @Override
    public boolean destinationHasPointers() {
        switch (this.getType()) {
            case 20: {
                return true;
            }
            case 22: {
                return false;
            }
        }
        return this.isFormatBetween(29, 31) || this.isFormatBetween(80, 96);
    }

    @Override
    public boolean destinationHasChunks() {
        switch (this.getType()) {
            case 21: {
                return true;
            }
            case 24: {
                return true;
            }
        }
        return this.isFormatBetween(208, 223);
    }

    @Override
    public boolean destinationCompressed() {
        return (this.getFormat() & 2) > 0;
    }

    @Override
    public int getSizeInBytes() {
        return 16;
    }

    @Override
    public int getNumPointersOffset(byte[] data) {
        switch (this.getType()) {
            case 29: 
            case 78: {
                return 30;
            }
            case 30: {
                return 54;
            }
            case 20: {
                return 130;
            }
        }
        return 10;
    }

    @Override
    public int getNumPointers(int offset, byte[] data) {
        return LittleEndian.getShort(data, offset);
    }

    @Override
    public int getPostNumPointersSkip() {
        return 2;
    }
}

