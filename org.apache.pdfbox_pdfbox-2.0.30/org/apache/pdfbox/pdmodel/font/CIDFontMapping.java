/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.ttf.OpenTypeFont
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.pdfbox.pdmodel.font.FontMapping;

public final class CIDFontMapping
extends FontMapping<OpenTypeFont> {
    private final FontBoxFont ttf;

    public CIDFontMapping(OpenTypeFont font, FontBoxFont fontBoxFont, boolean isFallback) {
        super(font, isFallback);
        this.ttf = fontBoxFont;
    }

    public FontBoxFont getTrueTypeFont() {
        return this.ttf;
    }

    public boolean isCIDFont() {
        return this.getFont() != null;
    }
}

