/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharStream;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.util.AttributeSource;
import java.io.IOException;
import java.io.Reader;

public abstract class Tokenizer
extends TokenStream {
    protected Reader input;

    @Deprecated
    protected Tokenizer() {
    }

    protected Tokenizer(Reader input) {
        this.input = input;
    }

    @Deprecated
    protected Tokenizer(AttributeSource.AttributeFactory factory) {
        super(factory);
    }

    protected Tokenizer(AttributeSource.AttributeFactory factory, Reader input) {
        super(factory);
        this.input = input;
    }

    @Deprecated
    protected Tokenizer(AttributeSource source) {
        super(source);
    }

    protected Tokenizer(AttributeSource source, Reader input) {
        super(source);
        this.input = input;
    }

    public void close() throws IOException {
        if (this.input != null) {
            this.input.close();
            this.input = null;
        }
    }

    protected final int correctOffset(int currentOff) {
        return this.input instanceof CharStream ? ((CharStream)this.input).correctOffset(currentOff) : currentOff;
    }

    public void reset(Reader input) throws IOException {
        this.input = input;
    }
}

