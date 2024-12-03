/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.SplitCharacter;
import com.lowagie.text.pdf.PdfChunk;

public class DefaultSplitCharacter
implements SplitCharacter {
    public static final SplitCharacter DEFAULT = new DefaultSplitCharacter();

    @Override
    public boolean isSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
        char c = this.getCurrentCharacter(current, cc, ck);
        if (c <= ' ' || c == '-' || c == '\u2010') {
            return true;
        }
        if (c < '\u2002') {
            return false;
        }
        return c <= '\u200b' || c >= '\u2e80' && c < '\ud7a0' || c >= '\uf900' && c < '\ufb00' || c >= '\ufe30' && c < '\ufe50' || c >= '\uff61' && c < '\uffa0';
    }

    protected char getCurrentCharacter(int current, char[] cc, PdfChunk[] ck) {
        if (ck == null) {
            return cc[current];
        }
        return (char)ck[Math.min(current, ck.length - 1)].getUnicodeEquivalent(cc[current]);
    }
}

