/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

class AffineOpImage
extends GeometricOpImage {
    protected static final int USHORT_MAX = 65535;
    protected AffineTransform f_transform;
    protected AffineTransform i_transform;
    protected Interpolation interp;
    private Rectangle srcimg;
    private Rectangle padimg;
    protected BorderExtender extender;
    private Rectangle theDest;
    private ImagingListener listener;
    protected static final int geom_frac_max = 0x100000;
    double m00;
    double m10;
    double flr_m00;
    double flr_m10;
    double fracdx;
    double fracdx1;
    double fracdy;
    double fracdy1;
    int incx;
    int incx1;
    int incy;
    int incy1;
    int ifracdx;
    int ifracdx1;
    int ifracdy;
    int ifracdy1;
    public int lpad;
    public int rpad;
    public int tpad;
    public int bpad;

    protected static int floorRatio(long num, long denom) {
        if (denom < 0L) {
            denom = -denom;
            num = -num;
        }
        if (num >= 0L) {
            return (int)(num / denom);
        }
        return (int)((num - denom + 1L) / denom);
    }

    protected static int ceilRatio(long num, long denom) {
        if (denom < 0L) {
            denom = -denom;
            num = -num;
        }
        if (num >= 0L) {
            return (int)((num + denom - 1L) / denom);
        }
        return (int)(num / denom);
    }

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, AffineTransform forward_tr) {
        ImageLayout newLayout = layout != null ? (ImageLayout)layout.clone() : new ImageLayout();
        float sx0 = source.getMinX();
        float sy0 = source.getMinY();
        float sw = source.getWidth();
        float sh = source.getHeight();
        Point2D[] pts = new Point2D[]{new Point2D.Float(sx0, sy0), new Point2D.Float(sx0 + sw, sy0), new Point2D.Float(sx0 + sw, sy0 + sh), new Point2D.Float(sx0, sy0 + sh)};
        forward_tr.transform(pts, 0, pts, 0, 4);
        float dx0 = Float.MAX_VALUE;
        float dy0 = Float.MAX_VALUE;
        float dx1 = -3.4028235E38f;
        float dy1 = -3.4028235E38f;
        for (int i = 0; i < 4; ++i) {
            float px = (float)pts[i].getX();
            float py = (float)pts[i].getY();
            dx0 = Math.min(dx0, px);
            dy0 = Math.min(dy0, py);
            dx1 = Math.max(dx1, px);
            dy1 = Math.max(dy1, py);
        }
        int lw = (int)(dx1 - dx0);
        int lh = (int)(dy1 - dy0);
        int i_dx0 = (int)Math.floor(dx0);
        int lx0 = (double)Math.abs(dx0 - (float)i_dx0) <= 0.5 ? i_dx0 : (int)Math.ceil(dx0);
        int i_dy0 = (int)Math.floor(dy0);
        int ly0 = (double)Math.abs(dy0 - (float)i_dy0) <= 0.5 ? i_dy0 : (int)Math.ceil(dy0);
        newLayout.setMinX(lx0);
        newLayout.setMinY(ly0);
        newLayout.setWidth(lw);
        newLayout.setHeight(lh);
        return newLayout;
    }

    public AffineOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues) {
        super(AffineOpImage.vectorize(source), AffineOpImage.layoutHelper(layout, source, transform), config, true, extender, interp, backgroundValues);
        this.listener = ImageUtil.getImagingListener((RenderingHints)config);
        this.interp = interp;
        this.extender = extender;
        this.lpad = interp.getLeftPadding();
        this.rpad = interp.getRightPadding();
        this.tpad = interp.getTopPadding();
        this.bpad = interp.getBottomPadding();
        this.srcimg = new Rectangle(this.getSourceImage(0).getMinX(), this.getSourceImage(0).getMinY(), this.getSourceImage(0).getWidth(), this.getSourceImage(0).getHeight());
        this.padimg = new Rectangle(this.srcimg.x - this.lpad, this.srcimg.y - this.tpad, this.srcimg.width + this.lpad + this.rpad, this.srcimg.height + this.tpad + this.bpad);
        if (extender == null) {
            float sx0 = this.srcimg.x;
            float sy0 = this.srcimg.y;
            float sw = this.srcimg.width;
            float sh = this.srcimg.height;
            float f_lpad = this.lpad;
            float f_rpad = this.rpad;
            float f_tpad = this.tpad;
            float f_bpad = this.bpad;
            if (!(interp instanceof InterpolationNearest)) {
                f_lpad = (float)((double)f_lpad + 0.5);
                f_tpad = (float)((double)f_tpad + 0.5);
                f_rpad = (float)((double)f_rpad + 0.5);
                f_bpad = (float)((double)f_bpad + 0.5);
            }
            Point2D[] pts = new Point2D[]{new Point2D.Float(sx0 += f_lpad, sy0 += f_tpad), new Point2D.Float(sx0 + (sw -= f_lpad + f_rpad), sy0), new Point2D.Float(sx0 + sw, sy0 + (sh -= f_tpad + f_bpad)), new Point2D.Float(sx0, sy0 + sh)};
            transform.transform(pts, 0, pts, 0, 4);
            float dx0 = Float.MAX_VALUE;
            float dy0 = Float.MAX_VALUE;
            float dx1 = -3.4028235E38f;
            float dy1 = -3.4028235E38f;
            for (int i = 0; i < 4; ++i) {
                float px = (float)pts[i].getX();
                float py = (float)pts[i].getY();
                dx0 = Math.min(dx0, px);
                dy0 = Math.min(dy0, py);
                dx1 = Math.max(dx1, px);
                dy1 = Math.max(dy1, py);
            }
            int lx0 = (int)Math.ceil(dx0);
            int ly0 = (int)Math.ceil(dy0);
            int lx1 = (int)Math.floor(dx1);
            int ly1 = (int)Math.floor(dy1);
            this.theDest = new Rectangle(lx0, ly0, lx1 - lx0, ly1 - ly0);
        } else {
            this.theDest = this.getBounds();
        }
        try {
            this.i_transform = transform.createInverse();
        }
        catch (Exception e) {
            String message = JaiI18N.getString("AffineOpImage0");
            this.listener.errorOccurred(message, new ImagingException(message, e), this, false);
        }
        this.f_transform = (AffineTransform)transform.clone();
        this.m00 = this.i_transform.getScaleX();
        this.flr_m00 = Math.floor(this.m00);
        this.fracdx = this.m00 - this.flr_m00;
        this.fracdx1 = 1.0 - this.fracdx;
        this.incx = (int)this.flr_m00;
        this.incx1 = this.incx + 1;
        this.ifracdx = (int)Math.round(this.fracdx * 1048576.0);
        this.ifracdx1 = 0x100000 - this.ifracdx;
        this.m10 = this.i_transform.getShearY();
        this.flr_m10 = Math.floor(this.m10);
        this.fracdy = this.m10 - this.flr_m10;
        this.fracdy1 = 1.0 - this.fracdy;
        this.incy = (int)this.flr_m10;
        this.incy1 = this.incy + 1;
        this.ifracdy = (int)Math.round(this.fracdy * 1048576.0);
        this.ifracdy1 = 0x100000 - this.ifracdy;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D dpt = (Point2D)destPt.clone();
        dpt.setLocation(dpt.getX() + 0.5, dpt.getY() + 0.5);
        Point2D spt = this.i_transform.transform(dpt, null);
        spt.setLocation(spt.getX() - 0.5, spt.getY() - 0.5);
        return spt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Point2D spt = (Point2D)sourcePt.clone();
        spt.setLocation(spt.getX() + 0.5, spt.getY() + 0.5);
        Point2D dpt = this.f_transform.transform(spt, null);
        dpt.setLocation(dpt.getX() - 0.5, dpt.getY() - 0.5);
        return dpt;
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        return this.f_transform.createTransformedShape(sourceRect).getBounds();
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        float dx0 = destRect.x;
        float dy0 = destRect.y;
        float dw = destRect.width;
        float dh = destRect.height;
        Point2D[] pts = new Point2D[]{new Point2D.Float(dx0, dy0), new Point2D.Float(dx0 + dw, dy0), new Point2D.Float(dx0 + dw, dy0 + dh), new Point2D.Float(dx0, dy0 + dh)};
        this.i_transform.transform(pts, 0, pts, 0, 4);
        float f_sx0 = Float.MAX_VALUE;
        float f_sy0 = Float.MAX_VALUE;
        float f_sx1 = -3.4028235E38f;
        float f_sy1 = -3.4028235E38f;
        for (int i = 0; i < 4; ++i) {
            float px = (float)pts[i].getX();
            float py = (float)pts[i].getY();
            f_sx0 = Math.min(f_sx0, px);
            f_sy0 = Math.min(f_sy0, py);
            f_sx1 = Math.max(f_sx1, px);
            f_sy1 = Math.max(f_sy1, py);
        }
        int s_x0 = 0;
        int s_y0 = 0;
        int s_x1 = 0;
        int s_y1 = 0;
        if (this.interp instanceof InterpolationNearest) {
            s_x0 = (int)Math.floor(f_sx0);
            s_y0 = (int)Math.floor(f_sy0);
            s_x1 = (int)Math.ceil((double)f_sx1 + 0.5);
            s_y1 = (int)Math.ceil((double)f_sy1 + 0.5);
        } else {
            s_x0 = (int)Math.floor((double)f_sx0 - 0.5);
            s_y0 = (int)Math.floor((double)f_sy0 - 0.5);
            s_x1 = (int)Math.ceil(f_sx1);
            s_y1 = (int)Math.ceil(f_sy1);
        }
        return new Rectangle(s_x0, s_y0, s_x1 - s_x0, s_y1 - s_y0);
    }

    public void mapDestPoint(Point2D destPoint, Point2D srcPoint) {
        this.i_transform.transform(destPoint, srcPoint);
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle rect = new Rectangle(org.x, org.y, this.tileWidth, this.tileHeight);
        Rectangle destRect = rect.intersection(this.theDest);
        Rectangle destRect1 = rect.intersection(this.getBounds());
        if (destRect.width <= 0 || destRect.height <= 0) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect1, this.backgroundValues);
            }
            return dest;
        }
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        srcRect = this.extender == null ? srcRect.intersection(this.srcimg) : srcRect.intersection(this.padimg);
        if (srcRect.width <= 0 || srcRect.height <= 0) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect1, this.backgroundValues);
            }
            return dest;
        }
        if (!destRect1.equals(destRect)) {
            ImageUtil.fillBordersWithBackgroundValues(destRect1, destRect, dest, this.backgroundValues);
        }
        Raster[] sources = new Raster[]{this.extender == null ? this.getSourceImage(0).getData(srcRect) : this.getSourceImage(0).getExtendedData(srcRect, this.extender)};
        this.computeRect(sources, dest, destRect);
        if (this.getSourceImage(0).overlapsMultipleTiles(srcRect)) {
            this.recycleTile(sources[0]);
        }
        return dest;
    }
}

