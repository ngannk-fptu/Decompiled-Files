/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.io.IOException;
import org.apache.xmlgraphics.java2d.TextHandler;

public class StrokingTextHandler
implements TextHandler {
    @Override
    public void drawString(Graphics2D g2d, String text, float x, float y) throws IOException {
        Font awtFont = g2d.getFont();
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = awtFont.createGlyphVector(frc, text);
        Shape glyphOutline = gv.getOutline(x, y);
        g2d.fill(glyphOutline);
    }
}

