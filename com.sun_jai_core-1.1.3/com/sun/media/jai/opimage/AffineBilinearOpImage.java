/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.AffineOpImage;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class AffineBilinearOpImage
extends AffineOpImage {
    public AffineBilinearOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues) {
        super(source, extender, config, layout, transform, interp, backgroundValues);
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
                this.byteLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 2: {
                this.shortLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
                break;
            }
            case 1: {
                this.ushortLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
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
        int dnumBands = dst.getNumBands();
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
        byte[] backgroundByte = new byte[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundByte[i] = (byte)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            s_x = (float)((double)s_x - 0.5);
            s_y = (float)((double)s_y - 0.5);
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            float fracx = s_x - (float)s_ix;
            float fracy = s_y - (float)s_iy;
            int pylow = (s_iy - srcRectY) * srcScanlineStride;
            int pxlow = (s_ix - srcRectX) * srcPixelStride;
            int pyhigh = pylow + srcScanlineStride;
            int pxhigh = pxlow + srcPixelStride;
            int tmp00 = pxlow + pylow;
            int tmp01 = pxhigh + pylow;
            int tmp10 = pxlow + pyhigh;
            int tmp11 = pxhigh + pyhigh;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 && (float)s_ix < src_rect_x2 - 1.0f && (float)s_iy >= src_rect_y1 && (float)s_iy < src_rect_y2 - 1.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        byte[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        int s00 = tmp_row[tmp00 + tmp_col] & 0xFF;
                        int s01 = tmp_row[tmp01 + tmp_col] & 0xFF;
                        float s0 = (float)s00 + (float)(s01 - s00) * fracx;
                        int s10 = tmp_row[tmp10 + tmp_col] & 0xFF;
                        int s11 = tmp_row[tmp11 + tmp_col] & 0xFF;
                        float s1 = (float)s10 + (float)(s11 - s10) * fracx;
                        float tmp = s0 + (s1 - s0) * fracy;
                        int s = tmp < 0.5f ? 0 : (tmp > 254.5f ? 255 : (int)(tmp + 0.5f));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = (byte)(s & 0xFF);
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundByte[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    fracx = (float)((double)fracx + this.fracdx);
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    fracy = (float)((double)fracy + this.fracdy);
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                pylow = (s_iy - srcRectY) * srcScanlineStride;
                pxlow = (s_ix - srcRectX) * srcPixelStride;
                pyhigh = pylow + srcScanlineStride;
                pxhigh = pxlow + srcPixelStride;
                tmp00 = pxlow + pylow;
                tmp01 = pxhigh + pylow;
                tmp10 = pxlow + pyhigh;
                tmp11 = pxhigh + pyhigh;
                dstPixelOffset += dstPixelStride;
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
        int dnumBands = dst.getNumBands();
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
        int[] backgroundInt = new int[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundInt[i] = (int)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            s_x = (float)((double)s_x - 0.5);
            s_y = (float)((double)s_y - 0.5);
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            float fracx = s_x - (float)s_ix;
            float fracy = s_y - (float)s_iy;
            int pylow = (s_iy - srcRectY) * srcScanlineStride;
            int pxlow = (s_ix - srcRectX) * srcPixelStride;
            int pyhigh = pylow + srcScanlineStride;
            int pxhigh = pxlow + srcPixelStride;
            int tmp00 = pxlow + pylow;
            int tmp01 = pxhigh + pylow;
            int tmp10 = pxlow + pyhigh;
            int tmp11 = pxhigh + pyhigh;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 && (float)s_ix < src_rect_x2 - 1.0f && (float)s_iy >= src_rect_y1 && (float)s_iy < src_rect_y2 - 1.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        int[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        int s00 = tmp_row[tmp00 + tmp_col];
                        int s01 = tmp_row[tmp01 + tmp_col];
                        float s0 = (float)s00 + (float)(s01 - s00) * fracx;
                        int s10 = tmp_row[tmp10 + tmp_col];
                        int s11 = tmp_row[tmp11 + tmp_col];
                        float s1 = (float)s10 + (float)(s11 - s10) * fracx;
                        float tmp = s0 + (s1 - s0) * fracy;
                        int s = tmp < -2.14748365E9f ? Integer.MIN_VALUE : (tmp > 2.14748365E9f ? Integer.MAX_VALUE : (tmp > 0.0f ? (int)(tmp + 0.5f) : (int)(tmp - 0.5f)));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = s;
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundInt[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    fracx = (float)((double)fracx + this.fracdx);
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    fracy = (float)((double)fracy + this.fracdy);
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                pylow = (s_iy - srcRectY) * srcScanlineStride;
                pxlow = (s_ix - srcRectX) * srcPixelStride;
                pyhigh = pylow + srcScanlineStride;
                pxhigh = pxlow + srcPixelStride;
                tmp00 = pxlow + pylow;
                tmp01 = pxhigh + pylow;
                tmp10 = pxlow + pyhigh;
                tmp11 = pxhigh + pyhigh;
                dstPixelOffset += dstPixelStride;
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
        int dnumBands = dst.getNumBands();
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
        short[] backgroundShort = new short[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundShort[i] = (short)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            s_x = (float)((double)s_x - 0.5);
            s_y = (float)((double)s_y - 0.5);
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            float fracx = s_x - (float)s_ix;
            float fracy = s_y - (float)s_iy;
            int pylow = (s_iy - srcRectY) * srcScanlineStride;
            int pxlow = (s_ix - srcRectX) * srcPixelStride;
            int pyhigh = pylow + srcScanlineStride;
            int pxhigh = pxlow + srcPixelStride;
            int tmp00 = pxlow + pylow;
            int tmp01 = pxhigh + pylow;
            int tmp10 = pxlow + pyhigh;
            int tmp11 = pxhigh + pyhigh;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 && (float)s_ix < src_rect_x2 - 1.0f && (float)s_iy >= src_rect_y1 && (float)s_iy < src_rect_y2 - 1.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        short[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        short s00 = tmp_row[tmp00 + tmp_col];
                        short s01 = tmp_row[tmp01 + tmp_col];
                        float s0 = (float)s00 + (float)(s01 - s00) * fracx;
                        short s10 = tmp_row[tmp10 + tmp_col];
                        short s11 = tmp_row[tmp11 + tmp_col];
                        float s1 = (float)s10 + (float)(s11 - s10) * fracx;
                        float tmp = s0 + (s1 - s0) * fracy;
                        int s = tmp < -32768.0f ? Short.MIN_VALUE : (tmp > 32767.0f ? Short.MAX_VALUE : (tmp > 0.0f ? (int)(tmp + 0.5f) : (int)(tmp - 0.5f)));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = (short)s;
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundShort[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    fracx = (float)((double)fracx + this.fracdx);
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    fracy = (float)((double)fracy + this.fracdy);
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                pylow = (s_iy - srcRectY) * srcScanlineStride;
                pxlow = (s_ix - srcRectX) * srcPixelStride;
                pyhigh = pylow + srcScanlineStride;
                pxhigh = pxlow + srcPixelStride;
                tmp00 = pxlow + pylow;
                tmp01 = pxhigh + pylow;
                tmp10 = pxlow + pyhigh;
                tmp11 = pxhigh + pyhigh;
                dstPixelOffset += dstPixelStride;
            }
            dstOffset += dstScanlineStride;
        }
    }

    private void ushortLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst) {
        float src_rect_x1 = src.getX();
        float src_rect_y1 = src.getY();
        float src_rect_x2 = src_rect_x1 + (float)src.getWidth();
        float src_rect_y2 = src_rect_y1 + (float)src.getHeight();
        int dstOffset = 0;
        Point2D.Float dst_pt = new Point2D.Float();
        Point2D.Float src_pt = new Point2D.Float();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
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
        short[] backgroundUShort = new short[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundUShort[i] = (short)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            s_x = (float)((double)s_x - 0.5);
            s_y = (float)((double)s_y - 0.5);
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            float fracx = s_x - (float)s_ix;
            float fracy = s_y - (float)s_iy;
            int pylow = (s_iy - srcRectY) * srcScanlineStride;
            int pxlow = (s_ix - srcRectX) * srcPixelStride;
            int pyhigh = pylow + srcScanlineStride;
            int pxhigh = pxlow + srcPixelStride;
            int tmp00 = pxlow + pylow;
            int tmp01 = pxhigh + pylow;
            int tmp10 = pxlow + pyhigh;
            int tmp11 = pxhigh + pyhigh;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 && (float)s_ix < src_rect_x2 - 1.0f && (float)s_iy >= src_rect_y1 && (float)s_iy < src_rect_y2 - 1.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        short[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        int s00 = tmp_row[tmp00 + tmp_col] & 0xFFFF;
                        int s01 = tmp_row[tmp01 + tmp_col] & 0xFFFF;
                        float s0 = (float)s00 + (float)(s01 - s00) * fracx;
                        int s10 = tmp_row[tmp10 + tmp_col] & 0xFFFF;
                        int s11 = tmp_row[tmp11 + tmp_col] & 0xFFFF;
                        float s1 = (float)s10 + (float)(s11 - s10) * fracx;
                        float tmp = s0 + (s1 - s0) * fracy;
                        int s = (double)tmp < 0.0 ? 0 : (tmp > 65535.0f ? 65535 : (int)(tmp + 0.5f));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = (short)(s & 0xFFFF);
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundUShort[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    fracx = (float)((double)fracx + this.fracdx);
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    fracy = (float)((double)fracy + this.fracdy);
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                pylow = (s_iy - srcRectY) * srcScanlineStride;
                pxlow = (s_ix - srcRectX) * srcPixelStride;
                pyhigh = pylow + srcScanlineStride;
                pxhigh = pxlow + srcPixelStride;
                tmp00 = pxlow + pylow;
                tmp01 = pxhigh + pylow;
                tmp10 = pxlow + pyhigh;
                tmp11 = pxhigh + pyhigh;
                dstPixelOffset += dstPixelStride;
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
        int dnumBands = dst.getNumBands();
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
        float[] backgroundFloat = new float[dst_num_bands];
        for (int i = 0; i < dst_num_bands; ++i) {
            backgroundFloat[i] = (float)this.backgroundValues[i];
        }
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            s_x = (float)((double)s_x - 0.5);
            s_y = (float)((double)s_y - 0.5);
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            float fracx = s_x - (float)s_ix;
            float fracy = s_y - (float)s_iy;
            int pylow = (s_iy - srcRectY) * srcScanlineStride;
            int pxlow = (s_ix - srcRectX) * srcPixelStride;
            int pyhigh = pylow + srcScanlineStride;
            int pxhigh = pxlow + srcPixelStride;
            int tmp00 = pxlow + pylow;
            int tmp01 = pxhigh + pylow;
            int tmp10 = pxlow + pyhigh;
            int tmp11 = pxhigh + pyhigh;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 && (float)s_ix < src_rect_x2 - 1.0f && (float)s_iy >= src_rect_y1 && (float)s_iy < src_rect_y2 - 1.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        float s;
                        float[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        float s00 = tmp_row[tmp00 + tmp_col];
                        float s01 = tmp_row[tmp01 + tmp_col];
                        float s10 = tmp_row[tmp10 + tmp_col];
                        float s11 = tmp_row[tmp11 + tmp_col];
                        float s0 = s00 + (s01 - s00) * fracx;
                        float s1 = s10 + (s11 - s10) * fracx;
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = s = s0 + (s1 - s0) * fracy;
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundFloat[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    fracx = (float)((double)fracx + this.fracdx);
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    fracy = (float)((double)fracy + this.fracdy);
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                pylow = (s_iy - srcRectY) * srcScanlineStride;
                pxlow = (s_ix - srcRectX) * srcPixelStride;
                pyhigh = pylow + srcScanlineStride;
                pxhigh = pxlow + srcPixelStride;
                tmp00 = pxlow + pylow;
                tmp01 = pxhigh + pylow;
                tmp10 = pxlow + pyhigh;
                tmp11 = pxhigh + pyhigh;
                dstPixelOffset += dstPixelStride;
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
        int dnumBands = dst.getNumBands();
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
        for (int y = dst_min_y; y < dst_max_y; ++y) {
            int dstPixelOffset = dstOffset;
            ((Point2D)dst_pt).setLocation((double)dst_min_x + 0.5, (double)y + 0.5);
            this.mapDestPoint((Point2D)dst_pt, src_pt);
            float s_x = (float)((Point2D)src_pt).getX();
            float s_y = (float)((Point2D)src_pt).getY();
            s_x = (float)((double)s_x - 0.5);
            s_y = (float)((double)s_y - 0.5);
            int s_ix = (int)Math.floor(s_x);
            int s_iy = (int)Math.floor(s_y);
            double fracx = s_x - (float)s_ix;
            double fracy = s_y - (float)s_iy;
            int pylow = (s_iy - srcRectY) * srcScanlineStride;
            int pxlow = (s_ix - srcRectX) * srcPixelStride;
            int pyhigh = pylow + srcScanlineStride;
            int pxhigh = pxlow + srcPixelStride;
            int tmp00 = pxlow + pylow;
            int tmp01 = pxhigh + pylow;
            int tmp10 = pxlow + pyhigh;
            int tmp11 = pxhigh + pyhigh;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 && (float)s_ix < src_rect_x2 - 1.0f && (float)s_iy >= src_rect_y1 && (float)s_iy < src_rect_y2 - 1.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        double s;
                        double[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        double s00 = tmp_row[tmp00 + tmp_col];
                        double s01 = tmp_row[tmp01 + tmp_col];
                        double s10 = tmp_row[tmp10 + tmp_col];
                        double s11 = tmp_row[tmp11 + tmp_col];
                        double s0 = s00 + (s01 - s00) * fracx;
                        double s1 = s10 + (s11 - s10) * fracx;
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = s = s0 + (s1 - s0) * fracy;
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = this.backgroundValues[k];
                    }
                }
                if (fracx < this.fracdx1) {
                    s_ix += this.incx;
                    fracx += this.fracdx;
                } else {
                    s_ix += this.incx1;
                    fracx -= this.fracdx1;
                }
                if (fracy < this.fracdy1) {
                    s_iy += this.incy;
                    fracy += this.fracdy;
                } else {
                    s_iy += this.incy1;
                    fracy -= this.fracdy1;
                }
                pylow = (s_iy - srcRectY) * srcScanlineStride;
                pxlow = (s_ix - srcRectX) * srcPixelStride;
                pyhigh = pylow + srcScanlineStride;
                pxhigh = pxlow + srcPixelStride;
                tmp00 = pxlow + pylow;
                tmp01 = pxhigh + pylow;
                tmp10 = pxlow + pyhigh;
                tmp11 = pxhigh + pyhigh;
                dstPixelOffset += dstPixelStride;
            }
            dstOffset += dstScanlineStride;
        }
    }
}

