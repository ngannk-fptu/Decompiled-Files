/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.ByteArrayInputStream;
import org.apache.commons.imaging.common.BinaryFileParser;

public class PngChunk
extends BinaryFileParser {
    public final int length;
    public final int chunkType;
    public final int crc;
    private final byte[] bytes;
    private final boolean[] propertyBits;
    public final boolean ancillary;
    public final boolean isPrivate;
    public final boolean reserved;
    public final boolean safeToCopy;

    public PngChunk(int length, int chunkType, int crc, byte[] bytes) {
        this.length = length;
        this.chunkType = chunkType;
        this.crc = crc;
        this.bytes = (byte[])bytes.clone();
        this.propertyBits = new boolean[4];
        int shift = 24;
        for (int i = 0; i < 4; ++i) {
            int theByte = 0xFF & chunkType >> shift;
            shift -= 8;
            int theMask = 32;
            this.propertyBits[i] = (theByte & 0x20) > 0;
        }
        this.ancillary = this.propertyBits[0];
        this.isPrivate = this.propertyBits[1];
        this.reserved = this.propertyBits[2];
        this.safeToCopy = this.propertyBits[3];
    }

    public byte[] getBytes() {
        return (byte[])this.bytes.clone();
    }

    public boolean[] getPropertyBits() {
        return (boolean[])this.propertyBits.clone();
    }

    protected ByteArrayInputStream getDataStream() {
        return new ByteArrayInputStream(this.getBytes());
    }
}

