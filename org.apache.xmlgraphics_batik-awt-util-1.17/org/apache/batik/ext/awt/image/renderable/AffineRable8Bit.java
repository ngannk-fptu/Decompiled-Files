/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.AffineRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PaintRable;

public class AffineRable8Bit
extends AbstractRable
implements AffineRable,
PaintRable {
    AffineTransform affine;
    AffineTransform invAffine;

    public AffineRable8Bit(Filter src, AffineTransform affine) {
        this.init(src);
        this.setAffine(affine);
    }

    @Override
    public Rectangle2D getBounds2D() {
        Filter src = this.getSource();
        Rectangle2D r = src.getBounds2D();
        return this.affine.createTransformedShape(r).getBounds2D();
    }

    @Override
    public Filter getSource() {
        return (Filter)this.srcs.get(0);
    }

    @Override
    public void setSource(Filter src) {
        this.init(src);
    }

    @Override
    public void setAffine(AffineTransform affine) {
        this.touch();
        this.affine = affine;
        try {
            this.invAffine = affine.createInverse();
        }
        catch (NoninvertibleTransformException e) {
            this.invAffine = null;
        }
    }

    @Override
    public AffineTransform getAffine() {
        return (AffineTransform)this.affine.clone();
    }

    @Override
    public boolean paintRable(Graphics2D g2d) {
        AffineTransform at = g2d.getTransform();
        g2d.transform(this.getAffine());
        GraphicsUtil.drawImage(g2d, this.getSource());
        g2d.setTransform(at);
        return true;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Shape aoi;
        if (this.invAffine == null) {
            return null;
        }
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        if ((aoi = rc.getAreaOfInterest()) != null) {
            aoi = this.invAffine.createTransformedShape(aoi);
        }
        AffineTransform at = rc.getTransform();
        at.concatenate(this.affine);
        return this.getSource().createRendering(new RenderContext(at, aoi, rh));
    }

    @Override
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        if (this.invAffine == null) {
            return null;
        }
        return this.invAffine.createTransformedShape(outputRgn);
    }

    @Override
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        return this.affine.createTransformedShape(inputRgn);
    }
}

