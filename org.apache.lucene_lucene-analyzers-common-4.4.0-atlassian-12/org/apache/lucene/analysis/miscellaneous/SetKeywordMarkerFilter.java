/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

public final class SetKeywordMarkerFilter
extends KeywordMarkerFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final CharArraySet keywordSet;

    public SetKeywordMarkerFilter(TokenStream in, CharArraySet keywordSet) {
        super(in);
        this.keywordSet = keywordSet;
    }

    @Override
    protected boolean isKeyword() {
        return this.keywordSet.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}

