/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.Rational;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.IntegerSequence;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public abstract class ScaleOpImage
extends GeometricOpImage {
    protected float scaleX;
    protected float scaleY;
    protected float transX;
    protected float transY;
    protected Rational scaleXRational;
    protected Rational scaleYRational;
    protected long scaleXRationalNum;
    protected long scaleXRationalDenom;
    protected long scaleYRationalNum;
    protected long scaleYRationalDenom;
    protected Rational invScaleXRational;
    protected Rational invScaleYRational;
    protected long invScaleXRationalNum;
    protected long invScaleXRationalDenom;
    protected long invScaleYRationalNum;
    protected long invScaleYRationalDenom;
    protected Rational transXRational;
    protected Rational transYRational;
    protected long transXRationalNum;
    protected long transXRationalDenom;
    protected long transYRationalNum;
    protected long transYRationalDenom;
    protected static float rationalTolerance = 1.0E-6f;
    private int lpad;
    private int rpad;
    private int tpad;
    private int bpad;

    private static ImageLayout layoutHelper(RenderedImage source, float scaleX, float scaleY, float transX, float transY, Interpolation interp, ImageLayout il) {
        Rational scaleXRational = Rational.approximate(scaleX, rationalTolerance);
        Rational scaleYRational = Rational.approximate(scaleY, rationalTolerance);
        long scaleXRationalNum = scaleXRational.num;
        long scaleXRationalDenom = scaleXRational.denom;
        long scaleYRationalNum = scaleYRational.num;
        long scaleYRationalDenom = scaleYRational.denom;
        Rational transXRational = Rational.approximate(transX, rationalTolerance);
        Rational transYRational = Rational.approximate(transY, rationalTolerance);
        long transXRationalNum = transXRational.num;
        long transXRationalDenom = transXRational.denom;
        long transYRationalNum = transYRational.num;
        long transYRationalDenom = transYRational.denom;
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        int x0 = source.getMinX();
        int y0 = source.getMinY();
        int w = source.getWidth();
        int h = source.getHeight();
        long dx0Num = x0;
        long dx0Denom = 1L;
        long dy0Num = y0;
        long dy0Denom = 1L;
        long dx1Num = x0 + w;
        long dx1Denom = 1L;
        long dy1Num = y0 + h;
        long dy1Denom = 1L;
        dx0Num *= scaleXRationalNum;
        dx0Denom *= scaleXRationalDenom;
        dy0Num *= scaleYRationalNum;
        dy0Denom *= scaleYRationalDenom;
        dx1Num *= scaleXRationalNum;
        dx1Denom *= scaleXRationalDenom;
        dy1Num *= scaleYRationalNum;
        dy1Denom *= scaleYRationalDenom;
        dx0Num = 2L * dx0Num - dx0Denom;
        dx0Denom *= 2L;
        dy0Num = 2L * dy0Num - dy0Denom;
        dy0Denom *= 2L;
        dx1Num = 2L * dx1Num - 3L * dx1Denom;
        dy1Num = 2L * dy1Num - 3L * dy1Denom;
        dx0Num = dx0Num * transXRationalDenom + transXRationalNum * dx0Denom;
        dy0Num = dy0Num * transYRationalDenom + transYRationalNum * dy0Denom;
        dx1Num = dx1Num * transXRationalDenom + transXRationalNum * (dx1Denom *= 2L);
        dy1Num = dy1Num * transYRationalDenom + transYRationalNum * (dy1Denom *= 2L);
        int l_x0 = Rational.ceil(dx0Num, dx0Denom *= transXRationalDenom);
        int l_y0 = Rational.ceil(dy0Num, dy0Denom *= transYRationalDenom);
        int l_x1 = Rational.ceil(dx1Num, dx1Denom *= transXRationalDenom);
        int l_y1 = Rational.ceil(dy1Num, dy1Denom *= transYRationalDenom);
        layout.setMinX(l_x0);
        layout.setMinY(l_y0);
        layout.setWidth(l_x1 - l_x0 + 1);
        layout.setHeight(l_y1 - l_y0 + 1);
        return layout;
    }

    private static Map configHelper(RenderedImage source, Map configuration, Interpolation interp) {
        Map config = configuration;
        if (ImageUtil.isBinary(source.getSampleModel()) && (interp == null || interp instanceof InterpolationNearest || interp instanceof InterpolationBilinear)) {
            if (configuration == null) {
                config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
            } else if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                RenderingHints hints = new RenderingHints(null);
                hints.putAll((Map<?, ?>)configuration);
                config = hints;
                config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
            }
        }
        return config;
    }

    public ScaleOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, float scaleX, float scaleY, float transX, float transY) {
        super(ScaleOpImage.vectorize(source), ScaleOpImage.layoutHelper(source, scaleX, scaleY, transX, transY, interp, layout), ScaleOpImage.configHelper(source, configuration, interp), cobbleSources, extender, interp, null);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.transX = transX;
        this.transY = transY;
        this.scaleXRational = Rational.approximate(scaleX, rationalTolerance);
        this.scaleYRational = Rational.approximate(scaleY, rationalTolerance);
        this.scaleXRationalNum = this.scaleXRational.num;
        this.scaleXRationalDenom = this.scaleXRational.denom;
        this.scaleYRationalNum = this.scaleYRational.num;
        this.scaleYRationalDenom = this.scaleYRational.denom;
        this.transXRational = Rational.approximate(transX, rationalTolerance);
        this.transYRational = Rational.approximate(transY, rationalTolerance);
        this.transXRationalNum = this.transXRational.num;
        this.transXRationalDenom = this.transXRational.denom;
        this.transYRationalNum = this.transYRational.num;
        this.transYRationalDenom = this.transYRational.denom;
        this.invScaleXRational = new Rational(this.scaleXRational);
        this.invScaleXRational.invert();
        this.invScaleYRational = new Rational(this.scaleYRational);
        this.invScaleYRational.invert();
        this.invScaleXRationalNum = this.invScaleXRational.num;
        this.invScaleXRationalDenom = this.invScaleXRational.denom;
        this.invScaleYRationalNum = this.invScaleYRational.num;
        this.invScaleYRationalDenom = this.invScaleYRational.denom;
        this.lpad = interp.getLeftPadding();
        this.rpad = interp.getRightPadding();
        this.tpad = interp.getTopPadding();
        this.bpad = interp.getBottomPadding();
        if (extender == null) {
            int l_y1;
            long dy1Denom;
            long dy1Num;
            long dx1Denom;
            long dx1Num;
            long dy0Denom;
            long dy0Num;
            long dx0Denom;
            long dx0Num;
            int x0 = source.getMinX();
            int y0 = source.getMinY();
            int w = source.getWidth();
            int h = source.getHeight();
            if (interp instanceof InterpolationNearest) {
                dx0Num = x0;
                dx0Denom = 1L;
                dy0Num = y0;
                dy0Denom = 1L;
                dx1Num = x0 + w;
                dx1Denom = 1L;
                dy1Num = y0 + h;
                dy1Denom = 1L;
            } else {
                dx0Num = 2 * x0 + 1;
                dx0Denom = 2L;
                dy0Num = 2 * y0 + 1;
                dy0Denom = 2L;
                dx1Num = 2 * x0 + 2 * w + 1;
                dx1Denom = 2L;
                dy1Num = 2 * y0 + 2 * h + 1;
                dy1Denom = 2L;
                dx0Num += dx0Denom * (long)this.lpad;
                dy0Num += dy0Denom * (long)this.tpad;
                dx1Num -= dx1Denom * (long)this.rpad;
                dy1Num -= dy1Denom * (long)this.bpad;
            }
            dx0Num *= this.scaleXRationalNum;
            dx0Num = dx0Num * this.transXRationalDenom + this.transXRationalNum * (dx0Denom *= this.scaleXRationalDenom);
            dx0Denom *= this.transXRationalDenom;
            dy0Num *= this.scaleYRationalNum;
            dy0Num = dy0Num * this.transYRationalDenom + this.transYRationalNum * (dy0Denom *= this.scaleYRationalDenom);
            dy0Denom *= this.transYRationalDenom;
            dx1Num *= this.scaleXRationalNum;
            dx1Num = dx1Num * this.transXRationalDenom + this.transXRationalNum * (dx1Denom *= this.scaleXRationalDenom);
            dx1Denom *= this.transXRationalDenom;
            dy1Num *= this.scaleYRationalNum;
            dy1Num = dy1Num * this.transYRationalDenom + this.transYRationalNum * (dy1Denom *= this.scaleYRationalDenom);
            dy1Denom *= this.transYRationalDenom;
            dx0Num = 2L * dx0Num - dx0Denom;
            dy0Num = 2L * dy0Num - dy0Denom;
            int l_x0 = Rational.ceil(dx0Num, dx0Denom *= 2L);
            int l_y0 = Rational.ceil(dy0Num, dy0Denom *= 2L);
            dx1Num = 2L * dx1Num - dx1Denom;
            dy1Num = 2L * dy1Num - dy1Denom;
            dy1Denom *= 2L;
            int l_x1 = Rational.floor(dx1Num, dx1Denom *= 2L);
            if ((long)l_x1 * dx1Denom == dx1Num) {
                --l_x1;
            }
            if ((long)(l_y1 = Rational.floor(dy1Num, dy1Denom)) * dy1Denom == dy1Num) {
                --l_y1;
            }
            this.computableBounds = new Rectangle(l_x0, l_y0, l_x1 - l_x0 + 1, l_y1 - l_y0 + 1);
        } else {
            this.computableBounds = this.getBounds();
        }
    }

    public int getLeftPadding() {
        return this.interp == null ? 0 : this.interp.getLeftPadding();
    }

    public int getRightPadding() {
        return this.interp == null ? 0 : this.interp.getRightPadding();
    }

    public int getTopPadding() {
        return this.interp == null ? 0 : this.interp.getTopPadding();
    }

    public int getBottomPadding() {
        return this.interp == null ? 0 : this.interp.getBottomPadding();
    }

    public Point2D mapDestPoint(Point2D destPt, int sourceIndex) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation((destPt.getX() - (double)this.transX + 0.5) / (double)this.scaleX - 0.5, (destPt.getY() - (double)this.transY + 0.5) / (double)this.scaleY - 0.5);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation((double)this.scaleX * (sourcePt.getX() + 0.5) + (double)this.transX - 0.5, (double)this.scaleY * (sourcePt.getY() + 0.5) + (double)this.transY - 0.5);
        return pt;
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        int l_y1;
        long dy1Denom;
        long dy1Num;
        long dx1Denom;
        long dx1Num;
        long dy0Denom;
        long dy0Num;
        long dx0Denom;
        long dx0Num;
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int x0 = sourceRect.x;
        int y0 = sourceRect.y;
        int w = sourceRect.width;
        int h = sourceRect.height;
        if (this.interp instanceof InterpolationNearest) {
            dx0Num = x0;
            dx0Denom = 1L;
            dy0Num = y0;
            dy0Denom = 1L;
            dx1Num = x0 + w;
            dx1Denom = 1L;
            dy1Num = y0 + h;
            dy1Denom = 1L;
        } else {
            dx0Num = 2 * x0 + 1;
            dx0Denom = 2L;
            dy0Num = 2 * y0 + 1;
            dy0Denom = 2L;
            dx1Num = 2 * x0 + 2 * w + 1;
            dx1Denom = 2L;
            dy1Num = 2 * y0 + 2 * h + 1;
            dy1Denom = 2L;
        }
        dx0Num *= this.scaleXRationalNum;
        dx0Denom *= this.scaleXRationalDenom;
        dy0Num *= this.scaleYRationalNum;
        dy0Denom *= this.scaleYRationalDenom;
        dx1Num *= this.scaleXRationalNum;
        dx1Denom *= this.scaleXRationalDenom;
        dy1Num *= this.scaleYRationalNum;
        dy1Denom *= this.scaleYRationalDenom;
        dx0Num = dx0Num * this.transXRationalDenom + this.transXRationalNum * dx0Denom;
        dx0Denom *= this.transXRationalDenom;
        dy0Num = dy0Num * this.transYRationalDenom + this.transYRationalNum * dy0Denom;
        dy0Denom *= this.transYRationalDenom;
        dx1Num = dx1Num * this.transXRationalDenom + this.transXRationalNum * dx1Denom;
        dx1Denom *= this.transXRationalDenom;
        dy1Num = dy1Num * this.transYRationalDenom + this.transYRationalNum * dy1Denom;
        dy1Denom *= this.transYRationalDenom;
        dx0Num = 2L * dx0Num - dx0Denom;
        dy0Num = 2L * dy0Num - dy0Denom;
        int l_x0 = Rational.ceil(dx0Num, dx0Denom *= 2L);
        int l_y0 = Rational.ceil(dy0Num, dy0Denom *= 2L);
        dx1Num = 2L * dx1Num - dx1Denom;
        dy1Num = 2L * dy1Num - dy1Denom;
        dy1Denom *= 2L;
        int l_x1 = Rational.floor(dx1Num, dx1Denom *= 2L);
        if ((long)l_x1 * dx1Denom == dx1Num) {
            --l_x1;
        }
        if ((long)(l_y1 = Rational.floor(dy1Num, dy1Denom)) * dy1Denom == dy1Num) {
            --l_y1;
        }
        return new Rectangle(l_x0, l_y0, l_x1 - l_x0 + 1, l_y1 - l_y0 + 1);
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int x0 = destRect.x;
        int y0 = destRect.y;
        int w = destRect.width;
        int h = destRect.height;
        long sx0Num = x0 * 2 + 1;
        long sx0Denom = 2L;
        long sy0Num = y0 * 2 + 1;
        long sy0Denom = 2L;
        long sx1Num = 2 * x0 + 2 * w - 1;
        long sx1Denom = 2L;
        long sy1Num = 2 * y0 + 2 * h - 1;
        long sy1Denom = 2L;
        sx0Num = sx0Num * this.transXRationalDenom - this.transXRationalNum * sx0Denom;
        sx0Denom *= this.transXRationalDenom;
        sy0Num = sy0Num * this.transYRationalDenom - this.transYRationalNum * sy0Denom;
        sy0Denom *= this.transYRationalDenom;
        sx1Num = sx1Num * this.transXRationalDenom - this.transXRationalNum * sx1Denom;
        sx1Denom *= this.transXRationalDenom;
        sy1Num = sy1Num * this.transYRationalDenom - this.transYRationalNum * sy1Denom;
        sy1Denom *= this.transYRationalDenom;
        sx0Num *= this.invScaleXRationalNum;
        sx0Denom *= this.invScaleXRationalDenom;
        sy0Num *= this.invScaleYRationalNum;
        sy0Denom *= this.invScaleYRationalDenom;
        sx1Num *= this.invScaleXRationalNum;
        sx1Denom *= this.invScaleXRationalDenom;
        sy1Num *= this.invScaleYRationalNum;
        sy1Denom *= this.invScaleYRationalDenom;
        int s_x0 = 0;
        int s_y0 = 0;
        int s_x1 = 0;
        int s_y1 = 0;
        if (this.interp instanceof InterpolationNearest) {
            s_x0 = Rational.floor(sx0Num, sx0Denom);
            s_y0 = Rational.floor(sy0Num, sy0Denom);
            s_x1 = Rational.floor(sx1Num, sx1Denom);
            s_y1 = Rational.floor(sy1Num, sy1Denom);
        } else {
            s_x0 = Rational.floor(2L * sx0Num - sx0Denom, 2L * sx0Denom);
            s_y0 = Rational.floor(2L * sy0Num - sy0Denom, 2L * sy0Denom);
            s_x1 = Rational.floor(2L * sx1Num - sx1Denom, 2L * sx1Denom);
            s_y1 = Rational.floor(2L * sy1Num - sy1Denom, 2L * sy1Denom);
        }
        return new Rectangle(s_x0, s_y0, s_x1 - s_x0 + 1, s_y1 - s_y0 + 1);
    }

    public Raster computeTile(int tileX, int tileY) {
        if (!this.cobbleSources) {
            return super.computeTile(tileX, tileY);
        }
        int orgX = this.tileXToX(tileX);
        int orgY = this.tileYToY(tileY);
        WritableRaster dest = this.createWritableRaster(this.sampleModel, new Point(orgX, orgY));
        Rectangle rect = new Rectangle(orgX, orgY, this.tileWidth, this.tileHeight);
        Rectangle destRect = rect.intersection(this.computableBounds);
        if (destRect.width <= 0 || destRect.height <= 0) {
            return dest;
        }
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        Raster[] sources = new Raster[1];
        PlanarImage source0 = this.getSource(0);
        IntegerSequence srcXSplits = new IntegerSequence();
        IntegerSequence srcYSplits = new IntegerSequence();
        source0.getSplits(srcXSplits, srcYSplits, srcRect);
        if (srcXSplits.getNumElements() == 1 && srcYSplits.getNumElements() == 1) {
            sources[0] = this.extender == null ? source0.getData(srcRect) : source0.getExtendedData(srcRect, this.extender);
            this.computeRect(sources, dest, destRect);
        } else {
            int srcTileWidth = source0.getTileWidth();
            int srcTileHeight = source0.getTileHeight();
            srcYSplits.startEnumeration();
            while (srcYSplits.hasMoreElements()) {
                int ysplit = srcYSplits.nextElement();
                srcXSplits.startEnumeration();
                while (srcXSplits.hasMoreElements()) {
                    int xsplit = srcXSplits.nextElement();
                    Rectangle srcTile = new Rectangle(xsplit, ysplit, srcTileWidth, srcTileHeight);
                    Rectangle newSrcRect = srcRect.intersection(srcTile);
                    if (!(this.interp instanceof InterpolationNearest)) {
                        if (newSrcRect.width <= this.interp.getWidth()) {
                            Rectangle wSrcRect = new Rectangle();
                            wSrcRect.x = newSrcRect.x;
                            wSrcRect.y = newSrcRect.y - this.tpad - 1;
                            wSrcRect.width = 2 * (this.lpad + this.rpad + 1);
                            wSrcRect.height = newSrcRect.height + this.bpad + this.tpad + 2;
                            wSrcRect = wSrcRect.intersection(source0.getBounds());
                            Rectangle wDestRect = this.mapSourceRect(wSrcRect, 0);
                            wDestRect = wDestRect.intersection(destRect);
                            if (wDestRect.width > 0 && wDestRect.height > 0) {
                                sources[0] = this.extender == null ? source0.getData(wSrcRect) : source0.getExtendedData(wSrcRect, this.extender);
                                this.computeRect(sources, dest, wDestRect);
                            }
                        }
                        if (newSrcRect.height <= this.interp.getHeight()) {
                            Rectangle hSrcRect = new Rectangle();
                            hSrcRect.x = newSrcRect.x - this.lpad - 1;
                            hSrcRect.y = newSrcRect.y;
                            hSrcRect.width = newSrcRect.width + this.lpad + this.rpad + 2;
                            hSrcRect.height = 2 * (this.tpad + this.bpad + 1);
                            hSrcRect = hSrcRect.intersection(source0.getBounds());
                            Rectangle hDestRect = this.mapSourceRect(hSrcRect, 0);
                            hDestRect = hDestRect.intersection(destRect);
                            if (hDestRect.width > 0 && hDestRect.height > 0) {
                                sources[0] = this.extender == null ? source0.getData(hSrcRect) : source0.getExtendedData(hSrcRect, this.extender);
                                this.computeRect(sources, dest, hDestRect);
                            }
                        }
                    }
                    if (newSrcRect.width <= 0 || newSrcRect.height <= 0) continue;
                    Rectangle newDestRect = this.mapSourceRect(newSrcRect, 0);
                    newDestRect = newDestRect.intersection(destRect);
                    if (newDestRect.width > 0 && newDestRect.height > 0) {
                        sources[0] = this.extender == null ? source0.getData(newSrcRect) : source0.getExtendedData(newSrcRect, this.extender);
                        this.computeRect(sources, dest, newDestRect);
                    }
                    if (this.interp instanceof InterpolationNearest) continue;
                    Rectangle RTSrcRect = new Rectangle();
                    RTSrcRect.x = newSrcRect.x + newSrcRect.width - 1 - this.rpad - this.lpad;
                    RTSrcRect.y = newSrcRect.y;
                    RTSrcRect.width = 2 * (this.lpad + this.rpad + 1);
                    RTSrcRect.height = newSrcRect.height;
                    Rectangle RTDestRect = this.mapSourceRect(RTSrcRect, 0);
                    RTDestRect = RTDestRect.intersection(destRect);
                    RTSrcRect = this.mapDestRect(RTDestRect, 0);
                    if (RTDestRect.width > 0 && RTDestRect.height > 0) {
                        sources[0] = this.extender == null ? source0.getData(RTSrcRect) : source0.getExtendedData(RTSrcRect, this.extender);
                        this.computeRect(sources, dest, RTDestRect);
                    }
                    Rectangle BTSrcRect = new Rectangle();
                    BTSrcRect.x = newSrcRect.x;
                    BTSrcRect.y = newSrcRect.y + newSrcRect.height - 1 - this.bpad - this.tpad;
                    BTSrcRect.width = newSrcRect.width;
                    BTSrcRect.height = 2 * (this.tpad + this.bpad + 1);
                    Rectangle BTDestRect = this.mapSourceRect(BTSrcRect, 0);
                    BTDestRect = BTDestRect.intersection(destRect);
                    BTSrcRect = this.mapDestRect(BTDestRect, 0);
                    if (BTDestRect.width > 0 && BTDestRect.height > 0) {
                        sources[0] = this.extender == null ? source0.getData(BTSrcRect) : source0.getExtendedData(BTSrcRect, this.extender);
                        this.computeRect(sources, dest, BTDestRect);
                    }
                    Rectangle LRTSrcRect = new Rectangle();
                    LRTSrcRect.x = newSrcRect.x + newSrcRect.width - 1 - this.rpad - this.lpad;
                    LRTSrcRect.y = newSrcRect.y + newSrcRect.height - 1 - this.bpad - this.tpad;
                    LRTSrcRect.width = 2 * (this.rpad + this.lpad + 1);
                    LRTSrcRect.height = 2 * (this.tpad + this.bpad + 1);
                    Rectangle LRTDestRect = this.mapSourceRect(LRTSrcRect, 0);
                    LRTDestRect = LRTDestRect.intersection(destRect);
                    LRTSrcRect = this.mapDestRect(LRTDestRect, 0);
                    if (LRTDestRect.width <= 0 || LRTDestRect.height <= 0) continue;
                    sources[0] = this.extender == null ? source0.getData(LRTSrcRect) : source0.getExtendedData(LRTSrcRect, this.extender);
                    this.computeRect(sources, dest, LRTDestRect);
                }
            }
        }
        return dest;
    }
}

