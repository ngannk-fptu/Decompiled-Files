/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;
import javax.media.jai.WarpPolynomial;

public abstract class WarpOpImage
extends GeometricOpImage {
    protected Warp warp;

    private static ImageLayout getLayout(ImageLayout layout, RenderedImage source, Warp warp) {
        if (layout != null && layout.isValid(15)) {
            return layout;
        }
        Rectangle sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
        Rectangle destBounds = warp.mapSourceRect(sourceBounds);
        if (destBounds == null) {
            Point[] srcPts = new Point[]{new Point(sourceBounds.x, sourceBounds.y), new Point(sourceBounds.x + sourceBounds.width, sourceBounds.y), new Point(sourceBounds.x, sourceBounds.y + sourceBounds.height), new Point(sourceBounds.x + sourceBounds.width, sourceBounds.y + sourceBounds.height)};
            boolean verticesMapped = true;
            double xMin = Double.MAX_VALUE;
            double xMax = -1.7976931348623157E308;
            double yMin = Double.MAX_VALUE;
            double yMax = -1.7976931348623157E308;
            for (int i = 0; i < 4; ++i) {
                Point2D destPt = warp.mapSourcePoint(srcPts[i]);
                if (destPt == null) {
                    verticesMapped = false;
                    break;
                }
                double x = destPt.getX();
                double y = destPt.getY();
                if (x < xMin) {
                    xMin = x;
                }
                if (x > xMax) {
                    xMax = x;
                }
                if (y < yMin) {
                    yMin = y;
                }
                if (!(y > yMax)) continue;
                yMax = y;
            }
            if (verticesMapped) {
                destBounds = new Rectangle();
                destBounds.x = (int)Math.floor(xMin);
                destBounds.y = (int)Math.floor(yMin);
                destBounds.width = (int)Math.ceil(xMax - (double)destBounds.x);
                destBounds.height = (int)Math.ceil(yMax - (double)destBounds.y);
            }
        }
        if (destBounds == null && !(warp instanceof WarpAffine)) {
            Point[] destPts = new Point[]{new Point(sourceBounds.x, sourceBounds.y), new Point(sourceBounds.x + sourceBounds.width, sourceBounds.y), new Point(sourceBounds.x, sourceBounds.y + sourceBounds.height), new Point(sourceBounds.x + sourceBounds.width, sourceBounds.y + sourceBounds.height)};
            float[] sourceCoords = new float[8];
            float[] destCoords = new float[8];
            int offset = 0;
            for (int i = 0; i < 4; ++i) {
                Point dstPt = destPts[i];
                Point2D srcPt = warp.mapDestPoint(destPts[i]);
                destCoords[offset] = (float)((Point2D)dstPt).getX();
                destCoords[offset + 1] = (float)((Point2D)dstPt).getY();
                sourceCoords[offset] = (float)srcPt.getX();
                sourceCoords[offset + 1] = (float)srcPt.getY();
                offset += 2;
            }
            WarpAffine wa = (WarpAffine)WarpPolynomial.createWarp(sourceCoords, 0, destCoords, 0, 8, 1.0f, 1.0f, 1.0f, 1.0f, 1);
            destBounds = wa.mapSourceRect(sourceBounds);
        }
        if (destBounds != null) {
            if (layout == null) {
                layout = new ImageLayout(destBounds.x, destBounds.y, destBounds.width, destBounds.height);
            } else {
                layout = (ImageLayout)layout.clone();
                layout.setMinX(destBounds.x);
                layout.setMinY(destBounds.y);
                layout.setWidth(destBounds.width);
                layout.setHeight(destBounds.height);
            }
        }
        return layout;
    }

    public WarpOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, Warp warp) {
        this(source, layout, configuration, cobbleSources, extender, interp, warp, null);
    }

    public WarpOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, Warp warp, double[] backgroundValues) {
        super(WarpOpImage.vectorize(source), WarpOpImage.getLayout(layout, source, warp), configuration, cobbleSources, extender, interp, backgroundValues);
        if (warp == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.warp = warp;
        if (cobbleSources && extender == null) {
            int l = interp == null ? 0 : interp.getLeftPadding();
            int r = interp == null ? 0 : interp.getRightPadding();
            int t = interp == null ? 0 : interp.getTopPadding();
            int b = interp == null ? 0 : interp.getBottomPadding();
            int x = this.getMinX() + l;
            int y = this.getMinY() + t;
            int w = Math.max(this.getWidth() - l - r, 0);
            int h = Math.max(this.getHeight() - t - b, 0);
            this.computableBounds = new Rectangle(x, y, w, h);
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
        return this.warp.mapDestPoint(destPt);
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        return this.warp.mapSourcePoint(sourcePt);
    }

    protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        return this.warp.mapSourceRect(sourceRect);
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        Rectangle wrect = this.warp.mapDestRect(destRect);
        return wrect == null ? this.getSource(0).getBounds() : wrect;
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle destRect = new Rectangle(org.x, org.y, this.tileWidth, this.tileHeight).intersection(this.computableBounds);
        if (destRect.isEmpty()) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect, this.backgroundValues);
            }
            return dest;
        }
        PlanarImage source = this.getSource(0);
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        if (!srcRect.intersects(source.getBounds())) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect, this.backgroundValues);
            }
            return dest;
        }
        if (this.cobbleSources) {
            Raster[] srcs = new Raster[]{this.extender != null ? source.getExtendedData(srcRect, this.extender) : source.getData(srcRect)};
            this.computeRect(srcs, dest, destRect);
            if (source.overlapsMultipleTiles(srcRect)) {
                this.recycleTile(srcs[0]);
            }
        } else {
            PlanarImage[] srcs = new PlanarImage[]{source};
            this.computeRect(srcs, dest, destRect);
        }
        return dest;
    }
}

