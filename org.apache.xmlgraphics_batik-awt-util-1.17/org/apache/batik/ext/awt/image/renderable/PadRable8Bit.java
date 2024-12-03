/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

public class PadRable8Bit
extends AbstractRable
implements PadRable,
PaintRable {
    PadMode padMode;
    Rectangle2D padRect;

    public PadRable8Bit(Filter src, Rectangle2D padRect, PadMode padMode) {
        super.init(src, null);
        this.padRect = padRect;
        this.padMode = padMode;
    }

    @Override
    public Filter getSource() {
        return (Filter)this.srcs.get(0);
    }

    @Override
    public void setSource(Filter src) {
        super.init(src, null);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.padRect.clone();
    }

    @Override
    public void setPadRect(Rectangle2D rect) {
        this.touch();
        this.padRect = rect;
    }

    @Override
    public Rectangle2D getPadRect() {
        return (Rectangle2D)this.padRect.clone();
    }

    @Override
    public void setPadMode(PadMode padMode) {
        this.touch();
        this.padMode = padMode;
    }

    @Override
    public PadMode getPadMode() {
        return this.padMode;
    }

    @Override
    public boolean paintRable(Graphics2D g2d) {
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        if (this.getPadMode() != PadMode.ZERO_PAD) {
            return false;
        }
        Rectangle2D padBounds = this.getPadRect();
        Shape clip = g2d.getClip();
        g2d.clip(padBounds);
        GraphicsUtil.drawImage(g2d, this.getSource());
        g2d.setClip(clip);
        return true;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        Filter src = this.getSource();
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        AffineTransform usr2dev = rc.getTransform();
        Rectangle2D srect = src.getBounds2D();
        Rectangle2D rect = this.getBounds2D();
        Rectangle2D arect = aoi.getBounds2D();
        if (!arect.intersects(rect)) {
            return null;
        }
        Rectangle2D.intersect(arect, rect, arect);
        RenderedImage ri = null;
        if (arect.intersects(srect)) {
            srect = (Rectangle2D)srect.clone();
            Rectangle2D.intersect(srect, arect, srect);
            RenderContext srcRC = new RenderContext(usr2dev, srect, rh);
            ri = src.createRendering(srcRC);
        }
        if (ri == null) {
            ri = new BufferedImage(1, 1, 2);
        }
        CachableRed cr = GraphicsUtil.wrap(ri);
        arect = usr2dev.createTransformedShape(arect).getBounds2D();
        cr = new PadRed(cr, arect.getBounds(), this.padMode, rh);
        return cr;
    }

    @Override
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        Rectangle2D srect = this.getSource().getBounds2D();
        if (!srect.intersects(outputRgn)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(srect, outputRgn, srect);
        Rectangle2D bounds = this.getBounds2D();
        if (!srect.intersects(bounds)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(srect, bounds, srect);
        return srect;
    }

    @Override
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        Rectangle2D bounds;
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        if (!(inputRgn = (Rectangle2D)inputRgn.clone()).intersects(bounds = this.getBounds2D())) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(inputRgn, bounds, inputRgn);
        return inputRgn;
    }
}

