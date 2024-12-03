/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.awt.font.FontRenderContext;
import java.text.CharacterIterator;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;

public interface GVTFont {
    public boolean canDisplay(char var1);

    public int canDisplayUpTo(char[] var1, int var2, int var3);

    public int canDisplayUpTo(CharacterIterator var1, int var2, int var3);

    public int canDisplayUpTo(String var1);

    public GVTGlyphVector createGlyphVector(FontRenderContext var1, char[] var2);

    public GVTGlyphVector createGlyphVector(FontRenderContext var1, CharacterIterator var2);

    public GVTGlyphVector createGlyphVector(FontRenderContext var1, int[] var2, CharacterIterator var3);

    public GVTGlyphVector createGlyphVector(FontRenderContext var1, String var2);

    public GVTFont deriveFont(float var1);

    public String getFamilyName();

    public GVTLineMetrics getLineMetrics(char[] var1, int var2, int var3, FontRenderContext var4);

    public GVTLineMetrics getLineMetrics(CharacterIterator var1, int var2, int var3, FontRenderContext var4);

    public GVTLineMetrics getLineMetrics(String var1, FontRenderContext var2);

    public GVTLineMetrics getLineMetrics(String var1, int var2, int var3, FontRenderContext var4);

    public float getSize();

    public float getVKern(int var1, int var2);

    public float getHKern(int var1, int var2);

    public String toString();
}

