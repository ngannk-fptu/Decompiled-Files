/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharStream;
import java.io.IOException;

public abstract class CharFilter
extends CharStream {
    protected CharStream input;

    protected CharFilter(CharStream in) {
        this.input = in;
    }

    protected int correct(int currentOff) {
        return currentOff;
    }

    public final int correctOffset(int currentOff) {
        return this.input.correctOffset(this.correct(currentOff));
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

