/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.io.enc.Decoder
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.io.enc.Decoder;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

abstract class AbstractRLEDecoder
implements Decoder {
    protected final int width;
    protected final int bitsPerSample;
    protected final byte[] row;
    protected int srcX;
    protected int srcY;
    protected int dstX;
    protected int dstY;

    AbstractRLEDecoder(int n, int n2) {
        this.width = n;
        this.bitsPerSample = n2;
        int n3 = (n2 * this.width + 31) / 32 * 4;
        this.row = new byte[n3];
    }

    protected abstract void decodeRow(InputStream var1) throws IOException;

    public final int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.capacity() < this.row.length) {
            throw new AssertionError((Object)"This decoder needs a buffer.capacity() of at least one row");
        }
        while (byteBuffer.remaining() >= this.row.length && this.srcY >= 0) {
            if (this.dstX == 0 && this.srcY == this.dstY) {
                this.decodeRow(inputStream);
            }
            int n = Math.min(this.row.length - this.dstX * this.bitsPerSample / 8, byteBuffer.remaining());
            byteBuffer.put(this.row, 0, n);
            this.dstX += n * 8 / this.bitsPerSample;
            if (this.dstX != this.row.length * 8 / this.bitsPerSample) continue;
            this.dstX = 0;
            ++this.dstY;
            if (this.srcX > this.dstX) {
                Arrays.fill(this.row, 0, this.srcX * this.bitsPerSample / 8, (byte)0);
            }
            if (this.srcY <= this.dstY) continue;
            Arrays.fill(this.row, (byte)0);
        }
        return byteBuffer.position();
    }

    protected static int checkEOF(int n) throws EOFException {
        if (n < 0) {
            throw new EOFException("Premature end of file");
        }
        return n;
    }
}

