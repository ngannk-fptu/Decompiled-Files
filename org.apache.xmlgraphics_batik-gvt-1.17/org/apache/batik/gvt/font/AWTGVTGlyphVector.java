/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.Platform
 */
package org.apache.batik.gvt.font;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.AWTGlyphGeometryCache;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.util.Platform;

public class AWTGVTGlyphVector
implements GVTGlyphVector {
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    private GlyphVector awtGlyphVector;
    private AWTGVTFont gvtFont;
    private CharacterIterator ci;
    private Point2D[] defaultGlyphPositions;
    private Point2D.Float[] glyphPositions;
    private AffineTransform[] glyphTransforms;
    private Shape[] glyphOutlines;
    private Shape[] glyphVisualBounds;
    private Shape[] glyphLogicalBounds;
    private boolean[] glyphVisible;
    private GVTGlyphMetrics[] glyphMetrics;
    private GeneralPath outline;
    private Rectangle2D visualBounds;
    private Rectangle2D logicalBounds;
    private Rectangle2D bounds2D;
    private double scaleFactor;
    private float ascent;
    private float descent;
    private TextPaintInfo cacheTPI;
    private static final boolean outlinesPositioned;
    private static final boolean drawGlyphVectorWorks;
    private static final boolean glyphVectorTransformWorks;

    public AWTGVTGlyphVector(GlyphVector glyphVector, AWTGVTFont font, double scaleFactor, CharacterIterator ci) {
        this.awtGlyphVector = glyphVector;
        this.gvtFont = font;
        this.scaleFactor = scaleFactor;
        this.ci = ci;
        GVTLineMetrics lineMetrics = this.gvtFont.getLineMetrics("By", this.awtGlyphVector.getFontRenderContext());
        this.ascent = lineMetrics.getAscent();
        this.descent = lineMetrics.getDescent();
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        int numGlyphs = glyphVector.getNumGlyphs();
        this.glyphPositions = new Point2D.Float[numGlyphs + 1];
        this.glyphTransforms = new AffineTransform[numGlyphs];
        this.glyphOutlines = new Shape[numGlyphs];
        this.glyphVisualBounds = new Shape[numGlyphs];
        this.glyphLogicalBounds = new Shape[numGlyphs];
        this.glyphVisible = new boolean[numGlyphs];
        this.glyphMetrics = new GVTGlyphMetrics[numGlyphs];
        for (int i = 0; i < numGlyphs; ++i) {
            this.glyphVisible[i] = true;
        }
    }

    @Override
    public GVTFont getFont() {
        return this.gvtFont;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return this.awtGlyphVector.getFontRenderContext();
    }

    @Override
    public int getGlyphCode(int glyphIndex) {
        return this.awtGlyphVector.getGlyphCode(glyphIndex);
    }

    @Override
    public int[] getGlyphCodes(int beginGlyphIndex, int numEntries, int[] codeReturn) {
        return this.awtGlyphVector.getGlyphCodes(beginGlyphIndex, numEntries, codeReturn);
    }

    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
        return this.awtGlyphVector.getGlyphJustificationInfo(glyphIndex);
    }

    @Override
    public Rectangle2D getBounds2D(AttributedCharacterIterator aci) {
        aci.first();
        TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
        if (this.bounds2D != null && TextPaintInfo.equivilent(tpi, this.cacheTPI)) {
            return this.bounds2D;
        }
        if (tpi == null) {
            return null;
        }
        if (!tpi.visible) {
            return null;
        }
        this.cacheTPI = new TextPaintInfo(tpi);
        Shape outline = null;
        if (tpi.fillPaint != null) {
            outline = this.getOutline();
            this.bounds2D = outline.getBounds2D();
        }
        Stroke stroke = tpi.strokeStroke;
        Paint paint = tpi.strokePaint;
        if (stroke != null && paint != null) {
            if (outline == null) {
                outline = this.getOutline();
            }
            Rectangle2D strokeBounds = stroke.createStrokedShape(outline).getBounds2D();
            if (this.bounds2D == null) {
                this.bounds2D = strokeBounds;
            } else {
                this.bounds2D.add(strokeBounds);
            }
        }
        if (this.bounds2D == null) {
            return null;
        }
        if (this.bounds2D.getWidth() == 0.0 || this.bounds2D.getHeight() == 0.0) {
            this.bounds2D = null;
        }
        return this.bounds2D;
    }

    @Override
    public Rectangle2D getLogicalBounds() {
        if (this.logicalBounds == null) {
            this.computeGlyphLogicalBounds();
        }
        return this.logicalBounds;
    }

    @Override
    public Shape getGlyphLogicalBounds(int glyphIndex) {
        if (this.glyphLogicalBounds[glyphIndex] == null && this.glyphVisible[glyphIndex]) {
            this.computeGlyphLogicalBounds();
        }
        return this.glyphLogicalBounds[glyphIndex];
    }

    private void computeGlyphLogicalBounds() {
        Rectangle2D ngb;
        Rectangle2D glyphBounds;
        int i;
        Shape[] tempLogicalBounds = new Shape[this.getNumGlyphs()];
        boolean[] rotated = new boolean[this.getNumGlyphs()];
        double maxWidth = -1.0;
        double maxHeight = -1.0;
        for (int i2 = 0; i2 < this.getNumGlyphs(); ++i2) {
            float glyphHeight;
            if (!this.glyphVisible[i2]) {
                tempLogicalBounds[i2] = null;
                continue;
            }
            AffineTransform glyphTransform = this.getGlyphTransform(i2);
            float glyphX = 0.0f;
            float glyphY = (float)((double)(-this.ascent) / this.scaleFactor);
            GVTGlyphMetrics glyphMetrics = this.getGlyphMetrics(i2);
            float glyphWidth = (float)((double)glyphMetrics.getHorizontalAdvance() / this.scaleFactor);
            Rectangle2D.Double glyphBounds2 = new Rectangle2D.Double(glyphX, glyphY, glyphWidth, glyphHeight = (float)((double)glyphMetrics.getVerticalAdvance() / this.scaleFactor));
            if (((RectangularShape)glyphBounds2).isEmpty()) {
                if (i2 > 0) {
                    rotated[i2] = rotated[i2 - 1];
                    continue;
                }
                rotated[i2] = true;
                continue;
            }
            Point2D.Double p1 = new Point2D.Double(glyphBounds2.getMinX(), glyphBounds2.getMinY());
            Point2D.Double p2 = new Point2D.Double(glyphBounds2.getMaxX(), glyphBounds2.getMinY());
            Point2D.Double p3 = new Point2D.Double(glyphBounds2.getMinX(), glyphBounds2.getMaxY());
            Point2D gpos = this.getGlyphPosition(i2);
            AffineTransform tr = AffineTransform.getTranslateInstance(gpos.getX(), gpos.getY());
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            tr.scale(this.scaleFactor, this.scaleFactor);
            tempLogicalBounds[i2] = tr.createTransformedShape(glyphBounds2);
            Point2D.Double tp1 = new Point2D.Double();
            Point2D.Double tp2 = new Point2D.Double();
            Point2D.Double tp3 = new Point2D.Double();
            tr.transform(p1, tp1);
            tr.transform(p2, tp2);
            tr.transform(p3, tp3);
            double tdx12 = ((Point2D)tp1).getX() - ((Point2D)tp2).getX();
            double tdx13 = ((Point2D)tp1).getX() - ((Point2D)tp3).getX();
            double tdy12 = ((Point2D)tp1).getY() - ((Point2D)tp2).getY();
            double tdy13 = ((Point2D)tp1).getY() - ((Point2D)tp3).getY();
            rotated[i2] = !(Math.abs(tdx12) < 0.001 && Math.abs(tdy13) < 0.001 || Math.abs(tdx13) < 0.001 && Math.abs(tdy12) < 0.001);
            Rectangle2D rectBounds = tempLogicalBounds[i2].getBounds2D();
            if (rectBounds.getWidth() > maxWidth) {
                maxWidth = rectBounds.getWidth();
            }
            if (!(rectBounds.getHeight() > maxHeight)) continue;
            maxHeight = rectBounds.getHeight();
        }
        GeneralPath logicalBoundsPath = new GeneralPath();
        for (i = 0; i < this.getNumGlyphs(); ++i) {
            if (tempLogicalBounds[i] == null) continue;
            logicalBoundsPath.append(tempLogicalBounds[i], false);
        }
        this.logicalBounds = logicalBoundsPath.getBounds2D();
        if (this.logicalBounds.getHeight() < maxHeight * 1.5) {
            for (i = 0; i < this.getNumGlyphs(); ++i) {
                double nw;
                if (rotated[i] || tempLogicalBounds[i] == null) continue;
                glyphBounds = tempLogicalBounds[i].getBounds2D();
                double x = glyphBounds.getMinX();
                double width = glyphBounds.getWidth();
                if (i < this.getNumGlyphs() - 1 && tempLogicalBounds[i + 1] != null && (ngb = tempLogicalBounds[i + 1].getBounds2D()).getX() > x && (nw = ngb.getX() - x) < width * 1.15 && nw > width * 0.85) {
                    double delta = (nw - width) * 0.5;
                    width += delta;
                    ngb.setRect(ngb.getX() - delta, ngb.getY(), ngb.getWidth() + delta, ngb.getHeight());
                }
                tempLogicalBounds[i] = new Rectangle2D.Double(x, this.logicalBounds.getMinY(), width, this.logicalBounds.getHeight());
            }
        } else if (this.logicalBounds.getWidth() < maxWidth * 1.5) {
            for (i = 0; i < this.getNumGlyphs(); ++i) {
                double nh;
                if (rotated[i] || tempLogicalBounds[i] == null) continue;
                glyphBounds = tempLogicalBounds[i].getBounds2D();
                double y = glyphBounds.getMinY();
                double height = glyphBounds.getHeight();
                if (i < this.getNumGlyphs() - 1 && tempLogicalBounds[i + 1] != null && (ngb = tempLogicalBounds[i + 1].getBounds2D()).getY() > y && (nh = ngb.getY() - y) < height * 1.15 && nh > height * 0.85) {
                    double delta = (nh - height) * 0.5;
                    height += delta;
                    ngb.setRect(ngb.getX(), ngb.getY() - delta, ngb.getWidth(), ngb.getHeight() + delta);
                }
                tempLogicalBounds[i] = new Rectangle2D.Double(this.logicalBounds.getMinX(), y, this.logicalBounds.getWidth(), height);
            }
        }
        System.arraycopy(tempLogicalBounds, 0, this.glyphLogicalBounds, 0, this.getNumGlyphs());
    }

    @Override
    public GVTGlyphMetrics getGlyphMetrics(int glyphIndex) {
        if (this.glyphMetrics[glyphIndex] != null) {
            return this.glyphMetrics[glyphIndex];
        }
        Point2D glyphPos = this.defaultGlyphPositions[glyphIndex];
        char c = this.ci.setIndex(this.ci.getBeginIndex() + glyphIndex);
        this.ci.setIndex(this.ci.getBeginIndex());
        AWTGlyphGeometryCache.Value v = AWTGVTFont.getGlyphGeometry(this.gvtFont, c, this.awtGlyphVector, glyphIndex, glyphPos);
        Rectangle2D gmB = v.getBounds2D();
        Rectangle2D.Double bounds = new Rectangle2D.Double(gmB.getX() * this.scaleFactor, gmB.getY() * this.scaleFactor, gmB.getWidth() * this.scaleFactor, gmB.getHeight() * this.scaleFactor);
        float adv = (float)(this.defaultGlyphPositions[glyphIndex + 1].getX() - this.defaultGlyphPositions[glyphIndex].getX());
        this.glyphMetrics[glyphIndex] = new GVTGlyphMetrics((float)((double)adv * this.scaleFactor), this.ascent + this.descent, bounds, 0);
        return this.glyphMetrics[glyphIndex];
    }

    @Override
    public Shape getGlyphOutline(int glyphIndex) {
        if (this.glyphOutlines[glyphIndex] == null) {
            Point2D glyphPos = this.defaultGlyphPositions[glyphIndex];
            char c = this.ci.setIndex(this.ci.getBeginIndex() + glyphIndex);
            this.ci.setIndex(this.ci.getBeginIndex());
            AWTGlyphGeometryCache.Value v = AWTGVTFont.getGlyphGeometry(this.gvtFont, c, this.awtGlyphVector, glyphIndex, glyphPos);
            Shape glyphOutline = v.getOutline();
            AffineTransform tr = AffineTransform.getTranslateInstance(this.getGlyphPosition(glyphIndex).getX(), this.getGlyphPosition(glyphIndex).getY());
            AffineTransform glyphTransform = this.getGlyphTransform(glyphIndex);
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            tr.scale(this.scaleFactor, this.scaleFactor);
            this.glyphOutlines[glyphIndex] = tr.createTransformedShape(glyphOutline);
        }
        return this.glyphOutlines[glyphIndex];
    }

    static boolean outlinesPositioned() {
        return outlinesPositioned;
    }

    @Override
    public Rectangle2D getGlyphCellBounds(int glyphIndex) {
        return this.getGlyphLogicalBounds(glyphIndex).getBounds2D();
    }

    @Override
    public Point2D getGlyphPosition(int glyphIndex) {
        return this.glyphPositions[glyphIndex];
    }

    @Override
    public float[] getGlyphPositions(int beginGlyphIndex, int numEntries, float[] positionReturn) {
        if (positionReturn == null) {
            positionReturn = new float[numEntries * 2];
        }
        for (int i = beginGlyphIndex; i < beginGlyphIndex + numEntries; ++i) {
            Point2D glyphPos = this.getGlyphPosition(i);
            positionReturn[(i - beginGlyphIndex) * 2] = (float)glyphPos.getX();
            positionReturn[(i - beginGlyphIndex) * 2 + 1] = (float)glyphPos.getY();
        }
        return positionReturn;
    }

    @Override
    public AffineTransform getGlyphTransform(int glyphIndex) {
        return this.glyphTransforms[glyphIndex];
    }

    @Override
    public Shape getGlyphVisualBounds(int glyphIndex) {
        if (this.glyphVisualBounds[glyphIndex] == null) {
            Point2D glyphPos = this.defaultGlyphPositions[glyphIndex];
            char c = this.ci.setIndex(this.ci.getBeginIndex() + glyphIndex);
            this.ci.setIndex(this.ci.getBeginIndex());
            AWTGlyphGeometryCache.Value v = AWTGVTFont.getGlyphGeometry(this.gvtFont, c, this.awtGlyphVector, glyphIndex, glyphPos);
            Rectangle2D glyphBounds = v.getOutlineBounds2D();
            AffineTransform tr = AffineTransform.getTranslateInstance(this.getGlyphPosition(glyphIndex).getX(), this.getGlyphPosition(glyphIndex).getY());
            AffineTransform glyphTransform = this.getGlyphTransform(glyphIndex);
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            tr.scale(this.scaleFactor, this.scaleFactor);
            this.glyphVisualBounds[glyphIndex] = tr.createTransformedShape(glyphBounds);
        }
        return this.glyphVisualBounds[glyphIndex];
    }

    @Override
    public int getNumGlyphs() {
        return this.awtGlyphVector.getNumGlyphs();
    }

    @Override
    public Shape getOutline() {
        if (this.outline != null) {
            return this.outline;
        }
        this.outline = new GeneralPath();
        for (int i = 0; i < this.getNumGlyphs(); ++i) {
            if (!this.glyphVisible[i]) continue;
            Shape glyphOutline = this.getGlyphOutline(i);
            this.outline.append(glyphOutline, false);
        }
        return this.outline;
    }

    @Override
    public Shape getOutline(float x, float y) {
        Shape outline = this.getOutline();
        AffineTransform tr = AffineTransform.getTranslateInstance(x, y);
        outline = tr.createTransformedShape(outline);
        return outline;
    }

    @Override
    public Rectangle2D getGeometricBounds() {
        if (this.visualBounds == null) {
            Shape outline = this.getOutline();
            this.visualBounds = outline.getBounds2D();
        }
        return this.visualBounds;
    }

    @Override
    public void performDefaultLayout() {
        Point2D glyphPos;
        int i;
        if (this.defaultGlyphPositions == null) {
            this.awtGlyphVector.performDefaultLayout();
            this.defaultGlyphPositions = new Point2D.Float[this.getNumGlyphs() + 1];
            for (int i2 = 0; i2 <= this.getNumGlyphs(); ++i2) {
                this.defaultGlyphPositions[i2] = this.awtGlyphVector.getGlyphPosition(i2);
            }
        }
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        float shiftLeft = 0.0f;
        for (i = 0; i < this.getNumGlyphs(); ++i) {
            this.glyphTransforms[i] = null;
            this.glyphVisualBounds[i] = null;
            this.glyphLogicalBounds[i] = null;
            this.glyphOutlines[i] = null;
            this.glyphMetrics[i] = null;
            glyphPos = this.defaultGlyphPositions[i];
            float x = (float)(glyphPos.getX() * this.scaleFactor - (double)shiftLeft);
            float y = (float)(glyphPos.getY() * this.scaleFactor);
            this.ci.setIndex(i + this.ci.getBeginIndex());
            if (this.glyphPositions[i] == null) {
                this.glyphPositions[i] = new Point2D.Float(x, y);
                continue;
            }
            this.glyphPositions[i].x = x;
            this.glyphPositions[i].y = y;
        }
        glyphPos = this.defaultGlyphPositions[i];
        this.glyphPositions[i] = new Point2D.Float((float)(glyphPos.getX() * this.scaleFactor - (double)shiftLeft), (float)(glyphPos.getY() * this.scaleFactor));
    }

    @Override
    public void setGlyphPosition(int glyphIndex, Point2D newPos) {
        this.glyphPositions[glyphIndex].x = (float)newPos.getX();
        this.glyphPositions[glyphIndex].y = (float)newPos.getY();
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        if (glyphIndex != this.getNumGlyphs()) {
            this.glyphVisualBounds[glyphIndex] = null;
            this.glyphLogicalBounds[glyphIndex] = null;
            this.glyphOutlines[glyphIndex] = null;
            this.glyphMetrics[glyphIndex] = null;
        }
    }

    @Override
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
        this.glyphTransforms[glyphIndex] = newTX;
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        this.glyphVisualBounds[glyphIndex] = null;
        this.glyphLogicalBounds[glyphIndex] = null;
        this.glyphOutlines[glyphIndex] = null;
        this.glyphMetrics[glyphIndex] = null;
    }

    @Override
    public void setGlyphVisible(int glyphIndex, boolean visible) {
        if (visible == this.glyphVisible[glyphIndex]) {
            return;
        }
        this.glyphVisible[glyphIndex] = visible;
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        this.glyphVisualBounds[glyphIndex] = null;
        this.glyphLogicalBounds[glyphIndex] = null;
        this.glyphOutlines[glyphIndex] = null;
        this.glyphMetrics[glyphIndex] = null;
    }

    @Override
    public boolean isGlyphVisible(int glyphIndex) {
        return this.glyphVisible[glyphIndex];
    }

    @Override
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        if (startGlyphIndex < 0) {
            startGlyphIndex = 0;
        }
        if (endGlyphIndex >= this.getNumGlyphs()) {
            endGlyphIndex = this.getNumGlyphs() - 1;
        }
        int charCount = 0;
        int start = startGlyphIndex + this.ci.getBeginIndex();
        int end = endGlyphIndex + this.ci.getBeginIndex();
        char c = this.ci.setIndex(start);
        while (this.ci.getIndex() <= end) {
            charCount += ArabicTextHandler.getNumChars(c);
            c = this.ci.next();
        }
        return charCount;
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public void maybeReverse(boolean mirror) {
    }

    @Override
    public void draw(Graphics2D graphics2D, AttributedCharacterIterator aci) {
        AffineTransform at;
        int type;
        int numGlyphs = this.getNumGlyphs();
        aci.first();
        TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO);
        if (tpi == null) {
            return;
        }
        if (!tpi.visible) {
            return;
        }
        Paint fillPaint = tpi.fillPaint;
        Stroke stroke = tpi.strokeStroke;
        Paint strokePaint = tpi.strokePaint;
        if (fillPaint == null && (strokePaint == null || stroke == null)) {
            return;
        }
        boolean useHinting = drawGlyphVectorWorks;
        if (useHinting && stroke != null && strokePaint != null) {
            useHinting = false;
        }
        if (useHinting && fillPaint != null && !(fillPaint instanceof Color)) {
            useHinting = false;
        }
        if (useHinting) {
            Object v1 = graphics2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            Object v2 = graphics2D.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            if (v1 == RenderingHints.VALUE_TEXT_ANTIALIAS_ON && v2 == RenderingHints.VALUE_STROKE_PURE) {
                useHinting = false;
            }
        }
        int typeGRot = 16;
        int typeGTrans = 32;
        if (useHinting && (((type = (at = graphics2D.getTransform()).getType()) & 0x20) != 0 || (type & 0x10) != 0)) {
            useHinting = false;
        }
        if (useHinting) {
            for (int i = 0; i < numGlyphs; ++i) {
                int type2;
                if (!this.glyphVisible[i]) {
                    useHinting = false;
                    break;
                }
                AffineTransform at2 = this.glyphTransforms[i];
                if (at2 == null || ((type2 = at2.getType()) & 0xFFFFFFFE) == 0 || glyphVectorTransformWorks && (type2 & 0x20) == 0 && (type2 & 0x10) == 0) continue;
                useHinting = false;
                break;
            }
        }
        if (useHinting) {
            Point2D pos;
            int i;
            double sf = this.scaleFactor;
            double[] mat = new double[6];
            for (i = 0; i < numGlyphs; ++i) {
                pos = this.glyphPositions[i];
                double x = ((Point2D)pos).getX();
                double y = ((Point2D)pos).getY();
                AffineTransform at3 = this.glyphTransforms[i];
                if (at3 != null) {
                    at3.getMatrix(mat);
                    x += mat[4];
                    y += mat[5];
                    if (mat[0] != 1.0 || mat[1] != 0.0 || mat[2] != 0.0 || mat[3] != 1.0) {
                        mat[4] = 0.0;
                        mat[5] = 0.0;
                        at3 = new AffineTransform(mat);
                    } else {
                        at3 = null;
                    }
                }
                pos = new Point2D.Double(x / sf, y / sf);
                this.awtGlyphVector.setGlyphPosition(i, pos);
                this.awtGlyphVector.setGlyphTransform(i, at3);
            }
            graphics2D.scale(sf, sf);
            graphics2D.setPaint(fillPaint);
            graphics2D.drawGlyphVector(this.awtGlyphVector, 0.0f, 0.0f);
            graphics2D.scale(1.0 / sf, 1.0 / sf);
            for (i = 0; i < numGlyphs; ++i) {
                pos = this.defaultGlyphPositions[i];
                this.awtGlyphVector.setGlyphPosition(i, pos);
                this.awtGlyphVector.setGlyphTransform(i, null);
            }
        } else {
            Shape outline = this.getOutline();
            if (fillPaint != null) {
                graphics2D.setPaint(fillPaint);
                graphics2D.fill(outline);
            }
            if (stroke != null && strokePaint != null) {
                graphics2D.setStroke(stroke);
                graphics2D.setPaint(strokePaint);
                graphics2D.draw(outline);
            }
        }
    }

    static {
        String s = System.getProperty("java.specification.version");
        if ("1.6".compareTo(s) <= 0) {
            outlinesPositioned = true;
            drawGlyphVectorWorks = false;
            glyphVectorTransformWorks = true;
        } else if ("1.4".compareTo(s) <= 0) {
            outlinesPositioned = true;
            drawGlyphVectorWorks = true;
            glyphVectorTransformWorks = true;
        } else if (Platform.isOSX) {
            outlinesPositioned = true;
            drawGlyphVectorWorks = false;
            glyphVectorTransformWorks = false;
        } else {
            outlinesPositioned = false;
            drawGlyphVectorWorks = true;
            glyphVectorTransformWorks = false;
        }
    }
}

