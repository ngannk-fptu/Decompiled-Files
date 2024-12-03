/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.AffineOpImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.util.Range;

class AffineNearestOpImage
extends AffineOpImage {
    static /* synthetic */ Class class$java$lang$Integer;

    public AffineNearestOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues) {
        super(source, extender, config, layout, transform, interp, backgroundValues);
        ColorModel srcColorModel = source.getColorModel();
        if (srcColorModel instanceof IndexColorModel) {
            this.sampleModel = source.getSampleModel().createCompatibleSampleModel(this.tileWidth, this.tileHeight);
            this.colorModel = srcColorModel;
        }
    }

    protected Range performScanlineClipping(float src_rect_x1, float src_rect_y1, float src_rect_x2, float src_rect_y2, int s_ix, int s_iy, int ifracx, int ifracy, int dst_min_x, int dst_max_x, int lpad, int rpad, int tpad, int bpad) {
        int clipMinX = dst_min_x;
        int clipMaxX = dst_max_x;
        long xdenom = this.incx * 0x100000 + this.ifracdx;
        if (xdenom != 0L) {
            long clipx1 = (long)src_rect_x1 + (long)lpad;
            long clipx2 = (long)src_rect_x2 - (long)rpad;
            long x1 = (clipx1 - (long)s_ix) * 0x100000L - (long)ifracx + (long)dst_min_x * xdenom;
            long x2 = (clipx2 - (long)s_ix) * 0x100000L - (long)ifracx + (long)dst_min_x * xdenom;
            if (xdenom < 0L) {
                long tmp = x1;
                x1 = x2;
                x2 = tmp;
            }
            int dx1 = AffineNearestOpImage.ceilRatio(x1, xdenom);
            clipMinX = Math.max(clipMinX, dx1);
            int dx2 = AffineNearestOpImage.floorRatio(x2, xdenom) + 1;
            clipMaxX = Math.min(clipMaxX, dx2);
        } else if ((float)s_ix < src_rect_x1 || (float)s_ix >= src_rect_x2) {
            clipMinX = clipMaxX = dst_min_x;
            return new Range(class$java$lang$Integer == null ? (class$java$lang$Integer = AffineNearestOpImage.class$("java.lang.Integer")) : class$java$lang$Integer, new Integer(clipMinX), new Integer(clipMaxX));
        }
        long ydenom = this.incy * 0x100000 + this.ifracdy;
        if (ydenom != 0L) {
            long clipy1 = (long)src_rect_y1 + (long)tpad;
            long clipy2 = (long)src_rect_y2 - (long)bpad;
            long y1 = (clipy1 - (long)s_iy) * 0x100000L - (long)ifracy + (long)dst_min_x * ydenom;
            long y2 = (clipy2 - (long)s_iy) * 0x100000L - (long)ifracy + (long)dst_min_x * ydenom;
            if (ydenom < 0L) {
                long tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            int dx1 = AffineNearestOpImage.ceilRatio(y1, ydenom);
            clipMinX = Math.max(clipMinX, dx1);
            int dx2 = AffineNearestOpImage.floorRatio(y2, ydenom) + 1;
            clipMaxX = Math.min(clipMaxX, dx2);
        } else if ((float)s_iy < src_rect_y1 || (float)s_iy >= src_rect_y2) {
            clipMinX = clipMaxX = dst_min_x;
        }
        if (clipMinX > dst_max_x) {
            clipMinX = dst_max_x;
        }
        if (clipMaxX < dst_min_x) {
            clipMaxX = dst_min_x;
        }
        return new Range(class$java$lang$Integer == null ? (class$java$lang$Integer = AffineNearestOpImage.class$("java.lang.Integer")) : class$java$lang$Integer, new Integer(clipMinX), new Integer(clipMaxX));
    }

    protected Point[] advanceToStartOfScanline(int dst_min_x, int clipMinX, int s_ix, int s_iy, int ifracx, int ifracy) {
        long skip = clipMinX - dst_min_x;
        long dx = ((long)ifracx + skip * (long)this.ifracdx) / 0x100000L;
        long dy = ((long)ifracy + skip * (long)this.ifracdy) / 0x100000L;
        s_ix = (int)((long)s_ix + (skip * (long)this.incx + (long)((int)dx)));
        s_iy = (int)((long)s_iy + (skip * (long)this.incy + (long)((int)dy)));
        long lfracx = (long)ifracx + skip * (long)this.ifracdx;
        ifracx = lfracx >= 0L ? (int)(lfracx % 0x100000L) : (int)(-(-lfracx % 0x100000L));
        long lfracy = (long)ifracy + skip * (long)this.ifracdy;
        ifracy = lfracy >= 0L ? (int)(lfracy % 0x100000L) : (int)(-(-lfracy % 0x100000L));
        return new Point[]{new Point(s_ix, s_iy), new Point(ifracx, ifracy)};
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int srcRectX = srcRect.x;
        int srcRectY = srcRect.y;
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (dstAccessor.getDataType()) {
            case 0: {
                int dstNumBands = dstAccessor.getNumBands();
                if (dstNumBands == 1) {
                    this.byteLoop_1band(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                    break;
                }
                if (dstNumBands == 3) {
                    this.byteLoop_3band(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                    break;
                }
                this.byteLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 4: {
                this.floatLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 5: {
                this.doubleLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    private void byteLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        byte[] backgroundByte = new byte[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundByte[i] = (byte)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int k2;
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundByte[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                for (k2 = 0; k2 < dst_num_bands; ++k2) {
                    dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = srcDataArrays[k2][src_pos + bandOffsets[k2]];
                }
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundByte[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void byteLoop_1band(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[] dstDataArray0 = dstDataArrays[0];
        int dstBandOffset0 = dstBandOffsets[0];
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        byte[] srcDataArray0 = srcDataArrays[0];
        int bandOffsets0 = bandOffsets[0];
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        byte backgroundByte = (byte)this.backgroundValues[0];
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    dstDataArray0[dstPixelOffset + dstBandOffset0] = backgroundByte;
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                dstDataArray0[dstPixelOffset + dstBandOffset0] = srcDataArray0[src_pos + bandOffsets0];
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    dstDataArray0[dstPixelOffset + dstBandOffset0] = backgroundByte;
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void byteLoop_3band(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[] dstDataArray0 = dstDataArrays[0];
        byte[] dstDataArray1 = dstDataArrays[1];
        byte[] dstDataArray2 = dstDataArrays[2];
        int dstBandOffset0 = dstBandOffsets[0];
        int dstBandOffset1 = dstBandOffsets[1];
        int dstBandOffset2 = dstBandOffsets[2];
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        byte[] srcDataArray0 = srcDataArrays[0];
        byte[] srcDataArray1 = srcDataArrays[1];
        byte[] srcDataArray2 = srcDataArrays[2];
        int bandOffsets0 = bandOffsets[0];
        int bandOffsets1 = bandOffsets[1];
        int bandOffsets2 = bandOffsets[2];
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        byte background0 = (byte)this.backgroundValues[0];
        byte background1 = (byte)this.backgroundValues[1];
        byte background2 = (byte)this.backgroundValues[2];
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    dstDataArray0[dstPixelOffset + dstBandOffset0] = background0;
                    dstDataArray1[dstPixelOffset + dstBandOffset1] = background1;
                    dstDataArray2[dstPixelOffset + dstBandOffset2] = background2;
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                dstDataArray0[dstPixelOffset + dstBandOffset0] = srcDataArray0[src_pos + bandOffsets0];
                dstDataArray1[dstPixelOffset + dstBandOffset1] = srcDataArray1[src_pos + bandOffsets1];
                dstDataArray2[dstPixelOffset + dstBandOffset2] = srcDataArray2[src_pos + bandOffsets2];
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    dstDataArray0[dstPixelOffset + dstBandOffset0] = background0;
                    dstDataArray1[dstPixelOffset + dstBandOffset1] = background1;
                    dstDataArray2[dstPixelOffset + dstBandOffset2] = background2;
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void intLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[][] srcDataArrays = src.getIntDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        int[] backgroundInt = new int[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundInt[i] = (int)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int k2;
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundInt[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                for (k2 = 0; k2 < dst_num_bands; ++k2) {
                    dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = srcDataArrays[k2][src_pos + bandOffsets[k2]];
                }
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundInt[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void shortLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        short[] backgroundShort = new short[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundShort[i] = (short)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int k2;
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundShort[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                for (k2 = 0; k2 < dst_num_bands; ++k2) {
                    dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = srcDataArrays[k2][src_pos + bandOffsets[k2]];
                }
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundShort[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void floatLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        float[][] dstDataArrays = dst.getFloatDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        float[][] srcDataArrays = src.getFloatDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        float[] backgroundFloat = new float[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundFloat[i] = (float)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int k2;
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundFloat[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                for (k2 = 0; k2 < dst_num_bands; ++k2) {
                    dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = srcDataArrays[k2][src_pos + bandOffsets[k2]];
                }
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = backgroundFloat[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void doubleLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dst_num_bands = dst.getNumBands();
        int dst_min_x = destRect.x;
        int dst_min_y = destRect.y;
        int dst_max_x = destRect.x + destRect.width;
        int dst_max_y = destRect.y + destRect.height;
        int incxStride = this.incx * srcPixelStride;
        int incx1Stride = this.incx1 * srcPixelStride;
        int incyStride = this.incy * srcScanlineStride;
        int incy1Stride = this.incy1 * srcScanlineStride;
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int k2;
            int x;
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = (double)s_x - (double)s_ix;
            double fracy = (double)s_y - (double)s_iy;
            int ifracx = (int)Math.floor(fracx * 1048576.0);
            int ifracy = (int)Math.floor(fracy * 1048576.0);
            Range clipRange = this.performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0f, src_rect_y2 - 1.0f, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
            int clipMinX = (Integer)clipRange.getMinValue();
            int clipMaxX = (Integer)clipRange.getMaxValue();
            Point[] startPts = this.advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
            s_ix = startPts[0].x;
            s_iy = startPts[0].y;
            ifracx = startPts[1].x;
            ifracy = startPts[1].y;
            int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
            if (this.setBackground) {
                for (x = dst_min_x; x < clipMinX; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = this.backgroundValues[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            } else {
                dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
            }
            for (x = clipMinX; x < clipMaxX; ++x) {
                for (k2 = 0; k2 < dst_num_bands; ++k2) {
                    dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = srcDataArrays[k2][src_pos + bandOffsets[k2]];
                }
                if (ifracx < this.ifracdx1) {
                    src_pos += incxStride;
                    ifracx += this.ifracdx;
                } else {
                    src_pos += incx1Stride;
                    ifracx -= this.ifracdx1;
                }
                if (ifracy < this.ifracdy1) {
                    src_pos += incyStride;
                    ifracy += this.ifracdy;
                } else {
                    src_pos += incy1Stride;
                    ifracy -= this.ifracdy1;
                }
                dstPixelOffset += dstPixelStride;
            }
            if (this.setBackground && clipMinX <= clipMaxX) {
                for (x = clipMaxX; x < dst_max_x; ++x) {
                    for (k2 = 0; k2 < dst_num_bands; ++k2) {
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = this.backgroundValues[k2];
                    }
                    dstPixelOffset += dstPixelStride;
                }
            }
            dstOffset += dstScanlineStride;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

