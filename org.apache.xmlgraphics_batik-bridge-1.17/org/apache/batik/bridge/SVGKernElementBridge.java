/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.Kern
 *  org.apache.batik.gvt.font.UnicodeRange
 */
package org.apache.batik.bridge;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGGVTFont;
import org.apache.batik.gvt.font.Kern;
import org.apache.batik.gvt.font.UnicodeRange;
import org.w3c.dom.Element;

public abstract class SVGKernElementBridge
extends AbstractSVGBridge {
    public Kern createKern(BridgeContext ctx, Element kernElement, SVGGVTFont font) {
        int[] secondGlyphs;
        int[] firstGlyphs;
        int[] glyphCodes;
        String token;
        String u1 = kernElement.getAttributeNS(null, "u1");
        String u2 = kernElement.getAttributeNS(null, "u2");
        String g1 = kernElement.getAttributeNS(null, "g1");
        String g2 = kernElement.getAttributeNS(null, "g2");
        String k = kernElement.getAttributeNS(null, "k");
        if (k.length() == 0) {
            k = "0";
        }
        float kernValue = Float.parseFloat(k);
        int firstGlyphLen = 0;
        int secondGlyphLen = 0;
        int[] firstGlyphSet = null;
        int[] secondGlyphSet = null;
        ArrayList<UnicodeRange> firstUnicodeRanges = new ArrayList<UnicodeRange>();
        ArrayList<UnicodeRange> secondUnicodeRanges = new ArrayList<UnicodeRange>();
        StringTokenizer st = new StringTokenizer(u1, ",");
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.startsWith("U+")) {
                firstUnicodeRanges.add(new UnicodeRange(token));
                continue;
            }
            glyphCodes = font.getGlyphCodesForUnicode(token);
            if (firstGlyphSet == null) {
                firstGlyphSet = glyphCodes;
                firstGlyphLen = glyphCodes.length;
                continue;
            }
            if (firstGlyphLen + glyphCodes.length > firstGlyphSet.length) {
                int sz = firstGlyphSet.length * 2;
                if (sz < firstGlyphLen + glyphCodes.length) {
                    sz = firstGlyphLen + glyphCodes.length;
                }
                int[] tmp = new int[sz];
                System.arraycopy(firstGlyphSet, 0, tmp, 0, firstGlyphLen);
                firstGlyphSet = tmp;
            }
            for (int glyphCode : glyphCodes) {
                firstGlyphSet[firstGlyphLen++] = glyphCode;
            }
        }
        st = new StringTokenizer(u2, ",");
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.startsWith("U+")) {
                secondUnicodeRanges.add(new UnicodeRange(token));
                continue;
            }
            glyphCodes = font.getGlyphCodesForUnicode(token);
            if (secondGlyphSet == null) {
                secondGlyphSet = glyphCodes;
                secondGlyphLen = glyphCodes.length;
                continue;
            }
            if (secondGlyphLen + glyphCodes.length > secondGlyphSet.length) {
                int sz = secondGlyphSet.length * 2;
                if (sz < secondGlyphLen + glyphCodes.length) {
                    sz = secondGlyphLen + glyphCodes.length;
                }
                int[] tmp = new int[sz];
                System.arraycopy(secondGlyphSet, 0, tmp, 0, secondGlyphLen);
                secondGlyphSet = tmp;
            }
            for (int glyphCode : glyphCodes) {
                secondGlyphSet[secondGlyphLen++] = glyphCode;
            }
        }
        st = new StringTokenizer(g1, ",");
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            glyphCodes = font.getGlyphCodesForName(token);
            if (firstGlyphSet == null) {
                firstGlyphSet = glyphCodes;
                firstGlyphLen = glyphCodes.length;
                continue;
            }
            if (firstGlyphLen + glyphCodes.length > firstGlyphSet.length) {
                int sz = firstGlyphSet.length * 2;
                if (sz < firstGlyphLen + glyphCodes.length) {
                    sz = firstGlyphLen + glyphCodes.length;
                }
                int[] tmp = new int[sz];
                System.arraycopy(firstGlyphSet, 0, tmp, 0, firstGlyphLen);
                firstGlyphSet = tmp;
            }
            for (int glyphCode : glyphCodes) {
                firstGlyphSet[firstGlyphLen++] = glyphCode;
            }
        }
        st = new StringTokenizer(g2, ",");
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            glyphCodes = font.getGlyphCodesForName(token);
            if (secondGlyphSet == null) {
                secondGlyphSet = glyphCodes;
                secondGlyphLen = glyphCodes.length;
                continue;
            }
            if (secondGlyphLen + glyphCodes.length > secondGlyphSet.length) {
                int sz = secondGlyphSet.length * 2;
                if (sz < secondGlyphLen + glyphCodes.length) {
                    sz = secondGlyphLen + glyphCodes.length;
                }
                int[] tmp = new int[sz];
                System.arraycopy(secondGlyphSet, 0, tmp, 0, secondGlyphLen);
                secondGlyphSet = tmp;
            }
            for (int glyphCode : glyphCodes) {
                secondGlyphSet[secondGlyphLen++] = glyphCode;
            }
        }
        if (firstGlyphLen == 0 || firstGlyphLen == firstGlyphSet.length) {
            firstGlyphs = firstGlyphSet;
        } else {
            firstGlyphs = new int[firstGlyphLen];
            System.arraycopy(firstGlyphSet, 0, firstGlyphs, 0, firstGlyphLen);
        }
        if (secondGlyphLen == 0 || secondGlyphLen == secondGlyphSet.length) {
            secondGlyphs = secondGlyphSet;
        } else {
            secondGlyphs = new int[secondGlyphLen];
            System.arraycopy(secondGlyphSet, 0, secondGlyphs, 0, secondGlyphLen);
        }
        UnicodeRange[] firstRanges = new UnicodeRange[firstUnicodeRanges.size()];
        firstUnicodeRanges.toArray(firstRanges);
        UnicodeRange[] secondRanges = new UnicodeRange[secondUnicodeRanges.size()];
        secondUnicodeRanges.toArray(secondRanges);
        return new Kern(firstGlyphs, secondGlyphs, firstRanges, secondRanges, kernValue);
    }
}

