/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.mylzw;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

public class MyBitOutputStream
extends OutputStream {
    private final OutputStream os;
    private final ByteOrder byteOrder;
    private int bitsInCache;
    private int bitCache;
    private int bytesWritten;

    public MyBitOutputStream(OutputStream os, ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        this.os = os;
    }

    @Override
    public void write(int value) throws IOException {
        this.writeBits(value, 8);
    }

    public void writeBits(int value, int sampleBits) throws IOException {
        int sampleMask = (1 << sampleBits) - 1;
        this.bitCache = this.byteOrder == ByteOrder.BIG_ENDIAN ? this.bitCache << sampleBits | value : (this.bitCache |= (value &= sampleMask) << this.bitsInCache);
        this.bitsInCache += sampleBits;
        while (this.bitsInCache >= 8) {
            int b;
            if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
                b = 0xFF & this.bitCache >> this.bitsInCache - 8;
                this.actualWrite(b);
                this.bitsInCache -= 8;
            } else {
                b = 0xFF & this.bitCache;
                this.actualWrite(b);
                this.bitCache >>= 8;
                this.bitsInCache -= 8;
            }
            int remainderMask = (1 << this.bitsInCache) - 1;
            this.bitCache &= remainderMask;
        }
    }

    private void actualWrite(int value) throws IOException {
        this.os.write(value);
        ++this.bytesWritten;
    }

    public void flushCache() throws IOException {
        if (this.bitsInCache > 0) {
            int bitMask = (1 << this.bitsInCache) - 1;
            int b = bitMask & this.bitCache;
            if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
                this.os.write(b <<= 8 - this.bitsInCache);
            } else {
                this.os.write(b);
            }
        }
        this.bitsInCache = 0;
        this.bitCache = 0;
    }

    public int getBytesWritten() {
        return this.bytesWritten + (this.bitsInCache > 0 ? 1 : 0);
    }
}

