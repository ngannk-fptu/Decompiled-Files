/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.stempel;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class StempelFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final StempelStemmer stemmer;
    private final int minLength;
    public static final int DEFAULT_MIN_LENGTH = 3;

    public StempelFilter(TokenStream in, StempelStemmer stemmer) {
        this(in, stemmer, 3);
    }

    public StempelFilter(TokenStream in, StempelStemmer stemmer, int minLength) {
        super(in);
        this.stemmer = stemmer;
        this.minLength = minLength;
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            StringBuilder sb;
            if (!this.keywordAtt.isKeyword() && this.termAtt.length() > this.minLength && (sb = this.stemmer.stem((CharSequence)this.termAtt)) != null) {
                this.termAtt.setEmpty().append(sb);
            }
            return true;
        }
        return false;
    }
}

