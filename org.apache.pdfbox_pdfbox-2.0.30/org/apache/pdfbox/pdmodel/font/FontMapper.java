/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.ttf.TrueTypeFont
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.font.CIDFontMapping;
import org.apache.pdfbox.pdmodel.font.FontMapping;
import org.apache.pdfbox.pdmodel.font.PDCIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;

public interface FontMapper {
    public FontMapping<TrueTypeFont> getTrueTypeFont(String var1, PDFontDescriptor var2);

    public FontMapping<FontBoxFont> getFontBoxFont(String var1, PDFontDescriptor var2);

    public CIDFontMapping getCIDFont(String var1, PDFontDescriptor var2, PDCIDSystemInfo var3);
}

