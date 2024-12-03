/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;

public abstract class Tokenizer
extends TokenStream {
    protected Reader input;

    protected Tokenizer(Reader input) {
        assert (input != null) : "input must not be null";
        this.input = input;
    }

    protected Tokenizer(AttributeSource.AttributeFactory factory, Reader input) {
        super(factory);
        assert (input != null) : "input must not be null";
        this.input = input;
    }

    @Override
    public void close() throws IOException {
        if (this.input != null) {
            this.input.close();
            this.input = null;
        }
    }

    protected final int correctOffset(int currentOff) {
        assert (this.input != null) : "this tokenizer is closed";
        return this.input instanceof CharFilter ? ((CharFilter)this.input).correctOffset(currentOff) : currentOff;
    }

    public final void setReader(Reader input) throws IOException {
        assert (input != null) : "input must not be null";
        this.input = input;
        assert (this.setReaderTestPoint());
    }

    boolean setReaderTestPoint() {
        return true;
    }
}

