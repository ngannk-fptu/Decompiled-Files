/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.util;

import java.io.IOException;
import java.io.Writer;

public class StringBufferWriter
extends Writer {
    private StringBuffer buffer;
    private boolean closed = false;

    public StringBufferWriter(StringBuffer buffer) {
        this.buffer = buffer;
        this.lock = buffer;
    }

    public StringBufferWriter() {
        this.buffer = new StringBuffer();
        this.lock = this.buffer;
    }

    public StringBufferWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative buffer size");
        }
        this.buffer = new StringBuffer(initialSize);
        this.lock = this.buffer;
    }

    public void write(int c) {
        this.buffer.append((char)c);
    }

    public void write(char[] cbuf, int off, int len) {
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.buffer.append(cbuf, off, len);
    }

    public void write(String str) {
        this.buffer.append(str);
    }

    public void write(String str, int off, int len) {
        this.buffer.append(str.substring(off, off + len));
    }

    public String toString() {
        return this.buffer.toString();
    }

    public StringBuffer getBuffer() {
        return this.buffer;
    }

    public void flush() {
    }

    public void close() throws IOException {
        this.closed = true;
    }
}

