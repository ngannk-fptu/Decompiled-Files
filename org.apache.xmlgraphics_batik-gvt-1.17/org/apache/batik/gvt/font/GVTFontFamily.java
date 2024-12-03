/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFace;

public interface GVTFontFamily {
    public String getFamilyName();

    public GVTFontFace getFontFace();

    public GVTFont deriveFont(float var1, AttributedCharacterIterator var2);

    public GVTFont deriveFont(float var1, Map var2);

    public boolean isComplex();
}

