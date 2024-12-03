/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.nio.charset.Charset;
import org.apache.poi.hdgf.chunks.ChunkHeaderV11;
import org.apache.poi.hdgf.chunks.ChunkHeaderV4V5;
import org.apache.poi.hdgf.chunks.ChunkHeaderV6;
import org.apache.poi.hdgf.exceptions.OldVisioFormatException;
import org.apache.poi.util.LittleEndian;

public abstract class ChunkHeader {
    private int type;
    private int id;
    private int length;
    private int unknown1;

    public static ChunkHeader createChunkHeader(int documentVersion, byte[] data, int offset) {
        if (documentVersion >= 6) {
            ChunkHeaderV6 ch = documentVersion > 6 ? new ChunkHeaderV11() : new ChunkHeaderV6();
            ch.setType((int)LittleEndian.getUInt(data, offset));
            ch.setId((int)LittleEndian.getUInt(data, offset + 4));
            ch.setUnknown1((int)LittleEndian.getUInt(data, offset + 8));
            ch.setLength((int)LittleEndian.getUInt(data, offset + 12));
            ch.setUnknown2(LittleEndian.getShort(data, offset + 16));
            ch.setUnknown3(LittleEndian.getUByte(data, offset + 18));
            return ch;
        }
        if (documentVersion == 5 || documentVersion == 4) {
            ChunkHeaderV4V5 ch = new ChunkHeaderV4V5();
            ch.setType(LittleEndian.getShort(data, offset));
            ch.setId(LittleEndian.getShort(data, offset + 2));
            ch.setUnknown2(LittleEndian.getUByte(data, offset + 4));
            ch.setUnknown3(LittleEndian.getUByte(data, offset + 5));
            ch.setUnknown1(LittleEndian.getShort(data, offset + 6));
            ch.setLength(Math.toIntExact(LittleEndian.getUInt(data, offset + 8)));
            return ch;
        }
        throw new OldVisioFormatException("Visio files with versions below 4 are not supported, yours was " + documentVersion);
    }

    public static int getHeaderSize(int documentVersion) {
        return documentVersion >= 6 ? ChunkHeaderV6.getHeaderSize() : ChunkHeaderV4V5.getHeaderSize();
    }

    public abstract int getSizeInBytes();

    public abstract boolean hasTrailer();

    public abstract boolean hasSeparator();

    public abstract Charset getChunkCharset();

    public int getId() {
        return this.id;
    }

    public int getLength() {
        return this.length;
    }

    public int getType() {
        return this.type;
    }

    public int getUnknown1() {
        return this.unknown1;
    }

    void setType(int type) {
        this.type = type;
    }

    void setId(int id) {
        this.id = id;
    }

    void setLength(int length) {
        this.length = length;
    }

    void setUnknown1(int unknown1) {
        this.unknown1 = unknown1;
    }
}

