/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.br;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class BrazilianStemFilter
extends TokenFilter {
    private BrazilianStemmer stemmer = new BrazilianStemmer();
    private Set<?> exclusions = null;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);

    public BrazilianStemFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            String s;
            String term = this.termAtt.toString();
            if (!(this.keywordAttr.isKeyword() || this.exclusions != null && this.exclusions.contains(term) || (s = this.stemmer.stem(term)) == null || s.equals(term))) {
                this.termAtt.setEmpty().append(s);
            }
            return true;
        }
        return false;
    }
}

