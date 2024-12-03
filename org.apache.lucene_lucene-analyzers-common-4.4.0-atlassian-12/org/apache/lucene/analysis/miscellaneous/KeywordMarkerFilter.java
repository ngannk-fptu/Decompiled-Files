/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public abstract class KeywordMarkerFilter
extends TokenFilter {
    private final KeywordAttribute keywordAttr = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);

    protected KeywordMarkerFilter(TokenStream in) {
        super(in);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (this.isKeyword()) {
                this.keywordAttr.setKeyword(true);
            }
            return true;
        }
        return false;
    }

    protected abstract boolean isKeyword();
}

