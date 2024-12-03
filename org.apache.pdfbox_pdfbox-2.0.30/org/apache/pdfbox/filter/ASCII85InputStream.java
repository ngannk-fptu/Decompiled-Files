/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class ASCII85InputStream
extends FilterInputStream {
    private int index = 0;
    private int n = 0;
    private boolean eof = false;
    private byte[] ascii = new byte[5];
    private byte[] b = new byte[4];
    private static final char TERMINATOR = '~';
    private static final char OFFSET = '!';
    private static final char NEWLINE = '\n';
    private static final char RETURN = '\r';
    private static final char SPACE = ' ';
    private static final char PADDING_U = 'u';
    private static final char Z = 'z';

    ASCII85InputStream(InputStream is) {
        super(is);
    }

    @Override
    public int read() throws IOException {
        if (this.index >= this.n) {
            byte zz;
            byte z;
            if (this.eof) {
                return -1;
            }
            this.index = 0;
            do {
                if ((zz = (byte)this.in.read()) != -1) continue;
                this.eof = true;
                return -1;
            } while ((z = (byte)zz) == 10 || z == 13 || z == 32);
            if (z == 126) {
                this.eof = true;
                this.b = null;
                this.ascii = null;
                this.n = 0;
                return -1;
            }
            if (z == 122) {
                this.b[3] = 0;
                this.b[2] = 0;
                this.b[1] = 0;
                this.b[0] = 0;
                this.n = 4;
            } else {
                int k;
                this.ascii[0] = z;
                for (k = 1; k < 5; ++k) {
                    do {
                        if ((zz = (byte)this.in.read()) != -1) continue;
                        this.eof = true;
                        return -1;
                    } while ((z = (byte)zz) == 10 || z == 13 || z == 32);
                    this.ascii[k] = z;
                    if (z != 126) continue;
                    this.ascii[k] = 117;
                    break;
                }
                this.n = k - 1;
                if (this.n == 0) {
                    this.eof = true;
                    this.ascii = null;
                    this.b = null;
                    return -1;
                }
                if (k < 5) {
                    ++k;
                    while (k < 5) {
                        this.ascii[k] = 117;
                        ++k;
                    }
                    this.eof = true;
                }
                long t = 0L;
                for (k = 0; k < 5; ++k) {
                    z = (byte)(this.ascii[k] - 33);
                    if (z < 0 || z > 93) {
                        this.n = 0;
                        this.eof = true;
                        this.ascii = null;
                        this.b = null;
                        throw new IOException("Invalid data in Ascii85 stream");
                    }
                    t = t * 85L + (long)z;
                }
                for (k = 3; k >= 0; --k) {
                    this.b[k] = (byte)(t & 0xFFL);
                    t >>>= 8;
                }
            }
        }
        return this.b[this.index++] & 0xFF;
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        if (this.eof && this.index >= this.n) {
            return -1;
        }
        for (int i = 0; i < len; ++i) {
            if (this.index < this.n) {
                data[i + offset] = this.b[this.index++];
                continue;
            }
            int t = this.read();
            if (t == -1) {
                return i;
            }
            data[i + offset] = (byte)t;
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        this.ascii = null;
        this.eof = true;
        this.b = null;
        super.close();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public long skip(long nValue) {
        return 0L;
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public synchronized void mark(int readlimit) {
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("Reset is not supported");
    }
}

