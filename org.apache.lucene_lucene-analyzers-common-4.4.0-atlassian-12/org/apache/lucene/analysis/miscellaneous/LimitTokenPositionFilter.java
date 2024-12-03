/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public final class LimitTokenPositionFilter
extends TokenFilter {
    private final int maxTokenPosition;
    private final boolean consumeAllTokens;
    private int tokenPosition = 0;
    private boolean exhausted = false;
    private final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);

    public LimitTokenPositionFilter(TokenStream in, int maxTokenPosition) {
        this(in, maxTokenPosition, false);
    }

    public LimitTokenPositionFilter(TokenStream in, int maxTokenPosition, boolean consumeAllTokens) {
        super(in);
        this.maxTokenPosition = maxTokenPosition;
        this.consumeAllTokens = consumeAllTokens;
    }

    public boolean incrementToken() throws IOException {
        if (this.exhausted) {
            return false;
        }
        if (this.input.incrementToken()) {
            this.tokenPosition += this.posIncAtt.getPositionIncrement();
            if (this.tokenPosition <= this.maxTokenPosition) {
                return true;
            }
            while (this.consumeAllTokens && this.input.incrementToken()) {
            }
            this.exhausted = true;
            return false;
        }
        this.exhausted = true;
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.tokenPosition = 0;
        this.exhausted = false;
    }
}

