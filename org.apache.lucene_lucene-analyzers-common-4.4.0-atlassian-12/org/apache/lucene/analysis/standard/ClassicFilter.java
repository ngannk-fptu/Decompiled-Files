/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 */
package org.apache.lucene.analysis.standard;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class ClassicFilter
extends TokenFilter {
    private static final String APOSTROPHE_TYPE = ClassicTokenizer.TOKEN_TYPES[1];
    private static final String ACRONYM_TYPE = ClassicTokenizer.TOKEN_TYPES[2];
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

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

