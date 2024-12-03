/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.GaussianBlurRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.GaussianBlurRed8Bit;
import org.apache.batik.ext.awt.image.rendered.PadRed;

public class GaussianBlurRable8Bit
extends AbstractColorInterpolationRable
implements GaussianBlurRable {
    private double stdDeviationX;
    private double stdDeviationY;
    static final double DSQRT2PI = Math.sqrt(Math.PI * 2) * 3.0 / 4.0;
    public static final double eps = 1.0E-4;

    public GaussianBlurRable8Bit(Filter src, double stdevX, double stdevY) {
        super(src, null);
        this.setStdDeviationX(stdevX);
        this.setStdDeviationY(stdevY);
    }

    @Override
    public void setStdDeviationX(double stdDeviationX) {
        if (stdDeviationX < 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.stdDeviationX = stdDeviationX;
    }

    @Override
    public void setStdDeviationY(double stdDeviationY) {
        if (stdDeviationY < 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.stdDeviationY = stdDeviationY;
    }

    @Override
    public double getStdDeviationX() {
        return this.stdDeviationX;
    }

    @Override
    public double getStdDeviationY() {
        return this.stdDeviationY;
    }

    @Override
    public void setSource(Filter src) {
        this.init(src, null);
    }

    @Override
    public Rectangle2D getBounds2D() {
        Rectangle2D src = this.getSource().getBounds2D();
        float dX = (float)(this.stdDeviationX * DSQRT2PI);
        float dY = (float)(this.stdDeviationY * DSQRT2PI);
        float radX = 3.0f * dX / 2.0f;
        float radY = 3.0f * dY / 2.0f;
        return new Rectangle2D.Float((float)(src.getMinX() - (double)radX), (float)(src.getMinY() - (double)radY), (float)(src.getWidth() + (double)(2.0f * radX)), (float)(src.getHeight() + (double)(2.0f * radY)));
    }

    @Override
    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    public static boolean eps_eq(double f1, double f2) {
        return f1 >= f2 - 1.0E-4 && f1 <= f2 + 1.0E-4;
    }

    public static boolean eps_abs_eq(double f1, double f2) {
        if (f1 < 0.0) {
            f1 = -f1;
        }
        if (f2 < 0.0) {
            f2 = -f2;
        }
        return GaussianBlurRable8Bit.eps_eq(f1, f2);
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Rectangle2D r;
        int outsetY;
        int outsetX;
        AffineTransform resAt;
        AffineTransform srcAt;
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
        double sdx = this.stdDeviationX * scaleX;
        double sdy = this.stdDeviationY * scaleY;
        if (sdx < 10.0 && sdy < 10.0 && GaussianBlurRable8Bit.eps_eq(sdx, sdy) && GaussianBlurRable8Bit.eps_abs_eq(sx / scaleX, sy / scaleY)) {
            srcAt = at;
            resAt = null;
            outsetX = 0;
            outsetY = 0;
        } else {
            if (sdx > 10.0) {
                scaleX = scaleX * 10.0 / sdx;
                sdx = 10.0;
            }
            if (sdy > 10.0) {
                scaleY = scaleY * 10.0 / sdy;
                sdy = 10.0;
            }
            srcAt = AffineTransform.getScaleInstance(scaleX, scaleY);
            resAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
            outsetX = 1;
            outsetY = 1;
        }
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        Shape devShape = srcAt.createTransformedShape(aoi);
        Rectangle devRect = devShape.getBounds();
        devRect.x -= (outsetX += GaussianBlurRed8Bit.surroundPixels(sdx, rh));
        devRect.y -= (outsetY += GaussianBlurRed8Bit.surroundPixels(sdy, rh));
        devRect.width += 2 * outsetX;
        devRect.height += 2 * outsetY;
        try {
            AffineTransform invSrcAt = srcAt.createInverse();
            r = invSrcAt.createTransformedShape(devRect).getBounds2D();
        }
        catch (NoninvertibleTransformException nte) {
            r = aoi.getBounds2D();
            r = new Rectangle2D.Double(r.getX() - (double)outsetX / scaleX, r.getY() - (double)outsetY / scaleY, r.getWidth() + (double)(2 * outsetX) / scaleX, r.getHeight() + (double)(2 * outsetY) / scaleY);
        }
        RenderedImage ri = this.getSource().createRendering(new RenderContext(srcAt, r, rh));
        if (ri == null) {
            return null;
        }
        CachableRed cr = this.convertSourceCS(ri);
        if (!devRect.equals(cr.getBounds())) {
            cr = new PadRed(cr, devRect, PadMode.ZERO_PAD, rh);
        }
        cr = new GaussianBlurRed8Bit(cr, sdx, sdy, rh);
        if (resAt != null && !resAt.isIdentity()) {
            cr = new AffineRed(cr, resAt, rh);
        }
        return cr;
    }

    @Override
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0) {
            outputRgn = null;
        } else {
            Rectangle2D bounds;
            float dX = (float)(this.stdDeviationX * DSQRT2PI);
            float dY = (float)(this.stdDeviationY * DSQRT2PI);
            float radX = 3.0f * dX / 2.0f;
            float radY = 3.0f * dY / 2.0f;
            if (!(outputRgn = new Rectangle2D.Float((float)(outputRgn.getMinX() - (double)radX), (float)(outputRgn.getMinY() - (double)radY), (float)(outputRgn.getWidth() + (double)(2.0f * radX)), (float)(outputRgn.getHeight() + (double)(2.0f * radY)))).intersects(bounds = this.getBounds2D())) {
                return new Rectangle2D.Float();
            }
            outputRgn = outputRgn.createIntersection(bounds);
        }
        return outputRgn;
    }

    @Override
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        Rectangle2D dirtyRegion = null;
        if (srcIndex == 0) {
            Rectangle2D bounds;
            float dX = (float)(this.stdDeviationX * DSQRT2PI);
            float dY = (float)(this.stdDeviationY * DSQRT2PI);
            float radX = 3.0f * dX / 2.0f;
            float radY = 3.0f * dY / 2.0f;
            if (!(inputRgn = new Rectangle2D.Float((float)(inputRgn.getMinX() - (double)radX), (float)(inputRgn.getMinY() - (double)radY), (float)(inputRgn.getWidth() + (double)(2.0f * radX)), (float)(inputRgn.getHeight() + (double)(2.0f * radY)))).intersects(bounds = this.getBounds2D())) {
                return new Rectangle2D.Float();
            }
            dirtyRegion = inputRgn.createIntersection(bounds);
        }
        return dirtyRegion;
    }
}

