/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.tr;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class TurkishLowerCaseFilter
extends TokenFilter {
    private static final int LATIN_CAPITAL_LETTER_I = 73;
    private static final int LATIN_SMALL_LETTER_I = 105;
    private static final int LATIN_SMALL_LETTER_DOTLESS_I = 305;
    private static final int COMBINING_DOT_ABOVE = 775;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public TurkishLowerCaseFilter(TokenStream in) {
        super(in);
    }

    public final boolean incrementToken() throws IOException {
        boolean iOrAfter = false;
        if (this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            int i = 0;
            block4: while (i < length) {
                int ch = Character.codePointAt(buffer, i, length);
                boolean bl = iOrAfter = ch == 73 || iOrAfter && Character.getType(ch) == 6;
                if (iOrAfter) {
                    switch (ch) {
                        case 775: {
                            length = this.delete(buffer, i, length);
                            continue block4;
                        }
                        case 73: {
                            if (this.isBeforeDot(buffer, i + 1, length)) {
                                buffer[i] = 105;
                            } else {
                                buffer[i] = 305;
                                iOrAfter = false;
                            }
                            ++i;
                            continue block4;
                        }
                    }
                }
                i += Character.toChars(Character.toLowerCase(ch), buffer, i);
            }
            this.termAtt.setLength(length);
            return true;
        }
        return false;
    }

    private boolean isBeforeDot(char[] s, int pos, int len) {
        int ch;
        for (int i = pos; i < len; i += Character.charCount(ch)) {
            ch = Character.codePointAt(s, i, len);
            if (Character.getType(ch) != 6) {
                return false;
            }
            if (ch != 775) continue;
            return true;
        }
        return false;
    }

    private int delete(char[] s, int pos, int len) {
        if (pos < len) {
            System.arraycopy(s, pos + 1, s, pos, len - pos - 1);
        }
        return len - 1;
    }
}

