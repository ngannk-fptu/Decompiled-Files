/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.png;

class PNGChunk {
    int length;
    int type;
    byte[] data;
    int crc;
    final String typeString;

    PNGChunk(int length, int type, byte[] data, int crc) {
        this.length = length;
        this.type = type;
        this.data = data;
        this.crc = crc;
        this.typeString = "" + (char)(type >>> 24 & 0xFF) + (char)(type >>> 16 & 0xFF) + (char)(type >>> 8 & 0xFF) + (char)(type & 0xFF);
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
        return "" + (char)this.data[offset] + (char)this.data[offset + 1] + (char)this.data[offset + 2] + (char)this.data[offset + 3];
    }

    public boolean isType(String typeName) {
        return this.typeString.equals(typeName);
    }
}

