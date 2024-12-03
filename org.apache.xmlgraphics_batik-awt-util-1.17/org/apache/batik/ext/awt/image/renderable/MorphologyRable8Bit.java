/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.MorphologyRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.MorphologyOp;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;

public class MorphologyRable8Bit
extends AbstractRable
implements MorphologyRable {
    private double radiusX;
    private double radiusY;
    private boolean doDilation;

    public MorphologyRable8Bit(Filter src, double radiusX, double radiusY, boolean doDilation) {
        super(src, null);
        this.setRadiusX(radiusX);
        this.setRadiusY(radiusY);
        this.setDoDilation(doDilation);
    }

    @Override
    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    @Override
    public void setSource(Filter src) {
        this.init(src, null);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds2D();
    }

    @Override
    public void setRadiusX(double radiusX) {
        if (radiusX <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.radiusX = radiusX;
    }

    @Override
    public void setRadiusY(double radiusY) {
        if (radiusY <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.radiusY = radiusY;
    }

    @Override
    public void setDoDilation(boolean doDilation) {
        this.touch();
        this.doDilation = doDilation;
    }

    @Override
    public boolean getDoDilation() {
        return this.doDilation;
    }

    @Override
    public double getRadiusX() {
        return this.radiusX;
    }

    @Override
    public double getRadiusY() {
        return this.radiusY;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
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
        double scaleX = Math.sqrt(sx * sx + shy * shy);
        double scaleY = Math.sqrt(sy * sy + shx * shx);
        AffineTransform srcAt = AffineTransform.getScaleInstance(scaleX, scaleY);
        int radX = (int)Math.round(this.radiusX * scaleX);
        int radY = (int)Math.round(this.radiusY * scaleY);
        MorphologyOp op = null;
        if (radX > 0 && radY > 0) {
            op = new MorphologyOp(radX, radY, this.doDilation);
        }
        AffineTransform resAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        Rectangle2D r = aoi.getBounds2D();
        r = new Rectangle2D.Double(r.getX() - (double)radX / scaleX, r.getY() - (double)radY / scaleY, r.getWidth() + (double)(2 * radX) / scaleX, r.getHeight() + (double)(2 * radY) / scaleY);
        RenderedImage ri = this.getSource().createRendering(new RenderContext(srcAt, r, rh));
        if (ri == null) {
            return null;
        }
        CachableRed cr = new RenderedImageCachableRed(ri);
        Shape devShape = srcAt.createTransformedShape(aoi.getBounds2D());
        r = devShape.getBounds2D();
        r = new Rectangle2D.Double(r.getX() - (double)radX, r.getY() - (double)radY, r.getWidth() + (double)(2 * radX), r.getHeight() + (double)(2 * radY));
        cr = new PadRed(cr, r.getBounds(), PadMode.ZERO_PAD, rh);
        ColorModel cm = ri.getColorModel();
        Raster rr = cr.getData();
        Point pt = new Point(0, 0);
        WritableRaster wr = Raster.createWritableRaster(rr.getSampleModel(), rr.getDataBuffer(), pt);
        BufferedImage srcBI = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
        BufferedImage destBI = op != null ? op.filter(srcBI, null) : srcBI;
        int rrMinX = cr.getMinX();
        int rrMinY = cr.getMinY();
        cr = new BufferedImageCachableRed(destBI, rrMinX, rrMinY);
        if (!resAt.isIdentity()) {
            cr = new AffineRed(cr, resAt, rh);
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

