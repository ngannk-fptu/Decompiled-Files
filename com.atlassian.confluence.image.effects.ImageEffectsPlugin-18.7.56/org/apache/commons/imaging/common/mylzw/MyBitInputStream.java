/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.mylzw;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class MyBitInputStream
extends InputStream {
    private final InputStream is;
    private final ByteOrder byteOrder;
    private boolean tiffLZWMode;
    private long bytesRead;
    private int bitsInCache;
    private int bitCache;

    public MyBitInputStream(InputStream is, ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        return this.readBits(8);
    }

    public void setTiffLZWMode() {
        this.tiffLZWMode = true;
    }

    public int readBits(int sampleBits) throws IOException {
        int sample;
        while (this.bitsInCache < sampleBits) {
            int next = this.is.read();
            if (next < 0) {
                if (this.tiffLZWMode) {
                    return 257;
                }
                return -1;
            }
            int newByte = 0xFF & next;
            this.bitCache = this.byteOrder == ByteOrder.BIG_ENDIAN ? this.bitCache << 8 | newByte : newByte << this.bitsInCache | this.bitCache;
            ++this.bytesRead;
            this.bitsInCache += 8;
        }
        int sampleMask = (1 << sampleBits) - 1;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            sample = sampleMask & this.bitCache >> this.bitsInCache - sampleBits;
        } else {
            sample = sampleMask & this.bitCache;
            this.bitCache >>= sampleBits;
        }
        int result = sample;
        this.bitsInCache -= sampleBits;
        int remainderMask = (1 << this.bitsInCache) - 1;
        this.bitCache &= remainderMask;
        return result;
    }

    public void flushCache() {
        this.bitsInCache = 0;
        this.bitCache = 0;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}

