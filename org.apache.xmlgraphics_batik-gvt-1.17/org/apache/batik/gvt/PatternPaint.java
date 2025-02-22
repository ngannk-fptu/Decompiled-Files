/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 */
package org.apache.batik.gvt;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.PatternPaintContext;

public class PatternPaint
implements Paint {
    private GraphicsNode node;
    private Rectangle2D patternRegion;
    private AffineTransform patternTransform;
    private Filter tile;
    private boolean overflow;
    private PatternPaintContext lastContext;

    public PatternPaint(GraphicsNode node, Rectangle2D patternRegion, boolean overflow, AffineTransform patternTransform) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        if (patternRegion == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
        this.patternRegion = patternRegion;
        this.overflow = overflow;
        this.patternTransform = patternTransform;
        CompositeGraphicsNode comp = new CompositeGraphicsNode();
        comp.getChildren().add(node);
        Filter gnr = comp.getGraphicsNodeRable(true);
        Rectangle2D padBounds = (Rectangle2D)patternRegion.clone();
        if (overflow) {
            Rectangle2D nodeBounds = comp.getBounds();
            padBounds.add(nodeBounds);
        }
        this.tile = new PadRable8Bit(gnr, padBounds, PadMode.ZERO_PAD);
    }

    public GraphicsNode getGraphicsNode() {
        return this.node;
    }

    public Rectangle2D getPatternRect() {
        return (Rectangle2D)this.patternRegion.clone();
    }

    public AffineTransform getPatternTransform() {
        return this.patternTransform;
    }

    public boolean getOverflow() {
        return this.overflow;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        if (this.patternTransform != null) {
            xform = new AffineTransform(xform);
            xform.concatenate(this.patternTransform);
        }
        if (this.lastContext != null && this.lastContext.getColorModel().equals(cm)) {
            double[] p = new double[6];
            double[] q = new double[6];
            xform.getMatrix(p);
            this.lastContext.getUsr2Dev().getMatrix(q);
            if (p[0] == q[0] && p[1] == q[1] && p[2] == q[2] && p[3] == q[3]) {
                if (p[4] == q[4] && p[5] == q[5]) {
                    return this.lastContext;
                }
                return new PatternPaintContextWrapper(this.lastContext, (int)(q[4] - p[4] + 0.5), (int)(q[5] - p[5] + 0.5));
            }
        }
        this.lastContext = new PatternPaintContext(cm, xform, hints, this.tile, this.patternRegion, this.overflow);
        return this.lastContext;
    }

    @Override
    public int getTransparency() {
        return 3;
    }

    static class PatternPaintContextWrapper
    implements PaintContext {
        PatternPaintContext ppc;
        int xShift;
        int yShift;

        PatternPaintContextWrapper(PatternPaintContext ppc, int xShift, int yShift) {
            this.ppc = ppc;
            this.xShift = xShift;
            this.yShift = yShift;
        }

        @Override
        public void dispose() {
        }

        @Override
        public ColorModel getColorModel() {
            return this.ppc.getColorModel();
        }

        @Override
        public Raster getRaster(int x, int y, int width, int height) {
            return this.ppc.getRaster(x + this.xShift, y + this.yShift, width, height);
        }
    }
}

