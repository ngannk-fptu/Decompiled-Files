/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.StemmerUtil;

public final class ScandinavianNormalizationFilter
extends TokenFilter {
    private final CharTermAttribute charTermAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private static final char AA = '\u00c5';
    private static final char aa = '\u00e5';
    private static final char AE = '\u00c6';
    private static final char ae = '\u00e6';
    private static final char AE_se = '\u00c4';
    private static final char ae_se = '\u00e4';
    private static final char OE = '\u00d8';
    private static final char oe = '\u00f8';
    private static final char OE_se = '\u00d6';
    private static final char oe_se = '\u00f6';

    public ScandinavianNormalizationFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] buffer = this.charTermAttribute.buffer();
        int length = this.charTermAttribute.length();
        for (int i = 0; i < length; ++i) {
            if (buffer[i] == '\u00e4') {
                buffer[i] = 230;
                continue;
            }
            if (buffer[i] == '\u00c4') {
                buffer[i] = 198;
                continue;
            }
            if (buffer[i] == '\u00f6') {
                buffer[i] = 248;
                continue;
            }
            if (buffer[i] == '\u00d6') {
                buffer[i] = 216;
                continue;
            }
            if (length - 1 <= i) continue;
            if (buffer[i] == 'a' && (buffer[i + 1] == 'a' || buffer[i + 1] == 'o' || buffer[i + 1] == 'A' || buffer[i + 1] == 'O')) {
                length = StemmerUtil.delete(buffer, i + 1, length);
                buffer[i] = 229;
                continue;
            }
            if (buffer[i] == 'A' && (buffer[i + 1] == 'a' || buffer[i + 1] == 'A' || buffer[i + 1] == 'o' || buffer[i + 1] == 'O')) {
                length = StemmerUtil.delete(buffer, i + 1, length);
                buffer[i] = 197;
                continue;
            }
            if (buffer[i] == 'a' && (buffer[i + 1] == 'e' || buffer[i + 1] == 'E')) {
                length = StemmerUtil.delete(buffer, i + 1, length);
                buffer[i] = 230;
                continue;
            }
            if (buffer[i] == 'A' && (buffer[i + 1] == 'e' || buffer[i + 1] == 'E')) {
                length = StemmerUtil.delete(buffer, i + 1, length);
                buffer[i] = 198;
                continue;
            }
            if (buffer[i] == 'o' && (buffer[i + 1] == 'e' || buffer[i + 1] == 'E' || buffer[i + 1] == 'o' || buffer[i + 1] == 'O')) {
                length = StemmerUtil.delete(buffer, i + 1, length);
                buffer[i] = 248;
                continue;
            }
            if (buffer[i] != 'O' || buffer[i + 1] != 'e' && buffer[i + 1] != 'E' && buffer[i + 1] != 'o' && buffer[i + 1] != 'O') continue;
            length = StemmerUtil.delete(buffer, i + 1, length);
            buffer[i] = 216;
        }
        this.charTermAttribute.setLength(length);
        return true;
    }
}

