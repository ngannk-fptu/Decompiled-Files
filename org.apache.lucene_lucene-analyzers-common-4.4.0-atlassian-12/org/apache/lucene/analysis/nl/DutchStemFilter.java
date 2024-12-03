/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.nl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.nl.DutchStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

@Deprecated
public final class DutchStemFilter
extends TokenFilter {
    private DutchStemmer stemmer = new DutchStemmer();
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);

    public DutchStemFilter(TokenStream _in) {
        super(_in);
    }

    public DutchStemFilter(TokenStream _in, Map<?, ?> stemdictionary) {
        this(_in);
        this.stemmer.setStemDictionary(stemdictionary);
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

    public void setStemmer(DutchStemmer stemmer) {
        if (stemmer != null) {
            this.stemmer = stemmer;
        }
    }

    public void setStemDictionary(HashMap<?, ?> dict) {
        if (this.stemmer != null) {
            this.stemmer.setStemDictionary(dict);
        }
    }
}

