/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.BuiltinFont;
import com.sun.pdfview.font.PDFCMap;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.PDFGlyph;
import java.io.IOException;

public class CIDFontType0
extends BuiltinFont {
    private PDFCMap glyphLookupMap;

    public CIDFontType0(String baseFont, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, fontObj, descriptor);
    }

    public void parseToUnicodeMap(PDFObject fontObj) throws IOException {
        PDFObject toUnicode = fontObj.getDictRef("ToUnicode");
        if (toUnicode != null) {
            PDFCMap cmap;
            this.glyphLookupMap = cmap = PDFCMap.getCMap(toUnicode);
        }
    }

    @Override
    protected PDFGlyph getGlyph(char src, String name) {
        if (this.glyphLookupMap != null) {
            src = this.glyphLookupMap.map(src);
        }
        return super.getGlyph(src, name);
    }
}

