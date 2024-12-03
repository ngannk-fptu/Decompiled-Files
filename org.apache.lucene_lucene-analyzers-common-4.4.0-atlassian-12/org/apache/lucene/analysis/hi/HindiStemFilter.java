/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.hi;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hi.HindiStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class HindiStemFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final HindiStemmer stemmer = new HindiStemmer();

    public HindiStemFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAtt.isKeyword()) {
                this.termAtt.setLength(this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length()));
            }
            return true;
        }
        return false;
    }
}

