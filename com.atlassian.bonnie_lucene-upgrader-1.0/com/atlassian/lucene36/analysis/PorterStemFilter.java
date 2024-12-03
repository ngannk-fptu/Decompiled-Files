/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.PorterStemmer;
import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.KeywordAttribute;
import java.io.IOException;

public final class PorterStemFilter
extends TokenFilter {
    private final PorterStemmer stemmer = new PorterStemmer();
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = this.addAttribute(KeywordAttribute.class);

    public PorterStemFilter(TokenStream in) {
        super(in);
    }

    public final boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        if (!this.keywordAttr.isKeyword() && this.stemmer.stem(this.termAtt.buffer(), 0, this.termAtt.length())) {
            this.termAtt.copyBuffer(this.stemmer.getResultBuffer(), 0, this.stemmer.getResultLength());
        }
        return true;
    }
}

