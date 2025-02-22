/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

public class BufferingOutputStream
extends OutputStream {
    private final OutputStream other;
    private final byte[] buf;
    private int bufOff;

    public BufferingOutputStream(OutputStream other) {
        this.other = other;
        this.buf = new byte[4096];
    }

    public BufferingOutputStream(OutputStream other, int bufferSize) {
        this.other = other;
        this.buf = new byte[bufferSize];
    }

    @Override
    public void write(byte[] bytes, int offset, int len) throws IOException {
        if (len < this.buf.length - this.bufOff) {
            System.arraycopy(bytes, offset, this.buf, this.bufOff, len);
            this.bufOff += len;
        } else {
            int gap = this.buf.length - this.bufOff;
            System.arraycopy(bytes, offset, this.buf, this.bufOff, gap);
            this.bufOff += gap;
            this.flush();
            offset += gap;
            len -= gap;
            while (len >= this.buf.length) {
                this.other.write(bytes, offset, this.buf.length);
                offset += this.buf.length;
                len -= this.buf.length;
            }
            if (len > 0) {
                System.arraycopy(bytes, offset, this.buf, this.bufOff, len);
                this.bufOff += len;
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.buf[this.bufOff++] = (byte)b;
        if (this.bufOff == this.buf.length) {
            this.flush();
        }
    }

    @Override
    public void flush() throws IOException {
        this.other.write(this.buf, 0, this.bufOff);
        this.bufOff = 0;
        Arrays.fill(this.buf, (byte)0);
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.other.close();
    }
}

