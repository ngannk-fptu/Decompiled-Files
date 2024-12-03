/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.in;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.in.IndicNormalizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class IndicNormalizationFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final IndicNormalizer normalizer = new IndicNormalizer();

    public IndicNormalizationFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            this.termAtt.setLength(this.normalizer.normalize(this.termAtt.buffer(), this.termAtt.length()));
            return true;
        }
        return false;
    }
}

