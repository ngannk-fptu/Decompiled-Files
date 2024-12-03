/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class YCbCrPlanarUpsamplerStream
extends FilterInputStream {
    private final int horizChromaSub;
    private final int vertChromaSub;
    private final int yCbCrPos;
    private final int columns;
    private final int units;
    private final byte[] decodedRows;
    int decodedLength;
    int decodedPos;
    private final byte[] buffer;
    int bufferLength;
    int bufferPos;

    public YCbCrPlanarUpsamplerStream(InputStream inputStream, int[] nArray, int n, int n2) {
        super((InputStream)Validate.notNull((Object)inputStream, (String)"stream"));
        Validate.notNull((Object)nArray, (String)"chromaSub");
        Validate.isTrue((nArray.length == 2 ? 1 : 0) != 0, (String)"chromaSub.length != 2");
        this.horizChromaSub = nArray[0];
        this.vertChromaSub = nArray[1];
        this.yCbCrPos = n;
        this.columns = n2;
        this.units = (n2 + this.horizChromaSub - 1) / this.horizChromaSub;
        this.decodedRows = new byte[n2 * this.vertChromaSub];
        this.buffer = new byte[this.units];
    }

    private void fetch() throws IOException {
        if (this.bufferPos >= this.bufferLength) {
            int n;
            int n2;
            for (n = 0; n < this.buffer.length && (n2 = this.in.read(this.buffer, n, this.buffer.length - n)) > 0; n += n2) {
            }
            this.bufferLength = n;
            this.bufferPos = 0;
        }
        if (this.bufferLength > 0) {
            this.decodeRows();
        } else {
            this.decodedLength = -1;
        }
    }

    private void decodeRows() throws EOFException {
        this.decodedLength = this.decodedRows.length;
        for (int i = 0; i < this.units; ++i) {
            if (i >= this.bufferLength) {
                throw new EOFException("Unexpected end of stream");
            }
            byte by = this.buffer[i];
            for (int j = 0; j < this.vertChromaSub; ++j) {
                int n;
                for (int k = 0; k < this.horizChromaSub && (n = this.horizChromaSub * i + k) < this.columns; ++k) {
                    int n2 = n + this.columns * j;
                    this.decodedRows[n2] = by;
                }
            }
        }
        this.bufferPos = this.bufferLength;
        this.decodedPos = 0;
    }

    @Override
    public int read() throws IOException {
        if (this.decodedLength < 0) {
            return -1;
        }
        if (this.decodedPos >= this.decodedLength) {
            this.fetch();
            if (this.decodedLength < 0) {
                return -1;
            }
        }
        return this.decodedRows[this.decodedPos++] & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.decodedLength < 0) {
            return -1;
        }
        if (this.decodedPos >= this.decodedLength) {
            this.fetch();
            if (this.decodedLength < 0) {
                return -1;
            }
        }
        int n3 = Math.min(this.decodedLength - this.decodedPos, n2);
        System.arraycopy(this.decodedRows, this.decodedPos, byArray, n, n3);
        this.decodedPos += n3;
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        if (this.decodedLength < 0) {
            return -1L;
        }
        if (this.decodedPos >= this.decodedLength) {
            this.fetch();
            if (this.decodedLength < 0) {
                return -1L;
            }
        }
        int n = (int)Math.min((long)(this.decodedLength - this.decodedPos), l);
        this.decodedPos += n;
        return n;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }
}

