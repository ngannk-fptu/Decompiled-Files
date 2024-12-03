/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 */
package org.apache.lucene.analysis.position;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

@Deprecated
public final class PositionFilter
extends TokenFilter {
    private final int positionIncrement;
    private boolean firstTokenPositioned = false;
    private PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);

    public PositionFilter(TokenStream input) {
        this(input, 0);
    }

    public PositionFilter(TokenStream input, int positionIncrement) {
        super(input);
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("positionIncrement may not be negative");
        }
        this.positionIncrement = positionIncrement;
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (this.firstTokenPositioned) {
                this.posIncrAtt.setPositionIncrement(this.positionIncrement);
            } else {
                this.firstTokenPositioned = true;
            }
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.firstTokenPositioned = false;
    }
}

