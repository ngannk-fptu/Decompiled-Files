/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.pattern;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class PatternReplaceFilter
extends TokenFilter {
    private final String replacement;
    private final boolean all;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final Matcher m;

    public PatternReplaceFilter(TokenStream in, Pattern p, String replacement, boolean all) {
        super(in);
        this.replacement = null == replacement ? "" : replacement;
        this.all = all;
        this.m = p.matcher((CharSequence)this.termAtt);
    }

    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        this.m.reset();
        if (this.m.find()) {
            String transformed = this.all ? this.m.replaceAll(this.replacement) : this.m.replaceFirst(this.replacement);
            this.termAtt.setEmpty().append(transformed);
        }
        return true;
    }
}

