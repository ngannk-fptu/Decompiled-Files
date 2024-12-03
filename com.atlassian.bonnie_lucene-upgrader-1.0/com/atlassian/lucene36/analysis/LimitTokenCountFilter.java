/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import java.io.IOException;

public final class LimitTokenCountFilter
extends TokenFilter {
    private final int maxTokenCount;
    private int tokenCount = 0;

    public LimitTokenCountFilter(TokenStream in, int maxTokenCount) {
        super(in);
        this.maxTokenCount = maxTokenCount;
    }

    public boolean incrementToken() throws IOException {
        if (this.tokenCount < this.maxTokenCount && this.input.incrementToken()) {
            ++this.tokenCount;
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.tokenCount = 0;
    }
}

