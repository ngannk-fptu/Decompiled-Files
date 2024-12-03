/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTFontFamily;

public class UnresolvedFontFamily
implements GVTFontFamily {
    protected GVTFontFace fontFace;

    public UnresolvedFontFamily(GVTFontFace fontFace) {
        this.fontFace = fontFace;
    }

    public UnresolvedFontFamily(String familyName) {
        this(new GVTFontFace(familyName));
    }

    @Override
    public GVTFontFace getFontFace() {
        return this.fontFace;
    }

    @Override
    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }

    @Override
    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
        return null;
    }

    @Override
    public GVTFont deriveFont(float size, Map attrs) {
        return null;
    }

    @Override
    public boolean isComplex() {
        return false;
    }
}

