/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.standard;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.standard.ClassicTokenizer;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import java.io.IOException;

public class ClassicFilter
extends TokenFilter {
    private static final String APOSTROPHE_TYPE = ClassicTokenizer.TOKEN_TYPES[1];
    private static final String ACRONYM_TYPE = ClassicTokenizer.TOKEN_TYPES[2];
    private final TypeAttribute typeAtt = this.addAttribute(TypeAttribute.class);
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public ClassicFilter(TokenStream in) {
        super(in);
    }

    public final boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] buffer = this.termAtt.buffer();
        int bufferLength = this.termAtt.length();
        String type = this.typeAtt.type();
        if (type == APOSTROPHE_TYPE && bufferLength >= 2 && buffer[bufferLength - 2] == '\'' && (buffer[bufferLength - 1] == 's' || buffer[bufferLength - 1] == 'S')) {
            this.termAtt.setLength(bufferLength - 2);
        } else if (type == ACRONYM_TYPE) {
            int upto = 0;
            for (int i = 0; i < bufferLength; ++i) {
                char c = buffer[i];
                if (c == '.') continue;
                buffer[upto++] = c;
            }
            this.termAtt.setLength(upto);
        }
        return true;
    }
}

