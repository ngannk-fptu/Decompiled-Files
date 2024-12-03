/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.ttf.HorizontalMetricsTable
 *  org.apache.fontbox.ttf.TrueTypeFont
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.font.TrueTypeEmbedder;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;

final class PDTrueTypeFontEmbedder
extends TrueTypeEmbedder {
    private final Encoding fontEncoding;

    PDTrueTypeFontEmbedder(PDDocument document, COSDictionary dict, TrueTypeFont ttf, Encoding encoding) throws IOException {
        super(document, dict, ttf, false);
        dict.setItem(COSName.SUBTYPE, (COSBase)COSName.TRUE_TYPE);
        GlyphList glyphList = GlyphList.getAdobeGlyphList();
        this.fontEncoding = encoding;
        dict.setItem(COSName.ENCODING, encoding.getCOSObject());
        this.fontDescriptor.setSymbolic(false);
        this.fontDescriptor.setNonSymbolic(true);
        dict.setItem(COSName.FONT_DESC, (COSObjectable)this.fontDescriptor);
        this.setWidths(dict, glyphList);
    }

    private void setWidths(COSDictionary font, GlyphList glyphList) throws IOException {
        float scaling = 1000.0f / (float)this.ttf.getHeader().getUnitsPerEm();
        HorizontalMetricsTable hmtx = this.ttf.getHorizontalMetrics();
        Map<Integer, String> codeToName = this.getFontEncoding().getCodeToNameMap();
        int firstChar = Collections.min(codeToName.keySet());
        int lastChar = Collections.max(codeToName.keySet());
        ArrayList<Integer> widths = new ArrayList<Integer>(lastChar - firstChar + 1);
        for (int i = 0; i < lastChar - firstChar + 1; ++i) {
            widths.add(0);
        }
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            int code = entry.getKey();
            String name = entry.getValue();
            if (code < firstChar || code > lastChar) continue;
            String uni = glyphList.toUnicode(name);
            int charCode = uni.codePointAt(0);
            int gid = this.cmapLookup.getGlyphId(charCode);
            widths.set(entry.getKey() - firstChar, Math.round((float)hmtx.getAdvanceWidth(gid) * scaling));
        }
        font.setInt(COSName.FIRST_CHAR, firstChar);
        font.setInt(COSName.LAST_CHAR, lastChar);
        font.setItem(COSName.WIDTHS, (COSBase)COSArrayList.converterToCOSArray(widths));
    }

    public Encoding getFontEncoding() {
        return this.fontEncoding;
    }

    @Override
    protected void buildSubset(InputStream ttfSubset, String tag, Map<Integer, Integer> gidToCid) throws IOException {
        throw new UnsupportedOperationException();
    }
}

