/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import java.awt.Rectangle;
import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.JustificationInfo;

public interface TextRenderer {
    public void setup(FontContext var1);

    public void drawString(OutputDevice var1, String var2, float var3, float var4);

    public void drawString(OutputDevice var1, String var2, float var3, float var4, JustificationInfo var5);

    public void drawGlyphVector(OutputDevice var1, FSGlyphVector var2, float var3, float var4);

    public FSGlyphVector getGlyphVector(OutputDevice var1, FSFont var2, String var3);

    public float[] getGlyphPositions(OutputDevice var1, FSFont var2, FSGlyphVector var3);

    public Rectangle getGlyphBounds(OutputDevice var1, FSFont var2, FSGlyphVector var3, int var4, float var5, float var6);

    public FSFontMetrics getFSFontMetrics(FontContext var1, FSFont var2, String var3);

    public int getWidth(FontContext var1, FSFont var2, String var3);

    public void setFontScale(float var1);

    public float getFontScale();

    public void setSmoothingThreshold(float var1);

    public int getSmoothingLevel();

    public void setSmoothingLevel(int var1);
}

