/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;

public final class LowerCaseFilter
extends TokenFilter {
    private final CharacterUtils charUtils;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public LowerCaseFilter(Version matchVersion, TokenStream in) {
        super(in);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            this.charUtils.toLowerCase(this.termAtt.buffer(), 0, this.termAtt.length());
            return true;
        }
        return false;
    }
}

