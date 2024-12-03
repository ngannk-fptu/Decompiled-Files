/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.abdera.i18n.text.io.DynamicPushbackInputStream;

public class RewindableInputStream
extends DynamicPushbackInputStream {
    private static final int INITIAL_CAPACITY = 32;
    private byte[] buffer;
    private int position;
    private final int scale;

    public RewindableInputStream(InputStream in) {
        this(in, 32);
    }

    public RewindableInputStream(InputStream in, int capacity) {
        super(in);
        this.grow(capacity);
        this.scale = capacity;
    }

    public int position() {
        return this.position;
    }

    private void grow(int capacity) {
        if (this.buffer == null) {
            this.buffer = new byte[capacity];
            return;
        }
        byte[] buf = new byte[this.buffer.length + capacity];
        System.arraycopy(this.buffer, 0, buf, 0, this.buffer.length);
        this.buffer = buf;
    }

    private void shrink(int len) {
        if (this.buffer == null) {
            return;
        }
        byte[] buf = new byte[this.buffer.length - len];
        System.arraycopy(this.buffer, 0, buf, 0, buf.length);
        this.position = this.buffer.length - len;
        this.buffer = buf;
    }

    public void rewind() throws IOException {
        if (this.buffer.length == 0) {
            return;
        }
        this.unread(this.buffer, 0, this.position);
        this.shrink(this.buffer.length);
    }

    public void rewind(int offset, int len) throws IOException {
        if (this.buffer.length == 0) {
            return;
        }
        if (offset > this.buffer.length) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        this.unread(this.buffer, offset, len);
        this.shrink(len);
    }

    public void rewind(int len) throws IOException {
        if (this.buffer.length == 0) {
            return;
        }
        this.rewind(this.buffer.length - len, len);
    }

    public int read() throws IOException {
        int i = super.read();
        if (i != -1) {
            if (this.position >= this.buffer.length) {
                this.grow(this.scale);
            }
            this.buffer[this.position++] = (byte)i;
        }
        return i;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int r = super.read(b, off, len);
        if (r != -1) {
            if (this.position + r >= this.buffer.length) {
                this.grow(Math.max(this.position + r, this.scale));
            }
            System.arraycopy(b, off, this.buffer, this.position, r);
            this.position += r;
        }
        return r;
    }

    public long skip(long n) throws IOException {
        return super.skip(n);
    }
}

