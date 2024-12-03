/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class UCSReader
extends Reader {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final short UCS2LE = 1;
    public static final short UCS2BE = 2;
    public static final short UCS4LE = 4;
    public static final short UCS4BE = 8;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    protected final short fEncoding;

    public UCSReader(InputStream inputStream, short s) {
        this(inputStream, 8192, s);
    }

    public UCSReader(InputStream inputStream, int n, short s) {
        this(inputStream, new byte[n], s);
    }

    public UCSReader(InputStream inputStream, byte[] byArray, short s) {
        this.fInputStream = inputStream;
        this.fBuffer = byArray;
        this.fEncoding = s;
    }

    @Override
    public int read() throws IOException {
        int n = this.fInputStream.read() & 0xFF;
        if (n == 255) {
            return -1;
        }
        int n2 = this.fInputStream.read() & 0xFF;
        if (n2 == 255) {
            return -1;
        }
        if (this.fEncoding >= 4) {
            int n3 = this.fInputStream.read() & 0xFF;
            if (n3 == 255) {
                return -1;
            }
            int n4 = this.fInputStream.read() & 0xFF;
            if (n4 == 255) {
                return -1;
            }
            if (this.fEncoding == 8) {
                return (n << 24) + (n2 << 16) + (n3 << 8) + n4;
            }
            return (n4 << 24) + (n3 << 16) + (n2 << 8) + n;
        }
        if (this.fEncoding == 2) {
            return (n << 8) + n2;
        }
        return (n2 << 8) + n;
    }

    @Override
    public int read(char[] cArray, int n, int n2) throws IOException {
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = n2 << (this.fEncoding >= 4 ? 2 : 1);
        if (n8 > this.fBuffer.length) {
            n8 = this.fBuffer.length;
        }
        if ((n7 = this.fInputStream.read(this.fBuffer, 0, n8)) == -1) {
            return -1;
        }
        if (this.fEncoding >= 4) {
            n6 = 4 - (n7 & 3) & 3;
            for (n5 = 0; n5 < n6; ++n5) {
                n4 = this.fInputStream.read();
                if (n4 == -1) {
                    for (n3 = n5; n3 < n6; ++n3) {
                        this.fBuffer[n7 + n3] = 0;
                    }
                    break;
                }
                this.fBuffer[n7 + n5] = (byte)n4;
            }
            n7 += n6;
        } else {
            n6 = n7 & 1;
            if (n6 != 0) {
                ++n7;
                n5 = this.fInputStream.read();
                this.fBuffer[n7] = n5 == -1 ? (byte)0 : (byte)n5;
            }
        }
        n6 = n7 >> (this.fEncoding >= 4 ? 2 : 1);
        n5 = 0;
        for (n4 = 0; n4 < n6; ++n4) {
            n3 = this.fBuffer[n5++] & 0xFF;
            int n9 = this.fBuffer[n5++] & 0xFF;
            if (this.fEncoding >= 4) {
                int n10 = this.fBuffer[n5++] & 0xFF;
                int n11 = this.fBuffer[n5++] & 0xFF;
                if (this.fEncoding == 8) {
                    cArray[n + n4] = (char)((n3 << 24) + (n9 << 16) + (n10 << 8) + n11);
                    continue;
                }
                cArray[n + n4] = (char)((n11 << 24) + (n10 << 16) + (n9 << 8) + n3);
                continue;
            }
            cArray[n + n4] = this.fEncoding == 2 ? (char)((n3 << 8) + n9) : (char)((n9 << 8) + n3);
        }
        return n6;
    }

    @Override
    public long skip(long l) throws IOException {
        int n = this.fEncoding >= 4 ? 2 : 1;
        long l2 = this.fInputStream.skip(l << n);
        if ((l2 & (long)(n | 1)) == 0L) {
            return l2 >> n;
        }
        return (l2 >> n) + 1L;
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }

    @Override
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }

    @Override
    public void mark(int n) throws IOException {
        this.fInputStream.mark(n);
    }

    @Override
    public void reset() throws IOException {
        this.fInputStream.reset();
    }

    @Override
    public void close() throws IOException {
        this.fInputStream.close();
    }
}

