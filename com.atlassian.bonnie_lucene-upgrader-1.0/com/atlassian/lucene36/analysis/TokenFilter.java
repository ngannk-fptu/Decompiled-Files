/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenStream;
import java.io.IOException;

public abstract class TokenFilter
extends TokenStream {
    protected final TokenStream input;

    protected TokenFilter(TokenStream input) {
        super(input);
        this.input = input;
    }

    public void end() throws IOException {
        this.input.end();
    }

    public void close() throws IOException {
        this.input.close();
    }

    public void reset() throws IOException {
        this.input.reset();
    }
}

