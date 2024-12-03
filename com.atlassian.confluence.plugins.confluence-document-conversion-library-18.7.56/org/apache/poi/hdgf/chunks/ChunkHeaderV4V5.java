/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.poi.hdgf.chunks.ChunkHeader;

public final class ChunkHeaderV4V5
extends ChunkHeader {
    private short unknown2;
    private short unknown3;

    public short getUnknown2() {
        return this.unknown2;
    }

    public short getUnknown3() {
        return this.unknown3;
    }

    protected static int getHeaderSize() {
        return 12;
    }

    @Override
    public int getSizeInBytes() {
        return ChunkHeaderV4V5.getHeaderSize();
    }

    @Override
    public boolean hasTrailer() {
        return false;
    }

    @Override
    public boolean hasSeparator() {
        return false;
    }

    @Override
    public Charset getChunkCharset() {
        return StandardCharsets.US_ASCII;
    }

    void setUnknown2(short unknown2) {
        this.unknown2 = unknown2;
    }

    void setUnknown3(short unknown3) {
        this.unknown3 = unknown3;
    }
}

