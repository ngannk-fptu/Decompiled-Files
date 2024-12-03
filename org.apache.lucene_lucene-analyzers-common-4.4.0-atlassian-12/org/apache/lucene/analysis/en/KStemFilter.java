/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.en;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.KStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class KStemFilter
extends TokenFilter {
    private final KStemmer stemmer = new KStemmer();
    private final CharTermAttribute termAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);

    public KStemFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] term = this.termAttribute.buffer();
        int len = this.termAttribute.length();
        if (!this.keywordAtt.isKeyword() && this.stemmer.stem(term, len)) {
            this.termAttribute.setEmpty().append(this.stemmer.asCharSequence());
        }
        return true;
    }
}

