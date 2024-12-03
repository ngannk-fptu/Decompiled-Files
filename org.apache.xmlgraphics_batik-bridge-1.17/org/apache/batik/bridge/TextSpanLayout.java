/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.GVTGlyphMetrics
 *  org.apache.batik.gvt.font.GVTGlyphVector
 *  org.apache.batik.gvt.font.GVTLineMetrics
 */
package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;

public interface TextSpanLayout {
    public static final int DECORATION_UNDERLINE = 1;
    public static final int DECORATION_STRIKETHROUGH = 2;
    public static final int DECORATION_OVERLINE = 4;
    public static final int DECORATION_ALL = 7;

    public void draw(Graphics2D var1);

    public Shape getDecorationOutline(int var1);

    public Rectangle2D getBounds2D();

    public Rectangle2D getGeometricBounds();

    public Shape getOutline();

    public Point2D getAdvance2D();

    public float[] getGlyphAdvances();

    public GVTGlyphMetrics getGlyphMetrics(int var1);

    public GVTLineMetrics getLineMetrics();

    public Point2D getTextPathAdvance();

    public Point2D getOffset();

    public void setScale(float var1, float var2, boolean var3);

    public void setOffset(Point2D var1);

    public Shape getHighlightShape(int var1, int var2);

    public TextHit hitTestChar(float var1, float var2);

    public boolean isVertical();

    public boolean isOnATextPath();

    public int getGlyphCount();

    public int getCharacterCount(int var1, int var2);

    public int getGlyphIndex(int var1);

    public boolean isLeftToRight();

    public boolean hasCharacterIndex(int var1);

    public GVTGlyphVector getGlyphVector();

    public double getComputedOrientationAngle(int var1);

    public boolean isAltGlyph();

    public boolean isReversed();

    public void maybeReverse(boolean var1);
}

