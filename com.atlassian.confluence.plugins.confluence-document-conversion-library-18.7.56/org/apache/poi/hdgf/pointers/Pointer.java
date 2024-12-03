/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.pointers;

public abstract class Pointer {
    private int type;
    private int address;
    private int offset;
    private int length;
    private short format;

    public int getAddress() {
        return this.address;
    }

    public short getFormat() {
        return this.format;
    }

    public int getLength() {
        return this.length;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getType() {
        return this.type;
    }

    public abstract int getSizeInBytes();

    public abstract int getNumPointersOffset(byte[] var1);

    public abstract int getNumPointers(int var1, byte[] var2);

    public abstract int getPostNumPointersSkip();

    public abstract boolean destinationHasStrings();

    public abstract boolean destinationHasPointers();

    public abstract boolean destinationHasChunks();

    public abstract boolean destinationCompressed();

    protected void setType(int type) {
        this.type = type;
    }

    protected void setAddress(int address) {
        this.address = address;
    }

    protected void setOffset(int offset) {
        this.offset = offset;
    }

    protected void setLength(int length) {
        this.length = length;
    }

    protected void setFormat(short format) {
        this.format = format;
    }

    protected boolean isFormatBetween(int min, int max) {
        return min <= this.format && this.format < max;
    }
}

