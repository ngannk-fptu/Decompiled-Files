/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.de;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class GermanStemFilter
extends TokenFilter {
    private GermanStemmer stemmer = new GermanStemmer();
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);

    public GermanStemFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            String s;
            String term = this.termAtt.toString();
            if (!this.keywordAttr.isKeyword() && (s = this.stemmer.stem(term)) != null && !s.equals(term)) {
                this.termAtt.setEmpty().append(s);
            }
            return true;
        }
        return false;
    }

    public void setStemmer(GermanStemmer stemmer) {
        if (stemmer != null) {
            this.stemmer = stemmer;
        }
    }
}

