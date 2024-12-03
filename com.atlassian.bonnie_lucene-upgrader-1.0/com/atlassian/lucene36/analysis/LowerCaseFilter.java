/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.util.CharacterUtils;
import com.atlassian.lucene36.util.Version;
import java.io.IOException;

public final class LowerCaseFilter
extends TokenFilter {
    private final CharacterUtils charUtils;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public LowerCaseFilter(Version matchVersion, TokenStream in) {
        super(in);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
    }

    @Deprecated
    public LowerCaseFilter(TokenStream in) {
        this(Version.LUCENE_30, in);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; i += Character.toChars(Character.toLowerCase(this.charUtils.codePointAt(buffer, i)), buffer, i)) {
            }
            return true;
        }
        return false;
    }
}

