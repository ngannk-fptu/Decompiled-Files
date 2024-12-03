/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.fa;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.fa.PersianNormalizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class PersianNormalizationFilter
extends TokenFilter {
    private final PersianNormalizer normalizer = new PersianNormalizer();
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public PersianNormalizationFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            int newlen = this.normalizer.normalize(this.termAtt.buffer(), this.termAtt.length());
            this.termAtt.setLength(newlen);
            return true;
        }
        return false;
    }
}

