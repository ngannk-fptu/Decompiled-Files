/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.CompositeRule
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.AbstractRable
 *  org.apache.batik.ext.awt.image.renderable.AffineRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 */
package org.apache.batik.gvt.filter;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

public class BackgroundRable8Bit
extends AbstractRable {
    private GraphicsNode node;

    public GraphicsNode getGraphicsNode() {
        return this.node;
    }

    public void setGraphicsNode(GraphicsNode node) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
    }

    public BackgroundRable8Bit(GraphicsNode node) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
    }

    static Rectangle2D addBounds(CompositeGraphicsNode cgn, GraphicsNode child, Rectangle2D init) {
        GraphicsNode gn;
        List children = cgn.getChildren();
        Iterator i = children.iterator();
        Rectangle2D r2d = null;
        while (i.hasNext() && (gn = (GraphicsNode)i.next()) != child) {
            Rectangle2D cr2d = gn.getBounds();
            if (cr2d == null) continue;
            AffineTransform at = gn.getTransform();
            if (at != null) {
                cr2d = at.createTransformedShape(cr2d).getBounds2D();
            }
            if (r2d == null) {
                r2d = (Rectangle2D)cr2d.clone();
                continue;
            }
            r2d.add(cr2d);
        }
        if (r2d == null) {
            if (init == null) {
                return CompositeGraphicsNode.VIEWPORT;
            }
            return init;
        }
        if (init == null) {
            return r2d;
        }
        init.add(r2d);
        return init;
    }

    static Rectangle2D getViewportBounds(GraphicsNode gn, GraphicsNode child) {
        CompositeGraphicsNode cgn;
        Rectangle2D r2d = null;
        if (gn instanceof CompositeGraphicsNode) {
            cgn = (CompositeGraphicsNode)gn;
            r2d = cgn.getBackgroundEnable();
        }
        if (r2d == null) {
            r2d = BackgroundRable8Bit.getViewportBounds(gn.getParent(), gn);
        }
        if (r2d == null) {
            return null;
        }
        if (r2d == CompositeGraphicsNode.VIEWPORT) {
            if (child == null) {
                return (Rectangle2D)gn.getPrimitiveBounds().clone();
            }
            cgn = (CompositeGraphicsNode)gn;
            return BackgroundRable8Bit.addBounds(cgn, child, null);
        }
        AffineTransform at = gn.getTransform();
        if (at != null) {
            try {
                at = at.createInverse();
                r2d = at.createTransformedShape(r2d).getBounds2D();
            }
            catch (NoninvertibleTransformException nte) {
                r2d = null;
            }
        }
        if (child != null) {
            CompositeGraphicsNode cgn2 = (CompositeGraphicsNode)gn;
            r2d = BackgroundRable8Bit.addBounds(cgn2, child, r2d);
        } else {
            Rectangle2D gnb = gn.getPrimitiveBounds();
            if (gnb != null) {
                r2d.add(gnb);
            }
        }
        return r2d;
    }

    static Rectangle2D getBoundsRecursive(GraphicsNode gn, GraphicsNode child) {
        Rectangle2D r2d = null;
        if (gn == null) {
            return null;
        }
        if (gn instanceof CompositeGraphicsNode) {
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            r2d = cgn.getBackgroundEnable();
        }
        if (r2d != null) {
            return r2d;
        }
        r2d = BackgroundRable8Bit.getBoundsRecursive(gn.getParent(), gn);
        if (r2d == null) {
            return new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        }
        if (r2d == CompositeGraphicsNode.VIEWPORT) {
            return r2d;
        }
        AffineTransform at = gn.getTransform();
        if (at != null) {
            try {
                at = at.createInverse();
                r2d = at.createTransformedShape(r2d).getBounds2D();
            }
            catch (NoninvertibleTransformException nte) {
                r2d = null;
            }
        }
        return r2d;
    }

    public Rectangle2D getBounds2D() {
        Rectangle2D r2d = BackgroundRable8Bit.getBoundsRecursive(this.node, null);
        if (r2d == CompositeGraphicsNode.VIEWPORT) {
            r2d = BackgroundRable8Bit.getViewportBounds(this.node, null);
        }
        return r2d;
    }

    public Filter getBackground(GraphicsNode gn, GraphicsNode child, Rectangle2D aoi) {
        Filter f;
        AffineTransform at;
        if (gn == null) {
            throw new IllegalArgumentException("BackgroundImage requested yet no parent has 'enable-background:new'");
        }
        Rectangle2D r2d = null;
        if (gn instanceof CompositeGraphicsNode) {
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            r2d = cgn.getBackgroundEnable();
        }
        ArrayList<Filter> srcs = new ArrayList<Filter>();
        if (r2d == null) {
            Rectangle2D paoi = aoi;
            at = gn.getTransform();
            if (at != null) {
                paoi = at.createTransformedShape(aoi).getBounds2D();
            }
            if ((f = this.getBackground(gn.getParent(), gn, paoi)) != null && f.getBounds2D().intersects(aoi)) {
                srcs.add(f);
            }
        }
        if (child != null) {
            Object aChildren;
            GraphicsNode childGN;
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            List children = cgn.getChildren();
            f = children.iterator();
            while (f.hasNext() && (childGN = (GraphicsNode)(aChildren = f.next())) != child) {
                Rectangle2D cbounds = childGN.getBounds();
                if (cbounds == null) continue;
                AffineTransform at2 = childGN.getTransform();
                if (at2 != null) {
                    cbounds = at2.createTransformedShape(cbounds).getBounds2D();
                }
                if (!aoi.intersects(cbounds)) continue;
                srcs.add(childGN.getEnableBackgroundGraphicsNodeRable(true));
            }
        }
        if (srcs.size() == 0) {
            return null;
        }
        Object ret = null;
        ret = srcs.size() == 1 ? (Filter)srcs.get(0) : new CompositeRable8Bit(srcs, CompositeRule.OVER, false);
        if (child != null && (at = child.getTransform()) != null) {
            try {
                at = at.createInverse();
                ret = new AffineRable8Bit(ret, at);
            }
            catch (NoninvertibleTransformException nte) {
                ret = null;
            }
        }
        return ret;
    }

    public boolean isDynamic() {
        return false;
    }

    public RenderedImage createRendering(RenderContext renderContext) {
        Filter background;
        Rectangle2D r2d = this.getBounds2D();
        Shape aoi = renderContext.getAreaOfInterest();
        if (aoi != null) {
            Rectangle2D aoiR2d = aoi.getBounds2D();
            if (!r2d.intersects(aoiR2d)) {
                return null;
            }
            Rectangle2D.intersect(r2d, aoiR2d, r2d);
        }
        if ((background = this.getBackground(this.node, null, r2d)) == null) {
            return null;
        }
        background = new PadRable8Bit(background, r2d, PadMode.ZERO_PAD);
        RenderedImage ri = background.createRendering(new RenderContext(renderContext.getTransform(), r2d, renderContext.getRenderingHints()));
        return ri;
    }
}

