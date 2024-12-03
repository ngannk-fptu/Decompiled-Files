/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

public class AWTFontFamily
implements GVTFontFamily {
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_DELIMITER = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;
    protected GVTFontFace fontFace;
    protected Font font;

    public AWTFontFamily(GVTFontFace fontFace) {
        this.fontFace = fontFace;
    }

    public AWTFontFamily(String familyName) {
        this(new GVTFontFace(familyName));
    }

    public AWTFontFamily(GVTFontFace fontFace, Font font) {
        this.fontFace = fontFace;
        this.font = font;
    }

    @Override
    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }

    @Override
    public GVTFontFace getFontFace() {
        return this.fontFace;
    }

    @Override
    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
        if (this.font != null) {
            return new AWTGVTFont(this.font, size);
        }
        return this.deriveFont(size, aci.getAttributes());
    }

    @Override
    public GVTFont deriveFont(float size, Map attrs) {
        if (this.font != null) {
            return new AWTGVTFont(this.font, size);
        }
        HashMap<TextAttribute, Object> fontAttributes = new HashMap<TextAttribute, Object>(attrs);
        fontAttributes.put(TextAttribute.SIZE, Float.valueOf(size));
        fontAttributes.put(TextAttribute.FAMILY, this.fontFace.getFamilyName());
        fontAttributes.remove(TEXT_COMPOUND_DELIMITER);
        return new AWTGVTFont(fontAttributes);
    }

    @Override
    public boolean isComplex() {
        return false;
    }
}

