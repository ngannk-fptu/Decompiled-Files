/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphMetrics;

public interface GVTGlyphVector {
    public GVTFont getFont();

    public FontRenderContext getFontRenderContext();

    public int getGlyphCode(int var1);

    public int[] getGlyphCodes(int var1, int var2, int[] var3);

    public GlyphJustificationInfo getGlyphJustificationInfo(int var1);

    public Shape getGlyphLogicalBounds(int var1);

    public GVTGlyphMetrics getGlyphMetrics(int var1);

    public Shape getGlyphOutline(int var1);

    public Rectangle2D getGlyphCellBounds(int var1);

    public Point2D getGlyphPosition(int var1);

    public float[] getGlyphPositions(int var1, int var2, float[] var3);

    public AffineTransform getGlyphTransform(int var1);

    public Shape getGlyphVisualBounds(int var1);

    public Rectangle2D getLogicalBounds();

    public int getNumGlyphs();

    public Shape getOutline();

    public Shape getOutline(float var1, float var2);

    public Rectangle2D getGeometricBounds();

    public Rectangle2D getBounds2D(AttributedCharacterIterator var1);

    public void performDefaultLayout();

    public void setGlyphPosition(int var1, Point2D var2);

    public void setGlyphTransform(int var1, AffineTransform var2);

    public void setGlyphVisible(int var1, boolean var2);

    public boolean isGlyphVisible(int var1);

    public int getCharacterCount(int var1, int var2);

    public boolean isReversed();

    public void maybeReverse(boolean var1);

    public void draw(Graphics2D var1, AttributedCharacterIterator var2);
}

