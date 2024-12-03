/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharStream;
import java.io.IOException;
import java.io.Reader;

public final class CharReader
extends CharStream {
    private final Reader input;

    public static CharStream get(Reader input) {
        return input instanceof CharStream ? (CharStream)input : new CharReader(input);
    }

    private CharReader(Reader in) {
        this.input = in;
    }

    public int correctOffset(int currentOff) {
        return currentOff;
    }

    public void close() throws IOException {
        this.input.close();
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.input.read(cbuf, off, len);
    }

    public boolean markSupported() {
        return this.input.markSupported();
    }

    public void mark(int readAheadLimit) throws IOException {
        this.input.mark(readAheadLimit);
    }

    public void reset() throws IOException {
        this.input.reset();
    }
}

