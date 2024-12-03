/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;

public final class SVGGVTGlyphVector
implements GVTGlyphVector {
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    private GVTFont font;
    private Glyph[] glyphs;
    private FontRenderContext frc;
    private GeneralPath outline;
    private Rectangle2D logicalBounds;
    private Rectangle2D bounds2D;
    private Shape[] glyphLogicalBounds;
    private boolean[] glyphVisible;
    private Point2D endPos;
    private TextPaintInfo cacheTPI;

    public SVGGVTGlyphVector(GVTFont font, Glyph[] glyphs, FontRenderContext frc) {
        this.font = font;
        this.glyphs = glyphs;
        this.frc = frc;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
        this.glyphLogicalBounds = new Shape[glyphs.length];
        this.glyphVisible = new boolean[glyphs.length];
        for (int i = 0; i < glyphs.length; ++i) {
            this.glyphVisible[i] = true;
        }
        this.endPos = glyphs[glyphs.length - 1].getPosition();
        this.endPos = new Point2D.Float((float)(this.endPos.getX() + (double)glyphs[glyphs.length - 1].getHorizAdvX()), (float)this.endPos.getY());
    }

    @Override
    public GVTFont getFont() {
        return this.font;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return this.frc;
    }

    @Override
    public int getGlyphCode(int glyphIndex) throws IndexOutOfBoundsException {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex " + glyphIndex + " is out of bounds, should be between 0 and " + (this.glyphs.length - 1));
        }
        return this.glyphs[glyphIndex].getGlyphCode();
    }

    @Override
    public int[] getGlyphCodes(int beginGlyphIndex, int numEntries, int[] codeReturn) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (numEntries < 0) {
            throw new IllegalArgumentException("numEntries argument value, " + numEntries + ", is illegal. It must be > 0.");
        }
        if (beginGlyphIndex < 0) {
            throw new IndexOutOfBoundsException("beginGlyphIndex " + beginGlyphIndex + " is out of bounds, should be between 0 and " + (this.glyphs.length - 1));
        }
        if (beginGlyphIndex + numEntries > this.glyphs.length) {
            throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries (" + beginGlyphIndex + "+" + numEntries + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (codeReturn == null) {
            codeReturn = new int[numEntries];
        }
        for (int i = beginGlyphIndex; i < beginGlyphIndex + numEntries; ++i) {
            codeReturn[i - beginGlyphIndex] = this.glyphs[i].getGlyphCode();
        }
        return codeReturn;
    }

    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + ".");
        }
        return null;
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
        float ascent = 0.0f;
        float descent = 0.0f;
        if (this.font != null) {
            GVTLineMetrics lineMetrics = this.font.getLineMetrics("By", this.frc);
            ascent = lineMetrics.getAscent();
            descent = lineMetrics.getDescent();
            if (descent < 0.0f) {
                descent = -descent;
            }
        }
        if (ascent == 0.0f) {
            float maxAscent = 0.0f;
            float maxDescent = 0.0f;
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
                if (!this.glyphVisible[i]) continue;
                GVTGlyphMetrics glyphMetrics = this.getGlyphMetrics(i);
                Rectangle2D glyphBounds2 = glyphMetrics.getBounds2D();
                ascent = (float)(-glyphBounds2.getMinY());
                descent = (float)(glyphBounds2.getHeight() - (double)ascent);
                if (ascent > maxAscent) {
                    maxAscent = ascent;
                }
                if (!(descent > maxDescent)) continue;
                maxDescent = descent;
            }
            ascent = maxAscent;
            descent = maxDescent;
        }
        Shape[] tempLogicalBounds = new Shape[this.getNumGlyphs()];
        boolean[] rotated = new boolean[this.getNumGlyphs()];
        double maxWidth = -1.0;
        double maxHeight = -1.0;
        for (int i = 0; i < this.getNumGlyphs(); ++i) {
            if (!this.glyphVisible[i]) {
                tempLogicalBounds[i] = null;
                continue;
            }
            AffineTransform glyphTransform = this.getGlyphTransform(i);
            GVTGlyphMetrics glyphMetrics = this.getGlyphMetrics(i);
            glyphBounds = new Rectangle2D.Double(0.0, -ascent, glyphMetrics.getHorizontalAdvance(), ascent + descent);
            if (glyphBounds.isEmpty()) {
                if (i > 0) {
                    rotated[i] = rotated[i - 1];
                    continue;
                }
                rotated[i] = true;
                continue;
            }
            Point2D.Double p1 = new Point2D.Double(glyphBounds.getMinX(), glyphBounds.getMinY());
            Point2D.Double p2 = new Point2D.Double(glyphBounds.getMaxX(), glyphBounds.getMinY());
            Point2D.Double p3 = new Point2D.Double(glyphBounds.getMinX(), glyphBounds.getMaxY());
            Point2D gpos = this.getGlyphPosition(i);
            AffineTransform tr = AffineTransform.getTranslateInstance(gpos.getX(), gpos.getY());
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            tempLogicalBounds[i] = tr.createTransformedShape(glyphBounds);
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
            rotated[i] = Math.abs(tdx12) < 0.001 && Math.abs(tdy13) < 0.001 ? false : !(Math.abs(tdx13) < 0.001) || !(Math.abs(tdy12) < 0.001);
            Rectangle2D rectBounds = tempLogicalBounds[i].getBounds2D();
            if (rectBounds.getWidth() > maxWidth) {
                maxWidth = rectBounds.getWidth();
            }
            if (!(rectBounds.getHeight() > maxHeight)) continue;
            maxHeight = rectBounds.getHeight();
        }
        GeneralPath logicalBoundsPath = new GeneralPath();
        for (int i = 0; i < this.getNumGlyphs(); ++i) {
            if (tempLogicalBounds[i] == null) continue;
            logicalBoundsPath.append(tempLogicalBounds[i], false);
        }
        Rectangle2D fullBounds = logicalBoundsPath.getBounds2D();
        if (fullBounds.getHeight() < maxHeight * 1.5) {
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
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
                tempLogicalBounds[i] = new Rectangle2D.Double(x, fullBounds.getMinY(), width, fullBounds.getHeight());
            }
        } else if (fullBounds.getWidth() < maxWidth * 1.5) {
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
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
                tempLogicalBounds[i] = new Rectangle2D.Double(fullBounds.getMinX(), y, fullBounds.getWidth(), height);
            }
        }
        System.arraycopy(tempLogicalBounds, 0, this.glyphLogicalBounds, 0, this.getNumGlyphs());
    }

    @Override
    public GVTGlyphMetrics getGlyphMetrics(int idx) {
        if (idx < 0 || idx > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        if (idx < this.glyphs.length - 1 && this.font != null) {
            float hkern = this.font.getHKern(this.glyphs[idx].getGlyphCode(), this.glyphs[idx + 1].getGlyphCode());
            float vkern = this.font.getVKern(this.glyphs[idx].getGlyphCode(), this.glyphs[idx + 1].getGlyphCode());
            return this.glyphs[idx].getGlyphMetrics(hkern, vkern);
        }
        return this.glyphs[idx].getGlyphMetrics();
    }

    @Override
    public Shape getGlyphOutline(int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + ".");
        }
        return this.glyphs[glyphIndex].getOutline();
    }

    @Override
    public Rectangle2D getGlyphCellBounds(int glyphIndex) {
        return this.getGlyphLogicalBounds(glyphIndex).getBounds2D();
    }

    @Override
    public Point2D getGlyphPosition(int glyphIndex) {
        if (glyphIndex == this.glyphs.length) {
            return this.endPos;
        }
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        return this.glyphs[glyphIndex].getPosition();
    }

    @Override
    public float[] getGlyphPositions(int beginGlyphIndex, int numEntries, float[] positionReturn) {
        if (numEntries < 0) {
            throw new IllegalArgumentException("numEntries argument value, " + numEntries + ", is illegal. It must be > 0.");
        }
        if (beginGlyphIndex < 0) {
            throw new IndexOutOfBoundsException("beginGlyphIndex " + beginGlyphIndex + " is out of bounds, should be between 0 and " + (this.glyphs.length - 1));
        }
        if (beginGlyphIndex + numEntries > this.glyphs.length + 1) {
            throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries (" + beginGlyphIndex + '+' + numEntries + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (positionReturn == null) {
            positionReturn = new float[numEntries * 2];
        }
        if (beginGlyphIndex + numEntries == this.glyphs.length + 1) {
            positionReturn[--numEntries * 2] = (float)this.endPos.getX();
            positionReturn[numEntries * 2 + 1] = (float)this.endPos.getY();
        }
        for (int i = beginGlyphIndex; i < beginGlyphIndex + numEntries; ++i) {
            Point2D glyphPos = this.glyphs[i].getPosition();
            positionReturn[(i - beginGlyphIndex) * 2] = (float)glyphPos.getX();
            positionReturn[(i - beginGlyphIndex) * 2 + 1] = (float)glyphPos.getY();
        }
        return positionReturn;
    }

    @Override
    public AffineTransform getGlyphTransform(int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        return this.glyphs[glyphIndex].getTransform();
    }

    @Override
    public Shape getGlyphVisualBounds(int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        return this.glyphs[glyphIndex].getOutline();
    }

    @Override
    public Rectangle2D getBounds2D(AttributedCharacterIterator aci) {
        aci.first();
        TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
        if (this.bounds2D != null && TextPaintInfo.equivilent(tpi, this.cacheTPI)) {
            return this.bounds2D;
        }
        Rectangle2D b = null;
        if (tpi.visible) {
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
                Rectangle2D glyphBounds;
                if (!this.glyphVisible[i] || (glyphBounds = this.glyphs[i].getBounds2D()) == null) continue;
                if (b == null) {
                    b = glyphBounds;
                    continue;
                }
                b.add(glyphBounds);
            }
        }
        this.bounds2D = b;
        if (this.bounds2D == null) {
            this.bounds2D = new Rectangle2D.Float();
        }
        this.cacheTPI = new TextPaintInfo(tpi);
        return this.bounds2D;
    }

    @Override
    public Rectangle2D getLogicalBounds() {
        if (this.logicalBounds == null) {
            GeneralPath logicalBoundsPath = new GeneralPath();
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
                Shape glyphLogicalBounds = this.getGlyphLogicalBounds(i);
                if (glyphLogicalBounds == null) continue;
                logicalBoundsPath.append(glyphLogicalBounds, false);
            }
            this.logicalBounds = logicalBoundsPath.getBounds2D();
        }
        return this.logicalBounds;
    }

    @Override
    public int getNumGlyphs() {
        if (this.glyphs != null) {
            return this.glyphs.length;
        }
        return 0;
    }

    @Override
    public Shape getOutline() {
        if (this.outline == null) {
            this.outline = new GeneralPath();
            for (int i = 0; i < this.glyphs.length; ++i) {
                Shape glyphOutline;
                if (!this.glyphVisible[i] || (glyphOutline = this.glyphs[i].getOutline()) == null) continue;
                this.outline.append(glyphOutline, false);
            }
        }
        return this.outline;
    }

    @Override
    public Shape getOutline(float x, float y) {
        Shape outline = this.getOutline();
        AffineTransform tr = AffineTransform.getTranslateInstance(x, y);
        Shape translatedOutline = tr.createTransformedShape(outline);
        return translatedOutline;
    }

    @Override
    public Rectangle2D getGeometricBounds() {
        return this.getOutline().getBounds2D();
    }

    @Override
    public void performDefaultLayout() {
        this.logicalBounds = null;
        this.outline = null;
        this.bounds2D = null;
        float currentX = 0.0f;
        float currentY = 0.0f;
        for (int i = 0; i < this.glyphs.length; ++i) {
            Glyph g = this.glyphs[i];
            g.setTransform(null);
            this.glyphLogicalBounds[i] = null;
            String uni = g.getUnicode();
            if (uni != null && uni.length() != 0 && ArabicTextHandler.arabicCharTransparent(uni.charAt(0))) {
                char ch;
                int j;
                for (j = i + 1; j < this.glyphs.length && (uni = this.glyphs[j].getUnicode()) != null && uni.length() != 0 && ArabicTextHandler.arabicCharTransparent(ch = uni.charAt(0)); ++j) {
                }
                if (j != this.glyphs.length) {
                    Glyph bg = this.glyphs[j];
                    float rEdge = currentX + bg.getHorizAdvX();
                    for (int k = i; k < j; ++k) {
                        g = this.glyphs[k];
                        g.setTransform(null);
                        this.glyphLogicalBounds[i] = null;
                        g.setPosition(new Point2D.Float(rEdge - g.getHorizAdvX(), currentY));
                    }
                    i = j;
                    g = bg;
                }
            }
            g.setPosition(new Point2D.Float(currentX, currentY));
            currentX += g.getHorizAdvX();
        }
        this.endPos = new Point2D.Float(currentX, currentY);
    }

    @Override
    public void setGlyphPosition(int glyphIndex, Point2D newPos) throws IndexOutOfBoundsException {
        if (glyphIndex == this.glyphs.length) {
            this.endPos = (Point2D)newPos.clone();
            return;
        }
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        this.glyphs[glyphIndex].setPosition(newPos);
        this.glyphLogicalBounds[glyphIndex] = null;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
    }

    @Override
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        this.glyphs[glyphIndex].setTransform(newTX);
        this.glyphLogicalBounds[glyphIndex] = null;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
    }

    @Override
    public void setGlyphVisible(int glyphIndex, boolean visible) {
        if (visible == this.glyphVisible[glyphIndex]) {
            return;
        }
        this.glyphVisible[glyphIndex] = visible;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
        this.glyphLogicalBounds[glyphIndex] = null;
    }

    @Override
    public boolean isGlyphVisible(int glyphIndex) {
        return this.glyphVisible[glyphIndex];
    }

    @Override
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        int numChars = 0;
        if (startGlyphIndex < 0) {
            startGlyphIndex = 0;
        }
        if (endGlyphIndex > this.glyphs.length - 1) {
            endGlyphIndex = this.glyphs.length - 1;
        }
        for (int i = startGlyphIndex; i <= endGlyphIndex; ++i) {
            Glyph glyph = this.glyphs[i];
            if (glyph.getGlyphCode() == -1) {
                ++numChars;
                continue;
            }
            String glyphUnicode = glyph.getUnicode();
            numChars += glyphUnicode.length();
        }
        return numChars;
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
        aci.first();
        TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
        if (!tpi.visible) {
            return;
        }
        for (int i = 0; i < this.glyphs.length; ++i) {
            if (!this.glyphVisible[i]) continue;
            this.glyphs[i].draw(graphics2D);
        }
    }
}

