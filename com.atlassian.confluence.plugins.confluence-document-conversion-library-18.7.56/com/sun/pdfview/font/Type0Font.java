/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.CIDFontType0;
import com.sun.pdfview.font.PDFFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.PDFGlyph;
import java.io.IOException;

public class Type0Font
extends PDFFont {
    PDFFont[] fonts;

    public Type0Font(String baseFont, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, descriptor);
        PDFObject[] descendantFonts = fontObj.getDictRef("DescendantFonts").getArray();
        this.fonts = new PDFFont[descendantFonts.length];
        for (int i = 0; i < descendantFonts.length; ++i) {
            PDFFont descFont = PDFFont.getFont(descendantFonts[i], null);
            if (descFont instanceof CIDFontType0) {
                ((CIDFontType0)descFont).parseToUnicodeMap(fontObj);
            }
            this.fonts[i] = descFont;
        }
    }

    public PDFFont getDescendantFont(int fontID) {
        return this.fonts[fontID];
    }

    @Override
    protected PDFGlyph getGlyph(char src, String name) {
        return this.getDescendantFont(0).getGlyph(src, name);
    }
}

