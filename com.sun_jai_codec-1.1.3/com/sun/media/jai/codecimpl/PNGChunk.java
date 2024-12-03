/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

class PNGChunk {
    int length;
    int type;
    byte[] data;
    int crc;
    String typeString;

    public PNGChunk(int length, int type, byte[] data, int crc) {
        this.length = length;
        this.type = type;
        this.data = data;
        this.crc = crc;
        this.typeString = new String();
        this.typeString = this.typeString + (char)(type >> 24);
        this.typeString = this.typeString + (char)(type >> 16 & 0xFF);
        this.typeString = this.typeString + (char)(type >> 8 & 0xFF);
        this.typeString = this.typeString + (char)(type & 0xFF);
    }

    public int getLength() {
        return this.length;
    }

    public int getType() {
        return this.type;
    }

    public String getTypeString() {
        return this.typeString;
    }

    public byte[] getData() {
        return this.data;
    }

    public byte getByte(int offset) {
        return this.data[offset];
    }

    public int getInt1(int offset) {
        return this.data[offset] & 0xFF;
    }

    public int getInt2(int offset) {
        return (this.data[offset] & 0xFF) << 8 | this.data[offset + 1] & 0xFF;
    }

    public int getInt4(int offset) {
        return (this.data[offset] & 0xFF) << 24 | (this.data[offset + 1] & 0xFF) << 16 | (this.data[offset + 2] & 0xFF) << 8 | this.data[offset + 3] & 0xFF;
    }

    public String getString4(int offset) {
        String s = new String();
        s = s + (char)this.data[offset];
        s = s + (char)this.data[offset + 1];
        s = s + (char)this.data[offset + 2];
        s = s + (char)this.data[offset + 3];
        return s;
    }

    public boolean isType(String typeName) {
        return this.typeString.equals(typeName);
    }
}

