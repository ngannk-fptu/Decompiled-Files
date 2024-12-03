/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.AWTGVTFont
 *  org.apache.batik.gvt.font.AltGlyphHandler
 *  org.apache.batik.gvt.font.GVTFont
 *  org.apache.batik.gvt.font.GVTGlyphMetrics
 *  org.apache.batik.gvt.font.GVTGlyphVector
 *  org.apache.batik.gvt.font.GVTLineMetrics
 *  org.apache.batik.gvt.text.ArabicTextHandler
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPath
 */
package org.apache.batik.bridge;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.HashSet;
import java.util.Set;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.bridge.TextSpanLayout;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPath;

public class GlyphLayout
implements TextSpanLayout {
    protected GVTGlyphVector gv;
    private GVTFont font;
    private GVTLineMetrics metrics;
    private AttributedCharacterIterator aci;
    protected Point2D advance;
    private Point2D offset;
    private float xScale = 1.0f;
    private float yScale = 1.0f;
    private TextPath textPath;
    private Point2D textPathAdvance;
    private int[] charMap;
    private boolean vertical;
    private boolean adjSpacing = true;
    private float[] glyphAdvances;
    private boolean isAltGlyph;
    protected boolean layoutApplied = false;
    private boolean spacingApplied = false;
    private boolean pathApplied = false;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_EMPTY_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_EMPTY_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute LINE_HEIGHT = GVTAttributedCharacterIterator.TextAttribute.LINE_HEIGHT;
    public static final AttributedCharacterIterator.Attribute VERTICAL_ORIENTATION = GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION;
    public static final AttributedCharacterIterator.Attribute VERTICAL_ORIENTATION_ANGLE = GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE;
    public static final AttributedCharacterIterator.Attribute HORIZONTAL_ORIENTATION_ANGLE = GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE;
    private static final AttributedCharacterIterator.Attribute X = GVTAttributedCharacterIterator.TextAttribute.X;
    private static final AttributedCharacterIterator.Attribute Y = GVTAttributedCharacterIterator.TextAttribute.Y;
    private static final AttributedCharacterIterator.Attribute DX = GVTAttributedCharacterIterator.TextAttribute.DX;
    private static final AttributedCharacterIterator.Attribute DY = GVTAttributedCharacterIterator.TextAttribute.DY;
    private static final AttributedCharacterIterator.Attribute ROTATION = GVTAttributedCharacterIterator.TextAttribute.ROTATION;
    private static final AttributedCharacterIterator.Attribute BASELINE_SHIFT = GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT;
    private static final AttributedCharacterIterator.Attribute WRITING_MODE = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE;
    private static final Integer WRITING_MODE_TTB = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB;
    private static final Integer ORIENTATION_AUTO = GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_AUTO;
    public static final AttributedCharacterIterator.Attribute GVT_FONT = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;
    protected static Set runAtts = new HashSet();
    protected static Set szAtts;
    public static final double eps = 1.0E-5;

    public GlyphLayout(AttributedCharacterIterator aci, int[] charMap, Point2D offset, FontRenderContext frc) {
        this.aci = aci;
        this.offset = offset;
        this.font = this.getFont();
        this.charMap = charMap;
        this.metrics = this.font.getLineMetrics((CharacterIterator)aci, aci.getBeginIndex(), aci.getEndIndex(), frc);
        this.gv = null;
        this.aci.first();
        this.vertical = aci.getAttribute(WRITING_MODE) == WRITING_MODE_TTB;
        this.textPath = (TextPath)aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);
        AltGlyphHandler altGlyphHandler = (AltGlyphHandler)this.aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER);
        if (altGlyphHandler != null) {
            this.gv = altGlyphHandler.createGlyphVector(frc, this.font.getSize(), this.aci);
            if (this.gv != null) {
                this.isAltGlyph = true;
            }
        }
        if (this.gv == null) {
            this.gv = this.font.createGlyphVector(frc, (CharacterIterator)this.aci);
        }
    }

    @Override
    public GVTGlyphVector getGlyphVector() {
        return this.gv;
    }

    @Override
    public Point2D getOffset() {
        return this.offset;
    }

    @Override
    public void setScale(float xScale, float yScale, boolean adjSpacing) {
        if (this.vertical) {
            xScale = 1.0f;
        } else {
            yScale = 1.0f;
        }
        if (xScale != this.xScale || yScale != this.yScale || adjSpacing != this.adjSpacing) {
            this.xScale = xScale;
            this.yScale = yScale;
            this.adjSpacing = adjSpacing;
            this.spacingApplied = false;
            this.glyphAdvances = null;
            this.pathApplied = false;
        }
    }

    @Override
    public void setOffset(Point2D offset) {
        if (offset.getX() != this.offset.getX() || offset.getY() != this.offset.getY()) {
            if (this.layoutApplied || this.spacingApplied) {
                float dx = (float)(offset.getX() - this.offset.getX());
                float dy = (float)(offset.getY() - this.offset.getY());
                int numGlyphs = this.gv.getNumGlyphs();
                float[] gp = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
                Point2D.Float pos = new Point2D.Float();
                for (int i = 0; i <= numGlyphs; ++i) {
                    pos.x = gp[2 * i] + dx;
                    pos.y = gp[2 * i + 1] + dy;
                    this.gv.setGlyphPosition(i, (Point2D)pos);
                }
            }
            this.offset = offset;
            this.pathApplied = false;
        }
    }

    @Override
    public GVTGlyphMetrics getGlyphMetrics(int glyphIndex) {
        return this.gv.getGlyphMetrics(glyphIndex);
    }

    @Override
    public GVTLineMetrics getLineMetrics() {
        return this.metrics;
    }

    @Override
    public boolean isVertical() {
        return this.vertical;
    }

    @Override
    public boolean isOnATextPath() {
        return this.textPath != null;
    }

    @Override
    public int getGlyphCount() {
        return this.gv.getNumGlyphs();
    }

    @Override
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        return this.gv.getCharacterCount(startGlyphIndex, endGlyphIndex);
    }

    @Override
    public boolean isLeftToRight() {
        this.aci.first();
        int bidiLevel = (Integer)this.aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL);
        return (bidiLevel & 1) == 0;
    }

    private final void syncLayout() {
        if (!this.pathApplied) {
            this.doPathLayout();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.syncLayout();
        this.gv.draw(g2d, this.aci);
    }

    @Override
    public Point2D getAdvance2D() {
        this.adjustTextSpacing();
        return this.advance;
    }

    @Override
    public Shape getOutline() {
        this.syncLayout();
        return this.gv.getOutline();
    }

    @Override
    public float[] getGlyphAdvances() {
        if (this.glyphAdvances != null) {
            return this.glyphAdvances;
        }
        if (!this.spacingApplied) {
            this.adjustTextSpacing();
        }
        int numGlyphs = this.gv.getNumGlyphs();
        float[] glyphPos = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
        this.glyphAdvances = new float[numGlyphs + 1];
        int off = 0;
        if (this.isVertical()) {
            off = 1;
        }
        float start = glyphPos[off];
        for (int i = 0; i < numGlyphs + 1; ++i) {
            this.glyphAdvances[i] = glyphPos[2 * i + off] - start;
        }
        return this.glyphAdvances;
    }

    @Override
    public Shape getDecorationOutline(int decorationType) {
        this.syncLayout();
        GeneralPath g = new GeneralPath();
        if ((decorationType & 1) != 0) {
            g.append(this.getUnderlineShape(), false);
        }
        if ((decorationType & 2) != 0) {
            g.append(this.getStrikethroughShape(), false);
        }
        if ((decorationType & 4) != 0) {
            g.append(this.getOverlineShape(), false);
        }
        return g;
    }

    @Override
    public Rectangle2D getBounds2D() {
        this.syncLayout();
        return this.gv.getBounds2D(this.aci);
    }

    @Override
    public Rectangle2D getGeometricBounds() {
        this.syncLayout();
        Rectangle2D gvB = this.gv.getGeometricBounds();
        Rectangle2D decB = this.getDecorationOutline(7).getBounds2D();
        return gvB.createUnion(decB);
    }

    @Override
    public Point2D getTextPathAdvance() {
        this.syncLayout();
        if (this.textPath != null) {
            return this.textPathAdvance;
        }
        return this.getAdvance2D();
    }

    @Override
    public int getGlyphIndex(int charIndex) {
        int numGlyphs = this.getGlyphCount();
        int j = 0;
        for (int i = 0; i < numGlyphs; ++i) {
            int count = this.getCharacterCount(i, i);
            for (int n = 0; n < count; ++n) {
                int glyphCharIndex;
                if (charIndex == (glyphCharIndex = this.charMap[j++])) {
                    return i;
                }
                if (j < this.charMap.length) continue;
                return -1;
            }
        }
        return -1;
    }

    public int getLastGlyphIndex(int charIndex) {
        int numGlyphs = this.getGlyphCount();
        int j = this.charMap.length - 1;
        for (int i = numGlyphs - 1; i >= 0; --i) {
            int count = this.getCharacterCount(i, i);
            for (int n = 0; n < count; ++n) {
                int glyphCharIndex;
                if (charIndex == (glyphCharIndex = this.charMap[j--])) {
                    return i;
                }
                if (j >= 0) continue;
                return -1;
            }
        }
        return -1;
    }

    @Override
    public double getComputedOrientationAngle(int index) {
        if (this.isGlyphOrientationAuto()) {
            if (this.isVertical()) {
                char ch = this.aci.setIndex(index);
                if (this.isLatinChar(ch)) {
                    return 90.0;
                }
                return 0.0;
            }
            return 0.0;
        }
        return this.getGlyphOrientationAngle();
    }

    @Override
    public Shape getHighlightShape(int beginCharIndex, int endCharIndex) {
        this.syncLayout();
        if (beginCharIndex > endCharIndex) {
            int temp = beginCharIndex;
            beginCharIndex = endCharIndex;
            endCharIndex = temp;
        }
        GeneralPath shape = null;
        int numGlyphs = this.getGlyphCount();
        Point2D.Float[] topPts = new Point2D.Float[2 * numGlyphs];
        Point2D.Float[] botPts = new Point2D.Float[2 * numGlyphs];
        int ptIdx = 0;
        int currentChar = 0;
        for (int i = 0; i < numGlyphs; ++i) {
            Shape gbounds;
            int glyphCharIndex = this.charMap[currentChar];
            if (glyphCharIndex >= beginCharIndex && glyphCharIndex <= endCharIndex && this.gv.isGlyphVisible(i) && (gbounds = this.gv.getGlyphLogicalBounds(i)) != null) {
                if (shape == null) {
                    shape = new GeneralPath();
                }
                float[] pts = new float[6];
                int count = 0;
                int type = -1;
                PathIterator pi = gbounds.getPathIterator(null);
                Point2D.Float firstPt = null;
                while (!pi.isDone()) {
                    type = pi.currentSegment(pts);
                    if (type == 0 || type == 1) {
                        if (count > 4) break;
                        if (count == 4) {
                            if (firstPt == null || firstPt.x != pts[0] || firstPt.y != pts[1]) {
                                break;
                            }
                        } else {
                            Point2D.Float pt = new Point2D.Float(pts[0], pts[1]);
                            if (count == 0) {
                                firstPt = pt;
                            }
                            switch (count) {
                                case 0: {
                                    botPts[ptIdx] = pt;
                                    break;
                                }
                                case 1: {
                                    topPts[ptIdx] = pt;
                                    break;
                                }
                                case 2: {
                                    topPts[ptIdx + 1] = pt;
                                    break;
                                }
                                case 3: {
                                    botPts[ptIdx + 1] = pt;
                                }
                            }
                        }
                    } else if (type != 4 || count < 4 || count > 5) break;
                    ++count;
                    pi.next();
                }
                if (pi.isDone()) {
                    if (botPts[ptIdx] != null && (topPts[ptIdx].x != topPts[ptIdx + 1].x || topPts[ptIdx].y != topPts[ptIdx + 1].y)) {
                        ptIdx += 2;
                    }
                } else {
                    GlyphLayout.addPtsToPath(shape, topPts, botPts, ptIdx);
                    ptIdx = 0;
                    shape.append(gbounds, false);
                }
            }
            if ((currentChar += this.getCharacterCount(i, i)) < this.charMap.length) continue;
            currentChar = this.charMap.length - 1;
        }
        GlyphLayout.addPtsToPath(shape, topPts, botPts, ptIdx);
        return shape;
    }

    public static boolean epsEQ(double a, double b) {
        return a + 1.0E-5 > b && a - 1.0E-5 < b;
    }

    public static int makeConvexHull(Point2D.Float[] pts, int numPts) {
        float c0;
        float dy;
        float soln;
        for (int i = 1; i < numPts; ++i) {
            if (!(pts[i].x < pts[i - 1].x) && (pts[i].x != pts[i - 1].x || !(pts[i].y < pts[i - 1].y))) continue;
            Point2D.Float tmp = pts[i];
            pts[i] = pts[i - 1];
            pts[i - 1] = tmp;
            i = 0;
        }
        Point2D.Float pt0 = pts[0];
        Point2D.Float pt1 = pts[numPts - 1];
        Point2D.Float dxdy = new Point2D.Float(pt1.x - pt0.x, pt1.y - pt0.y);
        float c = dxdy.y * pt0.x - dxdy.x * pt0.y;
        Point2D.Float[] topList = new Point2D.Float[numPts];
        Point2D.Float[] botList = new Point2D.Float[numPts];
        botList[0] = topList[0] = pts[0];
        int nTopPts = 1;
        int nBotPts = 1;
        for (int i = 1; i < numPts - 1; ++i) {
            float c02;
            float dy2;
            float dx;
            Point2D.Float pt = pts[i];
            soln = dxdy.x * pt.y - dxdy.y * pt.x + c;
            if (soln < 0.0f) {
                while (nBotPts >= 2) {
                    pt0 = botList[nBotPts - 2];
                    pt1 = botList[nBotPts - 1];
                    dx = pt1.x - pt0.x;
                    dy2 = pt1.y - pt0.y;
                    c02 = dy2 * pt0.x - dx * pt0.y;
                    soln = dx * pt.y - dy2 * pt.x + c02;
                    if ((double)soln > 1.0E-5) break;
                    if ((double)soln > -1.0E-5) {
                        if (pt1.y < pt.y) {
                            pt = pt1;
                        }
                        break;
                    }
                    --nBotPts;
                }
                int n = --nBotPts;
                ++nBotPts;
                botList[n] = pt;
                continue;
            }
            while (nTopPts >= 2) {
                pt0 = topList[nTopPts - 2];
                pt1 = topList[nTopPts - 1];
                dx = pt1.x - pt0.x;
                dy2 = pt1.y - pt0.y;
                c02 = dy2 * pt0.x - dx * pt0.y;
                soln = dx * pt.y - dy2 * pt.x + c02;
                if ((double)soln < -1.0E-5) break;
                if ((double)soln < 1.0E-5) {
                    if (pt1.y > pt.y) {
                        pt = pt1;
                    }
                    break;
                }
                --nTopPts;
            }
            int n = --nTopPts;
            ++nTopPts;
            topList[n] = pt;
        }
        Point2D.Float pt = pts[numPts - 1];
        while (nBotPts >= 2) {
            pt0 = botList[nBotPts - 2];
            pt1 = botList[nBotPts - 1];
            float dx = pt1.x - pt0.x;
            dy = pt1.y - pt0.y;
            c0 = dy * pt0.x - dx * pt0.y;
            soln = dx * pt.y - dy * pt.x + c0;
            if ((double)soln > 1.0E-5) break;
            if ((double)soln > -1.0E-5) {
                if (!(pt1.y >= pt.y)) break;
                --nBotPts;
                break;
            }
            --nBotPts;
        }
        while (nTopPts >= 2) {
            pt0 = topList[nTopPts - 2];
            pt1 = topList[nTopPts - 1];
            float dx = pt1.x - pt0.x;
            dy = pt1.y - pt0.y;
            c0 = dy * pt0.x - dx * pt0.y;
            soln = dx * pt.y - dy * pt.x + c0;
            if ((double)soln < -1.0E-5) break;
            if ((double)soln < 1.0E-5) {
                if (!(pt1.y <= pt.y)) break;
                --nTopPts;
                break;
            }
            --nTopPts;
        }
        System.arraycopy(topList, 0, pts, 0, nTopPts);
        int i = nTopPts;
        pts[i++] = pts[numPts - 1];
        int n = nBotPts - 1;
        while (n > 0) {
            pts[i] = botList[n];
            --n;
            ++i;
        }
        return i;
    }

    public static void addPtsToPath(GeneralPath shape, Point2D.Float[] topPts, Point2D.Float[] botPts, int numPts) {
        if (numPts < 2) {
            return;
        }
        if (numPts == 2) {
            shape.moveTo(topPts[0].x, topPts[0].y);
            shape.lineTo(topPts[1].x, topPts[1].y);
            shape.lineTo(botPts[1].x, botPts[1].y);
            shape.lineTo(botPts[0].x, botPts[0].y);
            shape.lineTo(topPts[0].x, topPts[0].y);
            return;
        }
        Point2D.Float[] boxes = new Point2D.Float[8];
        Point2D.Float[] chull = new Point2D.Float[8];
        boxes[4] = topPts[0];
        boxes[5] = topPts[1];
        boxes[6] = botPts[1];
        boxes[7] = botPts[0];
        Area[] areas = new Area[numPts / 2];
        int nAreas = 0;
        for (int i = 2; i < numPts; i += 2) {
            boxes[0] = boxes[4];
            boxes[1] = boxes[5];
            boxes[2] = boxes[6];
            boxes[3] = boxes[7];
            boxes[4] = topPts[i];
            boxes[5] = topPts[i + 1];
            boxes[6] = botPts[i + 1];
            boxes[7] = botPts[i];
            float delta = boxes[2].x - boxes[0].x;
            float dist = delta * delta;
            delta = boxes[2].y - boxes[0].y;
            float sz = (float)Math.sqrt(dist += delta * delta);
            delta = boxes[6].x - boxes[4].x;
            dist = delta * delta;
            delta = boxes[6].y - boxes[4].y;
            sz += (float)Math.sqrt(dist += delta * delta);
            delta = (boxes[0].x + boxes[1].x + boxes[2].x + boxes[3].x - (boxes[4].x + boxes[5].x + boxes[6].x + boxes[7].x)) / 4.0f;
            dist = delta * delta;
            delta = (boxes[0].y + boxes[1].y + boxes[2].y + boxes[3].y - (boxes[4].y + boxes[5].y + boxes[6].y + boxes[7].y)) / 4.0f;
            dist += delta * delta;
            dist = (float)Math.sqrt(dist);
            GeneralPath gp = new GeneralPath();
            if (dist < sz) {
                System.arraycopy(boxes, 0, chull, 0, 8);
                int npts = GlyphLayout.makeConvexHull(chull, 8);
                gp.moveTo(chull[0].x, chull[0].y);
                for (int n = 1; n < npts; ++n) {
                    gp.lineTo(chull[n].x, chull[n].y);
                }
                gp.closePath();
            } else {
                GlyphLayout.mergeAreas(shape, areas, nAreas);
                nAreas = 0;
                if (i == 2) {
                    gp.moveTo(boxes[0].x, boxes[0].y);
                    gp.lineTo(boxes[1].x, boxes[1].y);
                    gp.lineTo(boxes[2].x, boxes[2].y);
                    gp.lineTo(boxes[3].x, boxes[3].y);
                    gp.closePath();
                    shape.append(gp, false);
                    gp.reset();
                }
                gp.moveTo(boxes[4].x, boxes[4].y);
                gp.lineTo(boxes[5].x, boxes[5].y);
                gp.lineTo(boxes[6].x, boxes[6].y);
                gp.lineTo(boxes[7].x, boxes[7].y);
                gp.closePath();
            }
            areas[nAreas++] = new Area(gp);
        }
        GlyphLayout.mergeAreas(shape, areas, nAreas);
    }

    public static void mergeAreas(GeneralPath shape, Area[] shapes, int nShapes) {
        while (nShapes > 1) {
            int n = 0;
            for (int i = 1; i < nShapes; i += 2) {
                shapes[i - 1].add(shapes[i]);
                shapes[n++] = shapes[i - 1];
                shapes[i] = null;
            }
            if ((nShapes & 1) == 1) {
                shapes[n - 1].add(shapes[nShapes - 1]);
            }
            nShapes /= 2;
        }
        if (nShapes == 1) {
            shape.append(shapes[0], false);
        }
    }

    @Override
    public TextHit hitTestChar(float x, float y) {
        this.syncLayout();
        TextHit textHit = null;
        int currentChar = 0;
        for (int i = 0; i < this.gv.getNumGlyphs(); ++i) {
            Shape gbounds = this.gv.getGlyphLogicalBounds(i);
            if (gbounds != null) {
                Rectangle2D gbounds2d = gbounds.getBounds2D();
                if (gbounds.contains(x, y)) {
                    boolean isRightHalf = (double)x > gbounds2d.getX() + gbounds2d.getWidth() / 2.0;
                    boolean isLeadingEdge = !isRightHalf;
                    int charIndex = this.charMap[currentChar];
                    textHit = new TextHit(charIndex, isLeadingEdge);
                    return textHit;
                }
            }
            if ((currentChar += this.getCharacterCount(i, i)) < this.charMap.length) continue;
            currentChar = this.charMap.length - 1;
        }
        return textHit;
    }

    protected GVTFont getFont() {
        this.aci.first();
        GVTFont gvtFont = (GVTFont)this.aci.getAttribute(GVT_FONT);
        if (gvtFont != null) {
            return gvtFont;
        }
        return new AWTGVTFont(this.aci.getAttributes());
    }

    protected Shape getOverlineShape() {
        double y = this.metrics.getOverlineOffset();
        float overlineThickness = this.metrics.getOverlineThickness();
        y += (double)overlineThickness;
        this.aci.first();
        Float dy = (Float)this.aci.getAttribute(DY);
        if (dy != null) {
            y += (double)dy.floatValue();
        }
        BasicStroke overlineStroke = new BasicStroke(overlineThickness);
        Rectangle2D logicalBounds = this.gv.getLogicalBounds();
        return overlineStroke.createStrokedShape(new Line2D.Double(logicalBounds.getMinX() + (double)overlineThickness / 2.0, this.offset.getY() + y, logicalBounds.getMaxX() - (double)overlineThickness / 2.0, this.offset.getY() + y));
    }

    protected Shape getUnderlineShape() {
        double y = this.metrics.getUnderlineOffset();
        float underlineThickness = this.metrics.getUnderlineThickness();
        y += (double)underlineThickness * 1.5;
        BasicStroke underlineStroke = new BasicStroke(underlineThickness);
        this.aci.first();
        Float dy = (Float)this.aci.getAttribute(DY);
        if (dy != null) {
            y += (double)dy.floatValue();
        }
        Rectangle2D logicalBounds = this.gv.getLogicalBounds();
        return underlineStroke.createStrokedShape(new Line2D.Double(logicalBounds.getMinX() + (double)underlineThickness / 2.0, this.offset.getY() + y, logicalBounds.getMaxX() - (double)underlineThickness / 2.0, this.offset.getY() + y));
    }

    protected Shape getStrikethroughShape() {
        double y = this.metrics.getStrikethroughOffset();
        float strikethroughThickness = this.metrics.getStrikethroughThickness();
        BasicStroke strikethroughStroke = new BasicStroke(strikethroughThickness);
        this.aci.first();
        Float dy = (Float)this.aci.getAttribute(DY);
        if (dy != null) {
            y += (double)dy.floatValue();
        }
        Rectangle2D logicalBounds = this.gv.getLogicalBounds();
        return strikethroughStroke.createStrokedShape(new Line2D.Double(logicalBounds.getMinX() + (double)strikethroughThickness / 2.0, this.offset.getY() + y, logicalBounds.getMaxX() - (double)strikethroughThickness / 2.0, this.offset.getY() + y));
    }

    protected void doExplicitGlyphLayout() {
        int i;
        this.gv.performDefaultLayout();
        float baselineAscent = this.vertical ? (float)this.gv.getLogicalBounds().getWidth() : this.metrics.getAscent() + Math.abs(this.metrics.getDescent());
        int numGlyphs = this.gv.getNumGlyphs();
        float[] gp = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
        float verticalFirstOffset = 0.0f;
        float horizontalFirstOffset = 0.0f;
        boolean glyphOrientationAuto = this.isGlyphOrientationAuto();
        int glyphOrientationAngle = 0;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = this.getGlyphOrientationAngle();
        }
        int aciStart = this.aci.getBeginIndex();
        int aciIndex = 0;
        char ch = this.aci.first();
        int runLimit = aciIndex + aciStart;
        Float x = null;
        Float y = null;
        Float dx = null;
        Float dy = null;
        Float rotation = null;
        Object baseline = null;
        float shift_x_pos = 0.0f;
        float shift_y_pos = 0.0f;
        float curr_x_pos = (float)this.offset.getX();
        float curr_y_pos = (float)this.offset.getY();
        Point2D.Float pos = new Point2D.Float();
        boolean hasArabicTransparent = false;
        for (i = 0; i < numGlyphs; ++i) {
            float dsc;
            float asc;
            float advY;
            if (aciIndex + aciStart >= runLimit) {
                runLimit = this.aci.getRunLimit(runAtts);
                x = (Float)this.aci.getAttribute(X);
                y = (Float)this.aci.getAttribute(Y);
                dx = (Float)this.aci.getAttribute(DX);
                dy = (Float)this.aci.getAttribute(DY);
                rotation = (Float)this.aci.getAttribute(ROTATION);
                baseline = this.aci.getAttribute(BASELINE_SHIFT);
            }
            GVTGlyphMetrics gm = this.gv.getGlyphMetrics(i);
            if (i == 0) {
                if (this.isVertical()) {
                    if (glyphOrientationAuto) {
                        if (this.isLatinChar(ch)) {
                            verticalFirstOffset = 0.0f;
                        } else {
                            advY = gm.getVerticalAdvance();
                            asc = this.metrics.getAscent();
                            dsc = this.metrics.getDescent();
                            verticalFirstOffset = asc + (advY - (asc + dsc)) / 2.0f;
                        }
                    } else if (glyphOrientationAngle == 0) {
                        advY = gm.getVerticalAdvance();
                        asc = this.metrics.getAscent();
                        dsc = this.metrics.getDescent();
                        verticalFirstOffset = asc + (advY - (asc + dsc)) / 2.0f;
                    } else {
                        verticalFirstOffset = 0.0f;
                    }
                } else {
                    horizontalFirstOffset = glyphOrientationAngle == 270 ? (float)gm.getBounds2D().getHeight() : 0.0f;
                }
            } else if (glyphOrientationAuto && verticalFirstOffset == 0.0f && !this.isLatinChar(ch)) {
                advY = gm.getVerticalAdvance();
                asc = this.metrics.getAscent();
                dsc = this.metrics.getDescent();
                verticalFirstOffset = asc + (advY - (asc + dsc)) / 2.0f;
            }
            float ox = 0.0f;
            float oy = 0.0f;
            float glyphOrientationRotation = 0.0f;
            float glyphRotation = 0.0f;
            if (ch != '\uffff') {
                if (this.vertical) {
                    glyphOrientationRotation = glyphOrientationAuto ? (this.isLatinChar(ch) ? 1.5707964f : 0.0f) : (float)Math.toRadians(glyphOrientationAngle);
                    if (this.textPath != null) {
                        x = null;
                    }
                } else {
                    glyphOrientationRotation = (float)Math.toRadians(glyphOrientationAngle);
                    if (this.textPath != null) {
                        y = null;
                    }
                }
                glyphRotation = rotation == null || rotation.isNaN() ? glyphOrientationRotation : rotation.floatValue() + glyphOrientationRotation;
                if (x != null && !x.isNaN()) {
                    if (i == 0) {
                        shift_x_pos = (float)((double)x.floatValue() - this.offset.getX());
                    }
                    curr_x_pos = x.floatValue() - shift_x_pos;
                }
                if (dx != null && !dx.isNaN()) {
                    curr_x_pos += dx.floatValue();
                }
                if (y != null && !y.isNaN()) {
                    if (i == 0) {
                        shift_y_pos = (float)((double)y.floatValue() - this.offset.getY());
                    }
                    curr_y_pos = y.floatValue() - shift_y_pos;
                }
                if (dy != null && !dy.isNaN()) {
                    curr_y_pos += dy.floatValue();
                } else if (i > 0) {
                    curr_y_pos += gp[i * 2 + 1] - gp[i * 2 - 1];
                }
                float baselineAdjust = 0.0f;
                if (baseline != null) {
                    if (baseline instanceof Integer) {
                        if (baseline == TextAttribute.SUPERSCRIPT_SUPER) {
                            baselineAdjust = baselineAscent * 0.5f;
                        } else if (baseline == TextAttribute.SUPERSCRIPT_SUB) {
                            baselineAdjust = -baselineAscent * 0.5f;
                        }
                    } else if (baseline instanceof Float) {
                        baselineAdjust = ((Float)baseline).floatValue();
                    }
                    if (this.vertical) {
                        ox = baselineAdjust;
                    } else {
                        oy = -baselineAdjust;
                    }
                }
                if (this.vertical) {
                    Rectangle2D glyphBounds;
                    oy += verticalFirstOffset;
                    if (glyphOrientationAuto) {
                        if (this.isLatinChar(ch)) {
                            ox += this.metrics.getStrikethroughOffset();
                        } else {
                            glyphBounds = this.gv.getGlyphVisualBounds(i).getBounds2D();
                            ox -= (float)(glyphBounds.getMaxX() - (double)gp[2 * i] - glyphBounds.getWidth() / 2.0);
                        }
                    } else {
                        glyphBounds = this.gv.getGlyphVisualBounds(i).getBounds2D();
                        ox = glyphOrientationAngle == 0 ? (ox -= (float)(glyphBounds.getMaxX() - (double)gp[2 * i] - glyphBounds.getWidth() / 2.0)) : (glyphOrientationAngle == 180 ? (ox += (float)(glyphBounds.getMaxX() - (double)gp[2 * i] - glyphBounds.getWidth() / 2.0)) : (glyphOrientationAngle == 90 ? (ox += this.metrics.getStrikethroughOffset()) : (ox -= this.metrics.getStrikethroughOffset())));
                    }
                } else {
                    ox += horizontalFirstOffset;
                    if (glyphOrientationAngle == 90) {
                        oy -= gm.getHorizontalAdvance();
                    } else if (glyphOrientationAngle == 180) {
                        oy -= this.metrics.getAscent();
                    }
                }
            }
            pos.x = curr_x_pos + ox;
            pos.y = curr_y_pos + oy;
            this.gv.setGlyphPosition(i, (Point2D)pos);
            if (ArabicTextHandler.arabicCharTransparent((char)ch)) {
                hasArabicTransparent = true;
            } else if (this.vertical) {
                float advanceY = 0.0f;
                if (glyphOrientationAuto) {
                    advanceY = this.isLatinChar(ch) ? gm.getHorizontalAdvance() : gm.getVerticalAdvance();
                } else if (glyphOrientationAngle == 0 || glyphOrientationAngle == 180) {
                    advanceY = gm.getVerticalAdvance();
                } else if (glyphOrientationAngle == 90) {
                    advanceY = gm.getHorizontalAdvance();
                } else {
                    advanceY = gm.getHorizontalAdvance();
                    this.gv.setGlyphTransform(i, AffineTransform.getTranslateInstance(0.0, advanceY));
                }
                curr_y_pos += advanceY;
            } else {
                float advanceX = 0.0f;
                if (glyphOrientationAngle == 0) {
                    advanceX = gm.getHorizontalAdvance();
                } else if (glyphOrientationAngle == 180) {
                    advanceX = gm.getHorizontalAdvance();
                    this.gv.setGlyphTransform(i, AffineTransform.getTranslateInstance(advanceX, 0.0));
                } else {
                    advanceX = gm.getVerticalAdvance();
                }
                curr_x_pos += advanceX;
            }
            if (!GlyphLayout.epsEQ(glyphRotation, 0.0)) {
                AffineTransform glyphTransform = this.gv.getGlyphTransform(i);
                if (glyphTransform == null) {
                    glyphTransform = new AffineTransform();
                }
                AffineTransform rotAt = GlyphLayout.epsEQ(glyphRotation, 1.5707963267948966) ? new AffineTransform(0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f) : (GlyphLayout.epsEQ(glyphRotation, Math.PI) ? new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f) : (GlyphLayout.epsEQ(glyphRotation, 4.71238898038469) ? new AffineTransform(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f) : AffineTransform.getRotateInstance(glyphRotation)));
                glyphTransform.concatenate(rotAt);
                this.gv.setGlyphTransform(i, glyphTransform);
            }
            if ((aciIndex += this.gv.getCharacterCount(i, i)) >= this.charMap.length) {
                aciIndex = this.charMap.length - 1;
            }
            ch = this.aci.setIndex(aciIndex + aciStart);
        }
        pos.x = curr_x_pos;
        pos.y = curr_y_pos;
        this.gv.setGlyphPosition(i, (Point2D)pos);
        this.advance = new Point2D.Float((float)((double)curr_x_pos - this.offset.getX()), (float)((double)curr_y_pos - this.offset.getY()));
        if (hasArabicTransparent) {
            ch = this.aci.first();
            aciIndex = 0;
            int transparentStart = -1;
            for (i = 0; i < numGlyphs; ++i) {
                if (ArabicTextHandler.arabicCharTransparent((char)ch)) {
                    if (transparentStart == -1) {
                        transparentStart = i;
                    }
                } else if (transparentStart != -1) {
                    Point2D loc = this.gv.getGlyphPosition(i);
                    GVTGlyphMetrics gm = this.gv.getGlyphMetrics(i);
                    boolean tyS = false;
                    boolean txS = false;
                    float advX = 0.0f;
                    float advY = 0.0f;
                    if (this.vertical) {
                        if (glyphOrientationAuto || glyphOrientationAngle == 90) {
                            advY = gm.getHorizontalAdvance();
                        } else if (glyphOrientationAngle == 270) {
                            advY = 0.0f;
                        } else {
                            advX = glyphOrientationAngle == 0 ? gm.getHorizontalAdvance() : -gm.getHorizontalAdvance();
                        }
                    } else if (glyphOrientationAngle == 0) {
                        advX = gm.getHorizontalAdvance();
                    } else if (glyphOrientationAngle == 90) {
                        advY = gm.getHorizontalAdvance();
                    } else if (glyphOrientationAngle == 180) {
                        advX = 0.0f;
                    } else {
                        advY = -gm.getHorizontalAdvance();
                    }
                    float baseX = (float)(loc.getX() + (double)advX);
                    float baseY = (float)(loc.getY() + (double)advY);
                    for (int j = transparentStart; j < i; ++j) {
                        Point2D locT = this.gv.getGlyphPosition(j);
                        GVTGlyphMetrics gmT = this.gv.getGlyphMetrics(j);
                        float locX = (float)locT.getX();
                        float locY = (float)locT.getY();
                        float tx = 0.0f;
                        float ty = 0.0f;
                        float advT = gmT.getHorizontalAdvance();
                        if (this.vertical) {
                            if (glyphOrientationAuto || glyphOrientationAngle == 90) {
                                locY = baseY - advT;
                            } else if (glyphOrientationAngle == 270) {
                                locY = baseY + advT;
                            } else {
                                locX = glyphOrientationAngle == 0 ? baseX - advT : baseX + advT;
                            }
                        } else if (glyphOrientationAngle == 0) {
                            locX = baseX - advT;
                        } else if (glyphOrientationAngle == 90) {
                            locY = baseY - advT;
                        } else if (glyphOrientationAngle == 180) {
                            locX = baseX + advT;
                        } else {
                            locY = baseY + advT;
                        }
                        locT = new Point2D.Double(locX, locY);
                        this.gv.setGlyphPosition(j, locT);
                        if (!txS && !tyS) continue;
                        AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
                        at.concatenate(this.gv.getGlyphTransform(i));
                        this.gv.setGlyphTransform(i, at);
                    }
                    transparentStart = -1;
                }
                if ((aciIndex += this.gv.getCharacterCount(i, i)) >= this.charMap.length) {
                    aciIndex = this.charMap.length - 1;
                }
                ch = this.aci.setIndex(aciIndex + aciStart);
            }
        }
        this.layoutApplied = true;
        this.spacingApplied = false;
        this.glyphAdvances = null;
        this.pathApplied = false;
    }

    protected void adjustTextSpacing() {
        if (this.spacingApplied) {
            return;
        }
        if (!this.layoutApplied) {
            this.doExplicitGlyphLayout();
        }
        this.aci.first();
        Boolean customSpacing = (Boolean)this.aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING);
        if (customSpacing != null && customSpacing.booleanValue()) {
            this.advance = this.doSpacing((Float)this.aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.KERNING), (Float)this.aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING), (Float)this.aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING));
            this.layoutApplied = false;
        }
        this.applyStretchTransform(!this.adjSpacing);
        this.spacingApplied = true;
        this.pathApplied = false;
    }

    protected Point2D doSpacing(Float kern, Float letterSpacing, Float wordSpacing) {
        boolean autoKern = true;
        boolean doWordSpacing = false;
        boolean doLetterSpacing = false;
        float kernVal = 0.0f;
        float letterSpacingVal = 0.0f;
        if (kern != null && !kern.isNaN()) {
            kernVal = kern.floatValue();
            autoKern = false;
        }
        if (letterSpacing != null && !letterSpacing.isNaN()) {
            letterSpacingVal = letterSpacing.floatValue();
            doLetterSpacing = true;
        }
        if (wordSpacing != null && !wordSpacing.isNaN()) {
            doWordSpacing = true;
        }
        int numGlyphs = this.gv.getNumGlyphs();
        float dx = 0.0f;
        float dy = 0.0f;
        Point2D[] newPositions = new Point2D[numGlyphs + 1];
        Point2D prevPos = this.gv.getGlyphPosition(0);
        int prevCode = this.gv.getGlyphCode(0);
        float x = (float)prevPos.getX();
        float y = (float)prevPos.getY();
        Point2D.Double lastCharAdvance = new Point2D.Double(this.advance.getX() - (this.gv.getGlyphPosition(numGlyphs - 1).getX() - (double)x), this.advance.getY() - (this.gv.getGlyphPosition(numGlyphs - 1).getY() - (double)y));
        try {
            Point2D gpos;
            int i;
            GVTFont font = this.gv.getFont();
            if (numGlyphs > 1 && (doLetterSpacing || !autoKern)) {
                for (i = 1; i <= numGlyphs; ++i) {
                    gpos = this.gv.getGlyphPosition(i);
                    int currCode = i == numGlyphs ? -1 : this.gv.getGlyphCode(i);
                    dx = (float)gpos.getX() - (float)prevPos.getX();
                    dy = (float)gpos.getY() - (float)prevPos.getY();
                    if (autoKern) {
                        if (this.vertical) {
                            dy += letterSpacingVal;
                        } else {
                            dx += letterSpacingVal;
                        }
                    } else if (this.vertical) {
                        float vKern = 0.0f;
                        if (currCode != -1) {
                            vKern = font.getVKern(prevCode, currCode);
                        }
                        dy += kernVal - vKern + letterSpacingVal;
                    } else {
                        float hKern = 0.0f;
                        if (currCode != -1) {
                            hKern = font.getHKern(prevCode, currCode);
                        }
                        dx += kernVal - hKern + letterSpacingVal;
                    }
                    newPositions[i] = new Point2D.Float(x += dx, y += dy);
                    prevPos = gpos;
                    prevCode = currCode;
                }
                for (i = 1; i <= numGlyphs; ++i) {
                    if (newPositions[i] == null) continue;
                    this.gv.setGlyphPosition(i, newPositions[i]);
                }
            }
            if (this.vertical) {
                ((Point2D)lastCharAdvance).setLocation(((Point2D)lastCharAdvance).getX(), ((Point2D)lastCharAdvance).getY() + (double)kernVal + (double)letterSpacingVal);
            } else {
                ((Point2D)lastCharAdvance).setLocation(((Point2D)lastCharAdvance).getX() + (double)kernVal + (double)letterSpacingVal, ((Point2D)lastCharAdvance).getY());
            }
            dx = 0.0f;
            dy = 0.0f;
            prevPos = this.gv.getGlyphPosition(0);
            x = (float)prevPos.getX();
            y = (float)prevPos.getY();
            if (numGlyphs > 1 && doWordSpacing) {
                for (i = 1; i < numGlyphs; ++i) {
                    gpos = this.gv.getGlyphPosition(i);
                    dx = (float)gpos.getX() - (float)prevPos.getX();
                    dy = (float)gpos.getY() - (float)prevPos.getY();
                    boolean inWS = false;
                    int beginWS = i;
                    int endWS = i;
                    GVTGlyphMetrics gm = this.gv.getGlyphMetrics(i);
                    while (gm.getBounds2D().getWidth() < 0.01 || gm.isWhitespace()) {
                        if (!inWS) {
                            inWS = true;
                        }
                        if (i == numGlyphs - 1) break;
                        ++endWS;
                        gpos = this.gv.getGlyphPosition(++i);
                        gm = this.gv.getGlyphMetrics(i);
                    }
                    if (inWS) {
                        int nWS = endWS - beginWS;
                        float px = (float)prevPos.getX();
                        float py = (float)prevPos.getY();
                        dx = (float)(gpos.getX() - (double)px) / (float)(nWS + 1);
                        dy = (float)(gpos.getY() - (double)py) / (float)(nWS + 1);
                        if (this.vertical) {
                            dy += wordSpacing.floatValue() / (float)(nWS + 1);
                        } else {
                            dx += wordSpacing.floatValue() / (float)(nWS + 1);
                        }
                        for (int j = beginWS; j <= endWS; ++j) {
                            newPositions[j] = new Point2D.Float(x += dx, y += dy);
                        }
                    } else {
                        dx = (float)(gpos.getX() - prevPos.getX());
                        dy = (float)(gpos.getY() - prevPos.getY());
                        newPositions[i] = new Point2D.Float(x += dx, y += dy);
                    }
                    prevPos = gpos;
                }
                Point2D gPos = this.gv.getGlyphPosition(numGlyphs);
                newPositions[numGlyphs] = new Point2D.Float(x += (float)(gPos.getX() - prevPos.getX()), y += (float)(gPos.getY() - prevPos.getY()));
                for (int i2 = 1; i2 <= numGlyphs; ++i2) {
                    if (newPositions[i2] == null) continue;
                    this.gv.setGlyphPosition(i2, newPositions[i2]);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        double advX = this.gv.getGlyphPosition(numGlyphs - 1).getX() - this.gv.getGlyphPosition(0).getX();
        double advY = this.gv.getGlyphPosition(numGlyphs - 1).getY() - this.gv.getGlyphPosition(0).getY();
        Point2D.Double newAdvance = new Point2D.Double(advX + ((Point2D)lastCharAdvance).getX(), advY + ((Point2D)lastCharAdvance).getY());
        return newAdvance;
    }

    protected void applyStretchTransform(boolean stretchGlyphs) {
        if (this.xScale == 1.0f && this.yScale == 1.0f) {
            return;
        }
        AffineTransform scaleAT = AffineTransform.getScaleInstance(this.xScale, this.yScale);
        int numGlyphs = this.gv.getNumGlyphs();
        float[] gp = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
        float initX = gp[0];
        float initY = gp[1];
        Point2D.Float pos = new Point2D.Float();
        for (int i = 0; i <= numGlyphs; ++i) {
            float dx = gp[2 * i] - initX;
            float dy = gp[2 * i + 1] - initY;
            pos.x = initX + dx * this.xScale;
            pos.y = initY + dy * this.yScale;
            this.gv.setGlyphPosition(i, (Point2D)pos);
            if (!stretchGlyphs || i == numGlyphs) continue;
            AffineTransform glyphTransform = this.gv.getGlyphTransform(i);
            if (glyphTransform != null) {
                glyphTransform.preConcatenate(scaleAT);
                this.gv.setGlyphTransform(i, glyphTransform);
                continue;
            }
            this.gv.setGlyphTransform(i, scaleAT);
        }
        this.advance = new Point2D.Float((float)(this.advance.getX() * (double)this.xScale), (float)(this.advance.getY() * (double)this.yScale));
        this.layoutApplied = false;
    }

    protected void doPathLayout() {
        float currentPosition;
        if (this.pathApplied) {
            return;
        }
        if (!this.spacingApplied) {
            this.adjustTextSpacing();
        }
        this.getGlyphAdvances();
        if (this.textPath == null) {
            this.pathApplied = true;
            return;
        }
        boolean horizontal = !this.isVertical();
        boolean glyphOrientationAuto = this.isGlyphOrientationAuto();
        int glyphOrientationAngle = 0;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = this.getGlyphOrientationAngle();
        }
        float pathLength = this.textPath.lengthOfPath();
        float startOffset = this.textPath.getStartOffset();
        int numGlyphs = this.gv.getNumGlyphs();
        for (int i = 0; i < numGlyphs; ++i) {
            this.gv.setGlyphVisible(i, true);
        }
        float glyphsLength = horizontal ? (float)this.gv.getLogicalBounds().getWidth() : (float)this.gv.getLogicalBounds().getHeight();
        if (pathLength == 0.0f || glyphsLength == 0.0f) {
            this.pathApplied = true;
            this.textPathAdvance = this.advance;
            return;
        }
        Point2D firstGlyphPosition = this.gv.getGlyphPosition(0);
        float glyphOffset = 0.0f;
        if (horizontal) {
            glyphOffset = (float)firstGlyphPosition.getY();
            currentPosition = (float)(firstGlyphPosition.getX() + (double)startOffset);
        } else {
            glyphOffset = (float)firstGlyphPosition.getX();
            currentPosition = (float)(firstGlyphPosition.getY() + (double)startOffset);
        }
        char ch = this.aci.first();
        int start = this.aci.getBeginIndex();
        int currentChar = 0;
        int lastGlyphDrawn = -1;
        float lastGlyphAdvance = 0.0f;
        for (int i = 0; i < numGlyphs; ++i) {
            float charMidPos;
            Point2D charMidPoint;
            Point2D currentGlyphPos = this.gv.getGlyphPosition(i);
            float glyphAdvance = 0.0f;
            float nextGlyphOffset = 0.0f;
            Point2D nextGlyphPosition = this.gv.getGlyphPosition(i + 1);
            if (horizontal) {
                glyphAdvance = (float)(nextGlyphPosition.getX() - currentGlyphPos.getX());
                nextGlyphOffset = (float)(nextGlyphPosition.getY() - currentGlyphPos.getY());
            } else {
                glyphAdvance = (float)(nextGlyphPosition.getY() - currentGlyphPos.getY());
                nextGlyphOffset = (float)(nextGlyphPosition.getX() - currentGlyphPos.getX());
            }
            Rectangle2D glyphBounds = this.gv.getGlyphOutline(i).getBounds2D();
            float glyphWidth = (float)glyphBounds.getWidth();
            float glyphHeight = (float)glyphBounds.getHeight();
            float glyphMidX = 0.0f;
            if (glyphWidth > 0.0f) {
                glyphMidX = (float)(glyphBounds.getX() + (double)(glyphWidth / 2.0f));
                glyphMidX -= (float)currentGlyphPos.getX();
            }
            float glyphMidY = 0.0f;
            if (glyphHeight > 0.0f) {
                glyphMidY = (float)(glyphBounds.getY() + (double)(glyphHeight / 2.0f));
                glyphMidY -= (float)currentGlyphPos.getY();
            }
            if ((charMidPoint = this.textPath.pointAtLength(charMidPos = horizontal ? currentPosition + glyphMidX : currentPosition + glyphMidY)) != null) {
                float angle = this.textPath.angleAtLength(charMidPos);
                AffineTransform glyphPathTransform = new AffineTransform();
                if (horizontal) {
                    glyphPathTransform.rotate(angle);
                } else {
                    glyphPathTransform.rotate((double)angle - 1.5707963267948966);
                }
                if (horizontal) {
                    glyphPathTransform.translate(0.0, glyphOffset);
                } else {
                    glyphPathTransform.translate(glyphOffset, 0.0);
                }
                if (horizontal) {
                    glyphPathTransform.translate(-glyphMidX, 0.0);
                } else {
                    glyphPathTransform.translate(0.0, -glyphMidY);
                }
                AffineTransform glyphTransform = this.gv.getGlyphTransform(i);
                if (glyphTransform != null) {
                    glyphPathTransform.concatenate(glyphTransform);
                }
                this.gv.setGlyphTransform(i, glyphPathTransform);
                this.gv.setGlyphPosition(i, charMidPoint);
                lastGlyphDrawn = i;
                lastGlyphAdvance = glyphAdvance;
            } else {
                this.gv.setGlyphVisible(i, false);
            }
            currentPosition += glyphAdvance;
            glyphOffset += nextGlyphOffset;
            if ((currentChar += this.gv.getCharacterCount(i, i)) >= this.charMap.length) {
                currentChar = this.charMap.length - 1;
            }
            ch = this.aci.setIndex(currentChar + start);
        }
        if (lastGlyphDrawn > -1) {
            Point2D lastGlyphPos = this.gv.getGlyphPosition(lastGlyphDrawn);
            this.textPathAdvance = horizontal ? new Point2D.Double(lastGlyphPos.getX() + (double)lastGlyphAdvance, lastGlyphPos.getY()) : new Point2D.Double(lastGlyphPos.getX(), lastGlyphPos.getY() + (double)lastGlyphAdvance);
        } else {
            this.textPathAdvance = new Point2D.Double(0.0, 0.0);
        }
        this.layoutApplied = false;
        this.spacingApplied = false;
        this.pathApplied = true;
    }

    protected boolean isLatinChar(char c) {
        if (c < '\u00ff' && Character.isLetterOrDigit(c)) {
            return true;
        }
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.BASIC_LATIN || block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT || block == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL || block == Character.UnicodeBlock.LATIN_EXTENDED_A || block == Character.UnicodeBlock.LATIN_EXTENDED_B || block == Character.UnicodeBlock.ARABIC || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B;
    }

    protected boolean isGlyphOrientationAuto() {
        if (!this.isVertical()) {
            return false;
        }
        this.aci.first();
        Integer vOrient = (Integer)this.aci.getAttribute(VERTICAL_ORIENTATION);
        if (vOrient != null) {
            return vOrient == ORIENTATION_AUTO;
        }
        return true;
    }

    protected int getGlyphOrientationAngle() {
        int glyphOrientationAngle = 0;
        this.aci.first();
        Float angle = this.isVertical() ? (Float)this.aci.getAttribute(VERTICAL_ORIENTATION_ANGLE) : (Float)this.aci.getAttribute(HORIZONTAL_ORIENTATION_ANGLE);
        if (angle != null) {
            glyphOrientationAngle = (int)angle.floatValue();
        }
        if (glyphOrientationAngle != 0 || glyphOrientationAngle != 90 || glyphOrientationAngle != 180 || glyphOrientationAngle != 270) {
            while (glyphOrientationAngle < 0) {
                glyphOrientationAngle += 360;
            }
            while (glyphOrientationAngle >= 360) {
                glyphOrientationAngle -= 360;
            }
            glyphOrientationAngle = glyphOrientationAngle <= 45 || glyphOrientationAngle > 315 ? 0 : (glyphOrientationAngle > 45 && glyphOrientationAngle <= 135 ? 90 : (glyphOrientationAngle > 135 && glyphOrientationAngle <= 225 ? 180 : 270));
        }
        return glyphOrientationAngle;
    }

    @Override
    public boolean hasCharacterIndex(int index) {
        for (int aCharMap : this.charMap) {
            if (index != aCharMap) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isAltGlyph() {
        return this.isAltGlyph;
    }

    @Override
    public boolean isReversed() {
        return this.gv.isReversed();
    }

    @Override
    public void maybeReverse(boolean mirror) {
        this.gv.maybeReverse(mirror);
    }

    static {
        runAtts.add(X);
        runAtts.add(Y);
        runAtts.add(DX);
        runAtts.add(DY);
        runAtts.add(ROTATION);
        runAtts.add(BASELINE_SHIFT);
        szAtts = new HashSet();
        szAtts.add(TextAttribute.SIZE);
        szAtts.add(GVT_FONT);
        szAtts.add(LINE_HEIGHT);
    }
}

