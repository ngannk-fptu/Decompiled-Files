/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.AttributeSource$State
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public final class KeywordRepeatFilter
extends TokenFilter {
    private final KeywordAttribute keywordAttribute = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final PositionIncrementAttribute posIncAttr = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private AttributeSource.State state;

    public KeywordRepeatFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.state != null) {
            this.restoreState(this.state);
            this.posIncAttr.setPositionIncrement(0);
            this.keywordAttribute.setKeyword(false);
            this.state = null;
            return true;
        }
        if (this.input.incrementToken()) {
            this.state = this.captureState();
            this.keywordAttribute.setKeyword(true);
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.state = null;
    }
}

