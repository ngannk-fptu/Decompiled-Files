/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class PatternKeywordMarkerFilter
extends KeywordMarkerFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final Matcher matcher;

    public PatternKeywordMarkerFilter(TokenStream in, Pattern pattern) {
        super(in);
        this.matcher = pattern.matcher("");
    }

    @Override
    protected boolean isKeyword() {
        this.matcher.reset((CharSequence)this.termAtt);
        return this.matcher.matches();
    }
}

