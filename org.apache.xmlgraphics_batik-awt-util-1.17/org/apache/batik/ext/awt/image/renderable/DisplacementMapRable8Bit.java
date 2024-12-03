/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.List;
import org.apache.batik.ext.awt.image.ARGBChannel;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.DisplacementMapRed;

public class DisplacementMapRable8Bit
extends AbstractColorInterpolationRable
implements DisplacementMapRable {
    private double scale;
    private ARGBChannel xChannelSelector;
    private ARGBChannel yChannelSelector;

    public DisplacementMapRable8Bit(List sources, double scale, ARGBChannel xChannelSelector, ARGBChannel yChannelSelector) {
        this.setSources(sources);
        this.setScale(scale);
        this.setXChannelSelector(xChannelSelector);
        this.setYChannelSelector(yChannelSelector);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return ((Filter)this.getSources().get(0)).getBounds2D();
    }

    @Override
    public void setScale(double scale) {
        this.touch();
        this.scale = scale;
    }

    @Override
    public double getScale() {
        return this.scale;
    }

    @Override
    public void setSources(List sources) {
        if (sources.size() != 2) {
            throw new IllegalArgumentException();
        }
        this.init(sources, null);
    }

    @Override
    public void setXChannelSelector(ARGBChannel xChannelSelector) {
        if (xChannelSelector == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.xChannelSelector = xChannelSelector;
    }

    @Override
    public ARGBChannel getXChannelSelector() {
        return this.xChannelSelector;
    }

    @Override
    public void setYChannelSelector(ARGBChannel yChannelSelector) {
        if (yChannelSelector == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.yChannelSelector = yChannelSelector;
    }

    @Override
    public ARGBChannel getYChannelSelector() {
        return this.yChannelSelector;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Rectangle2D displacedRect;
        Rectangle2D aoiR;
        RenderContext srcRc;
        RenderedImage mapRed;
        Filter displaced = (Filter)this.getSources().get(0);
        Filter map = (Filter)this.getSources().get(1);
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        AffineTransform at = rc.getTransform();
        double sx = at.getScaleX();
        double sy = at.getScaleY();
        double shx = at.getShearX();
        double shy = at.getShearY();
        double tx = at.getTranslateX();
        double ty = at.getTranslateY();
        double atScaleX = Math.sqrt(sx * sx + shy * shy);
        double atScaleY = Math.sqrt(sy * sy + shx * shx);
        float scaleX = (float)(this.scale * atScaleX);
        float scaleY = (float)(this.scale * atScaleY);
        if (scaleX == 0.0f && scaleY == 0.0f) {
            return displaced.createRendering(rc);
        }
        AffineTransform srcAt = AffineTransform.getScaleInstance(atScaleX, atScaleY);
        Shape origAOI = rc.getAreaOfInterest();
        if (origAOI == null) {
            origAOI = this.getBounds2D();
        }
        if ((mapRed = map.createRendering(srcRc = new RenderContext(srcAt, aoiR = origAOI.getBounds2D(), rh))) == null) {
            return null;
        }
        if (!(aoiR = new Rectangle2D.Double(aoiR.getX() - this.scale / 2.0, aoiR.getY() - this.scale / 2.0, aoiR.getWidth() + this.scale, aoiR.getHeight() + this.scale)).intersects(displacedRect = displaced.getBounds2D())) {
            return null;
        }
        srcRc = new RenderContext(srcAt, aoiR = aoiR.createIntersection(displacedRect), rh);
        RenderedImage displacedRed = displaced.createRendering(srcRc);
        if (displacedRed == null) {
            return null;
        }
        mapRed = this.convertSourceCS(mapRed);
        AbstractRed cr = new DisplacementMapRed(GraphicsUtil.wrap(displacedRed), GraphicsUtil.wrap(mapRed), this.xChannelSelector, this.yChannelSelector, scaleX, scaleY, rh);
        AffineTransform resAt = new AffineTransform(sx / atScaleX, shy / atScaleX, shx / atScaleY, sy / atScaleY, tx, ty);
        if (!resAt.isIdentity()) {
            cr = new AffineRed((CachableRed)cr, resAt, rh);
        }
        return cr;
    }

    @Override
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        return super.getDependencyRegion(srcIndex, outputRgn);
    }

    @Override
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        return super.getDirtyRegion(srcIndex, inputRgn);
    }
}

