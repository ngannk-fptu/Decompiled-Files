/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.FilteringTokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;

public final class LengthFilter
extends FilteringTokenFilter {
    private final int min;
    private final int max;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public LengthFilter(boolean enablePositionIncrements, TokenStream in, int min, int max) {
        super(enablePositionIncrements, in);
        this.min = min;
        this.max = max;
    }

    @Deprecated
    public LengthFilter(TokenStream in, int min, int max) {
        this(false, in, min, max);
    }

    public boolean accept() throws IOException {
        int len = this.termAtt.length();
        return len >= this.min && len <= this.max;
    }
}

