/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.io.enc.Encoder
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.io.enc.Encoder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class LZWEncoder
implements Encoder {
    static final int CLEAR_CODE = 256;
    static final int EOI_CODE = 257;
    private static final int MIN_BITS = 9;
    private static final int MAX_BITS = 12;
    private static final int TABLE_SIZE = 4096;
    private final short[] CHILDREN = new short[4096];
    private final short[] SIBLINGS = new short[4096];
    private final short[] SUFFIXES = new short[4096];
    private int parent = -1;
    private int bitsPerCode = 9;
    private int nextValidCode = 258;
    private int maxCode = LZWEncoder.maxValue(this.bitsPerCode);
    private int bits = 0;
    private int bitPos = 0;
    private long remaining;

    LZWEncoder(long l) {
        this.remaining = l;
    }

    public void encode(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        this.encodeBytes(outputStream, byteBuffer);
        if (this.remaining <= 0L) {
            this.writeCode(outputStream, this.parent);
            this.writeCode(outputStream, 257);
            if (this.bitPos > 0) {
                this.writeCode(outputStream, 0);
            }
        }
    }

    void encodeBytes(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        int n = byteBuffer.remaining();
        if (n == 0) {
            return;
        }
        if (this.parent == -1) {
            this.writeCode(outputStream, 256);
            this.parent = byteBuffer.get() & 0xFF;
        }
        block0: while (byteBuffer.hasRemaining()) {
            int n2 = byteBuffer.get() & 0xFF;
            int n3 = this.CHILDREN[this.parent];
            if (n3 > 0) {
                if (this.SUFFIXES[n3] == n2) {
                    this.parent = n3;
                    continue;
                }
                int n4 = n3;
                while (this.SIBLINGS[n4] > 0) {
                    if (this.SUFFIXES[n4 = this.SIBLINGS[n4]] != n2) continue;
                    this.parent = n4;
                    continue block0;
                }
                this.SIBLINGS[n4] = (short)this.nextValidCode;
                this.SUFFIXES[this.nextValidCode] = (short)n2;
                this.writeCode(outputStream, this.parent);
                this.parent = n2;
                ++this.nextValidCode;
                this.increaseCodeSizeOrResetIfNeeded(outputStream);
                continue;
            }
            this.CHILDREN[this.parent] = (short)this.nextValidCode;
            this.SUFFIXES[this.nextValidCode] = (short)n2;
            this.writeCode(outputStream, this.parent);
            this.parent = n2;
            ++this.nextValidCode;
            this.increaseCodeSizeOrResetIfNeeded(outputStream);
        }
        this.remaining -= (long)n;
    }

    private void increaseCodeSizeOrResetIfNeeded(OutputStream outputStream) throws IOException {
        if (this.nextValidCode > this.maxCode) {
            if (this.bitsPerCode == 12) {
                this.writeCode(outputStream, 256);
                this.resetTables();
            } else {
                ++this.bitsPerCode;
                this.maxCode = LZWEncoder.maxValue(this.bitsPerCode);
            }
        }
    }

    private void resetTables() {
        Arrays.fill(this.CHILDREN, (short)0);
        Arrays.fill(this.SIBLINGS, (short)0);
        this.bitsPerCode = 9;
        this.maxCode = LZWEncoder.maxValue(this.bitsPerCode);
        this.nextValidCode = 258;
    }

    private void writeCode(OutputStream outputStream, int n) throws IOException {
        this.bits = this.bits << this.bitsPerCode | n & this.maxCode;
        this.bitPos += this.bitsPerCode;
        while (this.bitPos >= 8) {
            int n2 = this.bits >> this.bitPos - 8 & 0xFF;
            outputStream.write(n2);
            this.bitPos -= 8;
        }
        this.bits &= LZWEncoder.bitmaskFor(this.bitPos);
    }

    private static int maxValue(int n) {
        return (1 << n) - 1;
    }

    private static int bitmaskFor(int n) {
        return LZWEncoder.maxValue(n);
    }
}

