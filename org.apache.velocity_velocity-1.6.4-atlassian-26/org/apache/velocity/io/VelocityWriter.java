/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.io;

import java.io.IOException;
import java.io.Writer;

public final class VelocityWriter
extends Writer {
    public static final int NO_BUFFER = 0;
    public static final int DEFAULT_BUFFER = -1;
    public static final int UNBOUNDED_BUFFER = -2;
    private int bufferSize;
    private boolean autoFlush;
    private Writer writer;
    private char[] cb;
    private int nextChar;
    private static int defaultCharBufferSize = 8192;

    public VelocityWriter(Writer writer) {
        this(writer, defaultCharBufferSize, true);
    }

    private VelocityWriter(int bufferSize, boolean autoFlush) {
        this.bufferSize = bufferSize;
        this.autoFlush = autoFlush;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public boolean isAutoFlush() {
        return this.autoFlush;
    }

    public VelocityWriter(Writer writer, int sz, boolean autoFlush) {
        this(sz, autoFlush);
        if (sz < 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.writer = writer;
        this.cb = sz == 0 ? null : new char[sz];
        this.nextChar = 0;
    }

    private final void flushBuffer() throws IOException {
        if (this.bufferSize == 0) {
            return;
        }
        if (this.nextChar == 0) {
            return;
        }
        this.writer.write(this.cb, 0, this.nextChar);
        this.nextChar = 0;
    }

    public final void clear() {
        this.nextChar = 0;
    }

    private final void bufferOverflow() throws IOException {
        throw new IOException("overflow");
    }

    @Override
    public final void flush() throws IOException {
        this.flushBuffer();
        if (this.writer != null) {
            this.writer.flush();
        }
    }

    @Override
    public final void close() throws IOException {
        if (this.writer == null) {
            return;
        }
        this.flush();
    }

    public final int getRemaining() {
        return this.bufferSize - this.nextChar;
    }

    @Override
    public final void write(int c) throws IOException {
        if (this.bufferSize == 0) {
            this.writer.write(c);
        } else {
            if (this.nextChar >= this.bufferSize) {
                if (this.autoFlush) {
                    this.flushBuffer();
                } else {
                    this.bufferOverflow();
                }
            }
            this.cb[this.nextChar++] = (char)c;
        }
    }

    private final int min(int a, int b) {
        return a < b ? a : b;
    }

    @Override
    public final void write(char[] cbuf, int off, int len) throws IOException {
        if (this.bufferSize == 0) {
            this.writer.write(cbuf, off, len);
            return;
        }
        if (len == 0) {
            return;
        }
        if (len >= this.bufferSize) {
            if (this.autoFlush) {
                this.flushBuffer();
            } else {
                this.bufferOverflow();
            }
            this.writer.write(cbuf, off, len);
            return;
        }
        int b = off;
        int t = off + len;
        while (b < t) {
            int d = this.min(this.bufferSize - this.nextChar, t - b);
            System.arraycopy(cbuf, b, this.cb, this.nextChar, d);
            b += d;
            this.nextChar += d;
            if (this.nextChar < this.bufferSize) continue;
            if (this.autoFlush) {
                this.flushBuffer();
                continue;
            }
            this.bufferOverflow();
        }
    }

    @Override
    public final void write(char[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }

    @Override
    public final void write(String s, int off, int len) throws IOException {
        if (this.bufferSize == 0) {
            this.writer.write(s, off, len);
            return;
        }
        int b = off;
        int t = off + len;
        while (b < t) {
            int d = this.min(this.bufferSize - this.nextChar, t - b);
            s.getChars(b, b + d, this.cb, this.nextChar);
            b += d;
            this.nextChar += d;
            if (this.nextChar < this.bufferSize) continue;
            if (this.autoFlush) {
                this.flushBuffer();
                continue;
            }
            this.bufferOverflow();
        }
    }

    @Override
    public final void write(String s) throws IOException {
        if (s != null) {
            this.write(s, 0, s.length());
        }
    }

    public final void recycle(Writer writer) {
        this.writer = writer;
        this.clear();
    }
}

