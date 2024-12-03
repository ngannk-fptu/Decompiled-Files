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
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

public final class KeepWordFilter
extends FilteringTokenFilter {
    private final CharArraySet words;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    @Deprecated
    public KeepWordFilter(Version version, boolean enablePositionIncrements, TokenStream in, CharArraySet words) {
        super(version, enablePositionIncrements, in);
        this.words = words;
    }

    public KeepWordFilter(Version version, TokenStream in, CharArraySet words) {
        super(version, in);
        this.words = words;
    }

    @Override
    public boolean accept() {
        return this.words.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}

