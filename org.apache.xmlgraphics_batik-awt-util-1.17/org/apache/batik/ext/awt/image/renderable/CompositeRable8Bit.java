/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.CompositeRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.CompositeRed;
import org.apache.batik.ext.awt.image.rendered.FloodRed;

public class CompositeRable8Bit
extends AbstractColorInterpolationRable
implements CompositeRable,
PaintRable {
    protected CompositeRule rule;

    public CompositeRable8Bit(List srcs, CompositeRule rule, boolean csIsLinear) {
        super(srcs);
        this.setColorSpaceLinear(csIsLinear);
        this.rule = rule;
    }

    @Override
    public void setSources(List srcs) {
        this.init(srcs, null);
    }

    @Override
    public void setCompositeRule(CompositeRule cr) {
        this.touch();
        this.rule = cr;
    }

    @Override
    public CompositeRule getCompositeRule() {
        return this.rule;
    }

    @Override
    public boolean paintRable(Graphics2D g2d) {
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        if (this.getCompositeRule() != CompositeRule.OVER) {
            return false;
        }
        ColorSpace crCS = this.getOperationColorSpace();
        ColorSpace g2dCS = GraphicsUtil.getDestinationColorSpace(g2d);
        if (g2dCS == null || g2dCS != crCS) {
            return false;
        }
        for (Object o : this.getSources()) {
            GraphicsUtil.drawImage(g2d, (Filter)o);
        }
        return true;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Rectangle2D aoiR;
        if (this.srcs.size() == 0) {
            return null;
        }
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        AffineTransform at = rc.getTransform();
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoiR = this.getBounds2D();
        } else {
            aoiR = aoi.getBounds2D();
            Rectangle2D bounds2d = this.getBounds2D();
            if (!bounds2d.intersects(aoiR)) {
                return null;
            }
            Rectangle2D.intersect(aoiR, bounds2d, aoiR);
        }
        Rectangle devRect = at.createTransformedShape(aoiR).getBounds();
        rc = new RenderContext(at, aoiR, rh);
        ArrayList<CachableRed> srcs = new ArrayList<CachableRed>();
        for (Object o : this.getSources()) {
            Filter filt = (Filter)o;
            RenderedImage ri = filt.createRendering(rc);
            if (ri != null) {
                CachableRed cr = this.convertSourceCS(ri);
                srcs.add(cr);
                continue;
            }
            switch (this.rule.getRule()) {
                case 2: {
                    return null;
                }
                case 3: {
                    srcs.clear();
                    break;
                }
                case 6: {
                    srcs.add(new FloodRed(devRect));
                    break;
                }
            }
        }
        if (srcs.size() == 0) {
            return null;
        }
        CompositeRed cr = new CompositeRed(srcs, this.rule);
        return cr;
    }
}

