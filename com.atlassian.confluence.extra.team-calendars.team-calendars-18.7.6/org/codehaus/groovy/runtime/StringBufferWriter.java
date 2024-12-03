/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.io.IOException;
import java.io.Writer;

public class StringBufferWriter
extends Writer {
    private StringBuffer buffer;

    public StringBufferWriter(StringBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int c) {
        this.buffer.append((char)c);
    }

    @Override
    public void write(char[] text, int offset, int length) {
        if (offset < 0 || offset > text.length || length < 0 || offset + length > text.length || offset + length < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (length == 0) {
            return;
        }
        this.buffer.append(text, offset, length);
    }

    @Override
    public void write(String text) {
        this.buffer.append(text);
    }

    @Override
    public void write(String text, int offset, int length) {
        this.buffer.append(text.substring(offset, offset + length));
    }

    public String toString() {
        return this.buffer.toString();
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
    }
}

