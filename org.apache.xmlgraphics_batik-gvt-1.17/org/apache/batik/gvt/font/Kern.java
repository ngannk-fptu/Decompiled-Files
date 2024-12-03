/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.util.Arrays;
import org.apache.batik.gvt.font.UnicodeRange;

public class Kern {
    private int[] firstGlyphCodes;
    private int[] secondGlyphCodes;
    private UnicodeRange[] firstUnicodeRanges;
    private UnicodeRange[] secondUnicodeRanges;
    private float kerningAdjust;

    public Kern(int[] firstGlyphCodes, int[] secondGlyphCodes, UnicodeRange[] firstUnicodeRanges, UnicodeRange[] secondUnicodeRanges, float adjustValue) {
        this.firstGlyphCodes = firstGlyphCodes;
        this.secondGlyphCodes = secondGlyphCodes;
        this.firstUnicodeRanges = firstUnicodeRanges;
        this.secondUnicodeRanges = secondUnicodeRanges;
        this.kerningAdjust = adjustValue;
        if (firstGlyphCodes != null) {
            Arrays.sort(this.firstGlyphCodes);
        }
        if (secondGlyphCodes != null) {
            Arrays.sort(this.secondGlyphCodes);
        }
    }

    public boolean matchesFirstGlyph(int glyphCode, String glyphUnicode) {
        int pt;
        if (this.firstGlyphCodes != null && (pt = Arrays.binarySearch(this.firstGlyphCodes, glyphCode)) >= 0) {
            return true;
        }
        if (glyphUnicode.length() < 1) {
            return false;
        }
        char glyphChar = glyphUnicode.charAt(0);
        for (UnicodeRange firstUnicodeRange : this.firstUnicodeRanges) {
            if (!firstUnicodeRange.contains(glyphChar)) continue;
            return true;
        }
        return false;
    }

    public boolean matchesFirstGlyph(int glyphCode, char glyphUnicode) {
        int pt;
        if (this.firstGlyphCodes != null && (pt = Arrays.binarySearch(this.firstGlyphCodes, glyphCode)) >= 0) {
            return true;
        }
        for (UnicodeRange firstUnicodeRange : this.firstUnicodeRanges) {
            if (!firstUnicodeRange.contains(glyphUnicode)) continue;
            return true;
        }
        return false;
    }

    public boolean matchesSecondGlyph(int glyphCode, String glyphUnicode) {
        int pt;
        if (this.secondGlyphCodes != null && (pt = Arrays.binarySearch(this.secondGlyphCodes, glyphCode)) >= 0) {
            return true;
        }
        if (glyphUnicode.length() < 1) {
            return false;
        }
        char glyphChar = glyphUnicode.charAt(0);
        for (UnicodeRange secondUnicodeRange : this.secondUnicodeRanges) {
            if (!secondUnicodeRange.contains(glyphChar)) continue;
            return true;
        }
        return false;
    }

    public boolean matchesSecondGlyph(int glyphCode, char glyphUnicode) {
        int pt;
        if (this.secondGlyphCodes != null && (pt = Arrays.binarySearch(this.secondGlyphCodes, glyphCode)) >= 0) {
            return true;
        }
        for (UnicodeRange secondUnicodeRange : this.secondUnicodeRanges) {
            if (!secondUnicodeRange.contains(glyphUnicode)) continue;
            return true;
        }
        return false;
    }

    public float getAdjustValue() {
        return this.kerningAdjust;
    }
}

