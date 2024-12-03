/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.ar;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class ArabicNormalizationFilter
extends TokenFilter {
    private final ArabicNormalizer normalizer = new ArabicNormalizer();
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public ArabicNormalizationFilter(TokenStream input) {
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

