/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;

public class RedRable
extends AbstractRable {
    CachableRed src;

    public RedRable(CachableRed src) {
        super((Filter)null);
        this.src = src;
    }

    public CachableRed getSource() {
        return this.src;
    }

    @Override
    public Object getProperty(String name) {
        return this.src.getProperty(name);
    }

    @Override
    public String[] getPropertyNames() {
        return this.src.getPropertyNames();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds();
    }

    @Override
    public RenderedImage createDefaultRendering() {
        return this.getSource();
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Shape aoi;
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        Rectangle aoiR = (aoi = rc.getAreaOfInterest()) != null ? aoi.getBounds() : this.getBounds2D().getBounds();
        AffineTransform at = rc.getTransform();
        CachableRed cr = this.getSource();
        if (!aoiR.intersects(cr.getBounds())) {
            return null;
        }
        if (at.isIdentity()) {
            return cr;
        }
        if (at.getScaleX() == 1.0 && at.getScaleY() == 1.0 && at.getShearX() == 0.0 && at.getShearY() == 0.0) {
            int xloc = (int)((double)cr.getMinX() + at.getTranslateX());
            int yloc = (int)((double)cr.getMinY() + at.getTranslateY());
            double dx = (double)xloc - ((double)cr.getMinX() + at.getTranslateX());
            double dy = (double)yloc - ((double)cr.getMinY() + at.getTranslateY());
            if (dx > -1.0E-4 && dx < 1.0E-4 && dy > -1.0E-4 && dy < 1.0E-4) {
                return new TranslateRed(cr, xloc, yloc);
            }
        }
        return new AffineRed(cr, at, rh);
    }
}

