/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.cjk;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.StemmerUtil;

public final class CJKWidthFilter
extends TokenFilter {
    private CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private static final char[] KANA_NORM = new char[]{'\u30fb', '\u30f2', '\u30a1', '\u30a3', '\u30a5', '\u30a7', '\u30a9', '\u30e3', '\u30e5', '\u30e7', '\u30c3', '\u30fc', '\u30a2', '\u30a4', '\u30a6', '\u30a8', '\u30aa', '\u30ab', '\u30ad', '\u30af', '\u30b1', '\u30b3', '\u30b5', '\u30b7', '\u30b9', '\u30bb', '\u30bd', '\u30bf', '\u30c1', '\u30c4', '\u30c6', '\u30c8', '\u30ca', '\u30cb', '\u30cc', '\u30cd', '\u30ce', '\u30cf', '\u30d2', '\u30d5', '\u30d8', '\u30db', '\u30de', '\u30df', '\u30e0', '\u30e1', '\u30e2', '\u30e4', '\u30e6', '\u30e8', '\u30e9', '\u30ea', '\u30eb', '\u30ec', '\u30ed', '\u30ef', '\u30f3', '\u3099', '\u309a'};
    private static final byte[] KANA_COMBINE_VOICED = new byte[]{78, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    private static final byte[] KANA_COMBINE_HALF_VOICED = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public CJKWidthFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] text = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; ++i) {
                char ch = text[i];
                if (ch >= '\uff01' && ch <= '\uff5e') {
                    int n = i;
                    text[n] = (char)(text[n] - 65248);
                    continue;
                }
                if (ch < '\uff65' || ch > '\uff9f') continue;
                if ((ch == '\uff9e' || ch == '\uff9f') && i > 0 && CJKWidthFilter.combine(text, i, ch)) {
                    length = StemmerUtil.delete(text, i--, length);
                    continue;
                }
                text[i] = KANA_NORM[ch - 65381];
            }
            this.termAtt.setLength(length);
            return true;
        }
        return false;
    }

    private static boolean combine(char[] text, int pos, char ch) {
        char prev = text[pos - 1];
        if (prev >= '\u30a6' && prev <= '\u30fd') {
            int n = pos - 1;
            text[n] = (char)(text[n] + (ch == '\uff9f' ? KANA_COMBINE_HALF_VOICED[prev - 12454] : KANA_COMBINE_VOICED[prev - 12454]));
            return text[pos - 1] != prev;
        }
        return false;
    }
}

