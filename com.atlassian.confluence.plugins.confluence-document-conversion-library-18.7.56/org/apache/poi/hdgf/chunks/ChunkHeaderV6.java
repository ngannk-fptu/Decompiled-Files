/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.poi.hdgf.chunks.ChunkHeader;

public class ChunkHeaderV6
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
        return 19;
    }

    @Override
    public int getSizeInBytes() {
        return ChunkHeaderV6.getHeaderSize();
    }

    @Override
    public boolean hasTrailer() {
        switch (this.getType()) {
            case 44: 
            case 101: 
            case 102: 
            case 105: 
            case 106: 
            case 107: 
            case 112: 
            case 113: {
                return true;
            }
        }
        return this.getUnknown1() != 0;
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

