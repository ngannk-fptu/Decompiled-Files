/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.de;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.StemmerUtil;

public final class GermanNormalizationFilter
extends TokenFilter {
    private static final int N = 0;
    private static final int V = 1;
    private static final int U = 2;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public GermanNormalizationFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            int state = 0;
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            block10: for (int i = 0; i < length; ++i) {
                char c = buffer[i];
                switch (c) {
                    case 'a': 
                    case 'o': {
                        state = 2;
                        continue block10;
                    }
                    case 'u': {
                        state = state == 0 ? 2 : 1;
                        continue block10;
                    }
                    case 'e': {
                        if (state == 2) {
                            length = StemmerUtil.delete(buffer, i--, length);
                        }
                        state = 1;
                        continue block10;
                    }
                    case 'i': 
                    case 'q': 
                    case 'y': {
                        state = 1;
                        continue block10;
                    }
                    case '\u00e4': {
                        buffer[i] = 97;
                        state = 1;
                        continue block10;
                    }
                    case '\u00f6': {
                        buffer[i] = 111;
                        state = 1;
                        continue block10;
                    }
                    case '\u00fc': {
                        buffer[i] = 117;
                        state = 1;
                        continue block10;
                    }
                    case '\u00df': {
                        buffer[i++] = 115;
                        buffer = this.termAtt.resizeBuffer(1 + length);
                        if (i < length) {
                            System.arraycopy(buffer, i, buffer, i + 1, length - i);
                        }
                        buffer[i] = 115;
                        ++length;
                        state = 0;
                        continue block10;
                    }
                    default: {
                        state = 0;
                    }
                }
            }
            this.termAtt.setLength(length);
            return true;
        }
        return false;
    }
}

