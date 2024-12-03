/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.id;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class IndonesianStemFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final IndonesianStemmer stemmer = new IndonesianStemmer();
    private final boolean stemDerivational;

    public IndonesianStemFilter(TokenStream input) {
        this(input, true);
    }

    public IndonesianStemFilter(TokenStream input, boolean stemDerivational) {
        super(input);
        this.stemDerivational = stemDerivational;
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAtt.isKeyword()) {
                int newlen = this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length(), this.stemDerivational);
                this.termAtt.setLength(newlen);
            }
            return true;
        }
        return false;
    }
}

