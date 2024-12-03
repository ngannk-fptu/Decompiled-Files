/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

public final class LengthFilter
extends FilteringTokenFilter {
    private final int min;
    private final int max;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    @Deprecated
    public LengthFilter(Version version, boolean enablePositionIncrements, TokenStream in, int min, int max) {
        super(version, enablePositionIncrements, in);
        this.min = min;
        this.max = max;
    }

    public LengthFilter(Version version, TokenStream in, int min, int max) {
        super(version, in);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean accept() {
        int len = this.termAtt.length();
        return len >= this.min && len <= this.max;
    }
}

