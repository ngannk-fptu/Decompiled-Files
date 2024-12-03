/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTGlyphVector;

public interface AltGlyphHandler {
    public GVTGlyphVector createGlyphVector(FontRenderContext var1, float var2, AttributedCharacterIterator var3);
}

