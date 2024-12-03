/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

public final class LimitTokenCountFilter
extends TokenFilter {
    private final int maxTokenCount;
    private final boolean consumeAllTokens;
    private int tokenCount = 0;
    private boolean exhausted = false;

    public LimitTokenCountFilter(TokenStream in, int maxTokenCount) {
        this(in, maxTokenCount, false);
    }

    public LimitTokenCountFilter(TokenStream in, int maxTokenCount, boolean consumeAllTokens) {
        super(in);
        this.maxTokenCount = maxTokenCount;
        this.consumeAllTokens = consumeAllTokens;
    }

    public boolean incrementToken() throws IOException {
        if (this.exhausted) {
            return false;
        }
        if (this.tokenCount < this.maxTokenCount) {
            if (this.input.incrementToken()) {
                ++this.tokenCount;
                return true;
            }
            this.exhausted = true;
            return false;
        }
        while (this.consumeAllTokens && this.input.incrementToken()) {
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.tokenCount = 0;
        this.exhausted = false;
    }
}

