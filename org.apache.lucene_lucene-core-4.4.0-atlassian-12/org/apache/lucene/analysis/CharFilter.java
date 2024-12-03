/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.IOException;
import java.io.Reader;

public abstract class CharFilter
extends Reader {
    protected final Reader input;

    public CharFilter(Reader input) {
        super(input);
        this.input = input;
    }

    @Override
    public void close() throws IOException {
        this.input.close();
    }

    protected abstract int correct(int var1);

    public final int correctOffset(int currentOff) {
        int corrected = this.correct(currentOff);
        return this.input instanceof CharFilter ? ((CharFilter)this.input).correctOffset(corrected) : corrected;
    }
}

