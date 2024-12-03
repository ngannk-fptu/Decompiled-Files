/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import java.io.IOException;

public abstract class FilteringTokenFilter
extends TokenFilter {
    private final PositionIncrementAttribute posIncrAtt = this.addAttribute(PositionIncrementAttribute.class);
    private boolean enablePositionIncrements;
    private boolean first = true;

    public FilteringTokenFilter(boolean enablePositionIncrements, TokenStream input) {
        super(input);
        this.enablePositionIncrements = enablePositionIncrements;
    }

    protected abstract boolean accept() throws IOException;

    public final boolean incrementToken() throws IOException {
        if (this.enablePositionIncrements) {
            int skippedPositions = 0;
            while (this.input.incrementToken()) {
                if (this.accept()) {
                    if (skippedPositions != 0) {
                        this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + skippedPositions);
                    }
                    return true;
                }
                skippedPositions += this.posIncrAtt.getPositionIncrement();
            }
        } else {
            while (this.input.incrementToken()) {
                if (!this.accept()) continue;
                if (this.first) {
                    if (this.posIncrAtt.getPositionIncrement() == 0) {
                        this.posIncrAtt.setPositionIncrement(1);
                    }
                    this.first = false;
                }
                return true;
            }
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.first = true;
    }

    public boolean getEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    public void setEnablePositionIncrements(boolean enable) {
        this.enablePositionIncrements = enable;
    }
}

