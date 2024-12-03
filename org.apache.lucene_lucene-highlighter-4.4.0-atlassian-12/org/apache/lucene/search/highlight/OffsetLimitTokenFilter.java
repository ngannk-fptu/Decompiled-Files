/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public final class OffsetLimitTokenFilter
extends TokenFilter {
    private int offsetCount;
    private OffsetAttribute offsetAttrib = (OffsetAttribute)this.getAttribute(OffsetAttribute.class);
    private int offsetLimit;

    public OffsetLimitTokenFilter(TokenStream input, int offsetLimit) {
        super(input);
        this.offsetLimit = offsetLimit;
    }

    public boolean incrementToken() throws IOException {
        if (this.offsetCount < this.offsetLimit && this.input.incrementToken()) {
            int offsetLength = this.offsetAttrib.endOffset() - this.offsetAttrib.startOffset();
            this.offsetCount += offsetLength;
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.offsetCount = 0;
    }
}

