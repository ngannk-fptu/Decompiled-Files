/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharArraySet;
import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.KeywordAttribute;
import com.atlassian.lucene36.util.Version;
import java.io.IOException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class KeywordMarkerFilter
extends TokenFilter {
    private final KeywordAttribute keywordAttr = this.addAttribute(KeywordAttribute.class);
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);
    private final CharArraySet keywordSet;

    public KeywordMarkerFilter(TokenStream in, CharArraySet keywordSet) {
        super(in);
        this.keywordSet = keywordSet;
    }

    public KeywordMarkerFilter(TokenStream in, Set<?> keywordSet) {
        this(in, keywordSet instanceof CharArraySet ? (CharArraySet)keywordSet : CharArraySet.copy(Version.LUCENE_31, keywordSet));
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (this.keywordSet.contains(this.termAtt.buffer(), 0, this.termAtt.length())) {
                this.keywordAttr.setKeyword(true);
            }
            return true;
        }
        return false;
    }
}

