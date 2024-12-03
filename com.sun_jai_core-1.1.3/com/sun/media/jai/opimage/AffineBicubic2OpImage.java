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

final class AffineBicubic2OpImage
extends AffineOpImage {
    private int subsampleBits;
    private int shiftvalue;

    public AffineBicubic2OpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues) {
        super(source, extender, config, layout, transform, interp, backgroundValues);
        this.subsampleBits = interp.getSubsampleBitsH();
        this.shiftvalue = 1 << this.subsampleBits;
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
            int p_x = (s_ix - srcRectX) * srcPixelStride;
            int p_y = (s_iy - srcRectY) * srcScanlineStride;
            int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
            int p0_ = p__ + srcPixelStride;
            int p1_ = p0_ + srcPixelStride;
            int p2_ = p1_ + srcPixelStride;
            int p_0 = p__ + srcScanlineStride;
            int p00 = p_0 + srcPixelStride;
            int p10 = p00 + srcPixelStride;
            int p20 = p10 + srcPixelStride;
            int p_1 = p_0 + srcScanlineStride;
            int p01 = p_1 + srcPixelStride;
            int p11 = p01 + srcPixelStride;
            int p21 = p11 + srcPixelStride;
            int p_2 = p_1 + srcScanlineStride;
            int p02 = p_2 + srcPixelStride;
            int p12 = p02 + srcPixelStride;
            int p22 = p12 + srcPixelStride;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 + 1.0f && (float)s_ix < src_rect_x2 - 2.0f && (float)s_iy >= src_rect_y1 + 1.0f && (float)s_iy < src_rect_y2 - 2.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        byte[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        int s__ = tmp_row[p__ + tmp_col] & 0xFF;
                        int s0_ = tmp_row[p0_ + tmp_col] & 0xFF;
                        int s1_ = tmp_row[p1_ + tmp_col] & 0xFF;
                        int s2_ = tmp_row[p2_ + tmp_col] & 0xFF;
                        int s_0 = tmp_row[p_0 + tmp_col] & 0xFF;
                        int s00 = tmp_row[p00 + tmp_col] & 0xFF;
                        int s10 = tmp_row[p10 + tmp_col] & 0xFF;
                        int s20 = tmp_row[p20 + tmp_col] & 0xFF;
                        int s_1 = tmp_row[p_1 + tmp_col] & 0xFF;
                        int s01 = tmp_row[p01 + tmp_col] & 0xFF;
                        int s11 = tmp_row[p11 + tmp_col] & 0xFF;
                        int s21 = tmp_row[p21 + tmp_col] & 0xFF;
                        int s_2 = tmp_row[p_2 + tmp_col] & 0xFF;
                        int s02 = tmp_row[p02 + tmp_col] & 0xFF;
                        int s12 = tmp_row[p12 + tmp_col] & 0xFF;
                        int s22 = tmp_row[p22 + tmp_col] & 0xFF;
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        float s = this.interp.interpolate(s__, s0_, s1_, s2_, s_0, s00, s10, s20, s_1, s01, s11, s21, s_2, s02, s12, s22, xfrac, yfrac);
                        int result = s < 0.5f ? 0 : (s > 254.5f ? 255 : (int)(s + 0.5f));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = (byte)(result & 0xFF);
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundByte[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    if ((fracx = (float)((double)fracx + this.fracdx)) == 1.0f) {
                        fracx = 0.999999f;
                    }
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    if ((fracy = (float)((double)fracy + this.fracdy)) == 1.0f) {
                        fracy = 0.999999f;
                    }
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
                p0_ = p__ + srcPixelStride;
                p1_ = p0_ + srcPixelStride;
                p2_ = p1_ + srcPixelStride;
                p_0 = p__ + srcScanlineStride;
                p00 = p_0 + srcPixelStride;
                p10 = p00 + srcPixelStride;
                p20 = p10 + srcPixelStride;
                p_1 = p_0 + srcScanlineStride;
                p01 = p_1 + srcPixelStride;
                p11 = p01 + srcPixelStride;
                p21 = p11 + srcPixelStride;
                p_2 = p_1 + srcScanlineStride;
                p02 = p_2 + srcPixelStride;
                p12 = p02 + srcPixelStride;
                p22 = p12 + srcPixelStride;
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
            int p_x = (s_ix - srcRectX) * srcPixelStride;
            int p_y = (s_iy - srcRectY) * srcScanlineStride;
            int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
            int p0_ = p__ + srcPixelStride;
            int p1_ = p0_ + srcPixelStride;
            int p2_ = p1_ + srcPixelStride;
            int p_0 = p__ + srcScanlineStride;
            int p00 = p_0 + srcPixelStride;
            int p10 = p00 + srcPixelStride;
            int p20 = p10 + srcPixelStride;
            int p_1 = p_0 + srcScanlineStride;
            int p01 = p_1 + srcPixelStride;
            int p11 = p01 + srcPixelStride;
            int p21 = p11 + srcPixelStride;
            int p_2 = p_1 + srcScanlineStride;
            int p02 = p_2 + srcPixelStride;
            int p12 = p02 + srcPixelStride;
            int p22 = p12 + srcPixelStride;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 + 1.0f && (float)s_ix < src_rect_x2 - 2.0f && (float)s_iy >= src_rect_y1 + 1.0f && (float)s_iy < src_rect_y2 - 2.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        int[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        int s__ = tmp_row[p__ + tmp_col];
                        int s0_ = tmp_row[p0_ + tmp_col];
                        int s1_ = tmp_row[p1_ + tmp_col];
                        int s2_ = tmp_row[p2_ + tmp_col];
                        int s_0 = tmp_row[p_0 + tmp_col];
                        int s00 = tmp_row[p00 + tmp_col];
                        int s10 = tmp_row[p10 + tmp_col];
                        int s20 = tmp_row[p20 + tmp_col];
                        int s_1 = tmp_row[p_1 + tmp_col];
                        int s01 = tmp_row[p01 + tmp_col];
                        int s11 = tmp_row[p11 + tmp_col];
                        int s21 = tmp_row[p21 + tmp_col];
                        int s_2 = tmp_row[p_2 + tmp_col];
                        int s02 = tmp_row[p02 + tmp_col];
                        int s12 = tmp_row[p12 + tmp_col];
                        int s22 = tmp_row[p22 + tmp_col];
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        float s = this.interp.interpolate(s__, s0_, s1_, s2_, s_0, s00, s10, s20, s_1, s01, s11, s21, s_2, s02, s12, s22, xfrac, yfrac);
                        int result = s < -2.14748365E9f ? Integer.MIN_VALUE : (s > 2.14748365E9f ? Integer.MAX_VALUE : ((double)s > 0.0 ? (int)(s + 0.5f) : (int)(s - 0.5f)));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = result;
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundInt[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    if ((fracx = (float)((double)fracx + this.fracdx)) == 1.0f) {
                        fracx = 0.999999f;
                    }
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    if ((fracy = (float)((double)fracy + this.fracdy)) == 1.0f) {
                        fracy = 0.999999f;
                    }
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
                p0_ = p__ + srcPixelStride;
                p1_ = p0_ + srcPixelStride;
                p2_ = p1_ + srcPixelStride;
                p_0 = p__ + srcScanlineStride;
                p00 = p_0 + srcPixelStride;
                p10 = p00 + srcPixelStride;
                p20 = p10 + srcPixelStride;
                p_1 = p_0 + srcScanlineStride;
                p01 = p_1 + srcPixelStride;
                p11 = p01 + srcPixelStride;
                p21 = p11 + srcPixelStride;
                p_2 = p_1 + srcScanlineStride;
                p02 = p_2 + srcPixelStride;
                p12 = p02 + srcPixelStride;
                p22 = p12 + srcPixelStride;
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
            int p_x = (s_ix - srcRectX) * srcPixelStride;
            int p_y = (s_iy - srcRectY) * srcScanlineStride;
            int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
            int p0_ = p__ + srcPixelStride;
            int p1_ = p0_ + srcPixelStride;
            int p2_ = p1_ + srcPixelStride;
            int p_0 = p__ + srcScanlineStride;
            int p00 = p_0 + srcPixelStride;
            int p10 = p00 + srcPixelStride;
            int p20 = p10 + srcPixelStride;
            int p_1 = p_0 + srcScanlineStride;
            int p01 = p_1 + srcPixelStride;
            int p11 = p01 + srcPixelStride;
            int p21 = p11 + srcPixelStride;
            int p_2 = p_1 + srcScanlineStride;
            int p02 = p_2 + srcPixelStride;
            int p12 = p02 + srcPixelStride;
            int p22 = p12 + srcPixelStride;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 + 1.0f && (float)s_ix < src_rect_x2 - 2.0f && (float)s_iy >= src_rect_y1 + 1.0f && (float)s_iy < src_rect_y2 - 2.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        short[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        short s__ = tmp_row[p__ + tmp_col];
                        short s0_ = tmp_row[p0_ + tmp_col];
                        short s1_ = tmp_row[p1_ + tmp_col];
                        short s2_ = tmp_row[p2_ + tmp_col];
                        short s_0 = tmp_row[p_0 + tmp_col];
                        short s00 = tmp_row[p00 + tmp_col];
                        short s10 = tmp_row[p10 + tmp_col];
                        short s20 = tmp_row[p20 + tmp_col];
                        short s_1 = tmp_row[p_1 + tmp_col];
                        short s01 = tmp_row[p01 + tmp_col];
                        short s11 = tmp_row[p11 + tmp_col];
                        short s21 = tmp_row[p21 + tmp_col];
                        short s_2 = tmp_row[p_2 + tmp_col];
                        short s02 = tmp_row[p02 + tmp_col];
                        short s12 = tmp_row[p12 + tmp_col];
                        short s22 = tmp_row[p22 + tmp_col];
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        float s = this.interp.interpolate(s__, s0_, s1_, s2_, s_0, s00, s10, s20, s_1, s01, s11, s21, s_2, s02, s12, s22, xfrac, yfrac);
                        int result = s < -32768.0f ? Short.MIN_VALUE : (s > 32767.0f ? Short.MAX_VALUE : ((double)s > 0.0 ? (int)((short)(s + 0.5f)) : (int)((short)(s - 0.5f))));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = result;
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundShort[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    if ((fracx = (float)((double)fracx + this.fracdx)) == 1.0f) {
                        fracx = 0.999999f;
                    }
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    if ((fracy = (float)((double)fracy + this.fracdy)) == 1.0f) {
                        fracy = 0.999999f;
                    }
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
                p0_ = p__ + srcPixelStride;
                p1_ = p0_ + srcPixelStride;
                p2_ = p1_ + srcPixelStride;
                p_0 = p__ + srcScanlineStride;
                p00 = p_0 + srcPixelStride;
                p10 = p00 + srcPixelStride;
                p20 = p10 + srcPixelStride;
                p_1 = p_0 + srcScanlineStride;
                p01 = p_1 + srcPixelStride;
                p11 = p01 + srcPixelStride;
                p21 = p11 + srcPixelStride;
                p_2 = p_1 + srcScanlineStride;
                p02 = p_2 + srcPixelStride;
                p12 = p02 + srcPixelStride;
                p22 = p12 + srcPixelStride;
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
            int p_x = (s_ix - srcRectX) * srcPixelStride;
            int p_y = (s_iy - srcRectY) * srcScanlineStride;
            int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
            int p0_ = p__ + srcPixelStride;
            int p1_ = p0_ + srcPixelStride;
            int p2_ = p1_ + srcPixelStride;
            int p_0 = p__ + srcScanlineStride;
            int p00 = p_0 + srcPixelStride;
            int p10 = p00 + srcPixelStride;
            int p20 = p10 + srcPixelStride;
            int p_1 = p_0 + srcScanlineStride;
            int p01 = p_1 + srcPixelStride;
            int p11 = p01 + srcPixelStride;
            int p21 = p11 + srcPixelStride;
            int p_2 = p_1 + srcScanlineStride;
            int p02 = p_2 + srcPixelStride;
            int p12 = p02 + srcPixelStride;
            int p22 = p12 + srcPixelStride;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 + 1.0f && (float)s_ix < src_rect_x2 - 2.0f && (float)s_iy >= src_rect_y1 + 1.0f && (float)s_iy < src_rect_y2 - 2.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        short[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        int s__ = tmp_row[p__ + tmp_col] & 0xFFFF;
                        int s0_ = tmp_row[p0_ + tmp_col] & 0xFFFF;
                        int s1_ = tmp_row[p1_ + tmp_col] & 0xFFFF;
                        int s2_ = tmp_row[p2_ + tmp_col] & 0xFFFF;
                        int s_0 = tmp_row[p_0 + tmp_col] & 0xFFFF;
                        int s00 = tmp_row[p00 + tmp_col] & 0xFFFF;
                        int s10 = tmp_row[p10 + tmp_col] & 0xFFFF;
                        int s20 = tmp_row[p20 + tmp_col] & 0xFFFF;
                        int s_1 = tmp_row[p_1 + tmp_col] & 0xFFFF;
                        int s01 = tmp_row[p01 + tmp_col] & 0xFFFF;
                        int s11 = tmp_row[p11 + tmp_col] & 0xFFFF;
                        int s21 = tmp_row[p21 + tmp_col] & 0xFFFF;
                        int s_2 = tmp_row[p_2 + tmp_col] & 0xFFFF;
                        int s02 = tmp_row[p02 + tmp_col] & 0xFFFF;
                        int s12 = tmp_row[p12 + tmp_col] & 0xFFFF;
                        int s22 = tmp_row[p22 + tmp_col] & 0xFFFF;
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        float s = this.interp.interpolate(s__, s0_, s1_, s2_, s_0, s00, s10, s20, s_1, s01, s11, s21, s_2, s02, s12, s22, xfrac, yfrac);
                        int result = (double)s < 0.0 ? 0 : (s > 65535.0f ? 65535 : (int)(s + 0.5f));
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = (short)(result & 0xFFFF);
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundUShort[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    if ((fracx = (float)((double)fracx + this.fracdx)) == 1.0f) {
                        fracx = 0.999999f;
                    }
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    if ((fracy = (float)((double)fracy + this.fracdy)) == 1.0f) {
                        fracy = 0.999999f;
                    }
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
                p0_ = p__ + srcPixelStride;
                p1_ = p0_ + srcPixelStride;
                p2_ = p1_ + srcPixelStride;
                p_0 = p__ + srcScanlineStride;
                p00 = p_0 + srcPixelStride;
                p10 = p00 + srcPixelStride;
                p20 = p10 + srcPixelStride;
                p_1 = p_0 + srcScanlineStride;
                p01 = p_1 + srcPixelStride;
                p11 = p01 + srcPixelStride;
                p21 = p11 + srcPixelStride;
                p_2 = p_1 + srcScanlineStride;
                p02 = p_2 + srcPixelStride;
                p12 = p02 + srcPixelStride;
                p22 = p12 + srcPixelStride;
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
            int p_x = (s_ix - srcRectX) * srcPixelStride;
            int p_y = (s_iy - srcRectY) * srcScanlineStride;
            int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
            int p0_ = p__ + srcPixelStride;
            int p1_ = p0_ + srcPixelStride;
            int p2_ = p1_ + srcPixelStride;
            int p_0 = p__ + srcScanlineStride;
            int p00 = p_0 + srcPixelStride;
            int p10 = p00 + srcPixelStride;
            int p20 = p10 + srcPixelStride;
            int p_1 = p_0 + srcScanlineStride;
            int p01 = p_1 + srcPixelStride;
            int p11 = p01 + srcPixelStride;
            int p21 = p11 + srcPixelStride;
            int p_2 = p_1 + srcScanlineStride;
            int p02 = p_2 + srcPixelStride;
            int p12 = p02 + srcPixelStride;
            int p22 = p12 + srcPixelStride;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 + 1.0f && (float)s_ix < src_rect_x2 - 2.0f && (float)s_iy >= src_rect_y1 + 1.0f && (float)s_iy < src_rect_y2 - 2.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        float s;
                        float[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        float s__ = tmp_row[p__ + tmp_col];
                        float s0_ = tmp_row[p0_ + tmp_col];
                        float s1_ = tmp_row[p1_ + tmp_col];
                        float s2_ = tmp_row[p2_ + tmp_col];
                        float s_0 = tmp_row[p_0 + tmp_col];
                        float s00 = tmp_row[p00 + tmp_col];
                        float s10 = tmp_row[p10 + tmp_col];
                        float s20 = tmp_row[p20 + tmp_col];
                        float s_1 = tmp_row[p_1 + tmp_col];
                        float s01 = tmp_row[p01 + tmp_col];
                        float s11 = tmp_row[p11 + tmp_col];
                        float s21 = tmp_row[p21 + tmp_col];
                        float s_2 = tmp_row[p_2 + tmp_col];
                        float s02 = tmp_row[p02 + tmp_col];
                        float s12 = tmp_row[p12 + tmp_col];
                        float s22 = tmp_row[p22 + tmp_col];
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = s = this.interp.interpolate(s__, s0_, s1_, s2_, s_0, s00, s10, s20, s_1, s01, s11, s21, s_2, s02, s12, s22, fracx, fracy);
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = backgroundFloat[k];
                    }
                }
                if ((double)fracx < this.fracdx1) {
                    s_ix += this.incx;
                    if ((fracx = (float)((double)fracx + this.fracdx)) == 1.0f) {
                        fracx = 0.999999f;
                    }
                } else {
                    s_ix += this.incx1;
                    fracx = (float)((double)fracx - this.fracdx1);
                }
                if ((double)fracy < this.fracdy1) {
                    s_iy += this.incy;
                    if ((fracy = (float)((double)fracy + this.fracdy)) == 1.0f) {
                        fracy = 0.999999f;
                    }
                } else {
                    s_iy += this.incy1;
                    fracy = (float)((double)fracy - this.fracdy1);
                }
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
                p0_ = p__ + srcPixelStride;
                p1_ = p0_ + srcPixelStride;
                p2_ = p1_ + srcPixelStride;
                p_0 = p__ + srcScanlineStride;
                p00 = p_0 + srcPixelStride;
                p10 = p00 + srcPixelStride;
                p20 = p10 + srcPixelStride;
                p_1 = p_0 + srcScanlineStride;
                p01 = p_1 + srcPixelStride;
                p11 = p01 + srcPixelStride;
                p21 = p11 + srcPixelStride;
                p_2 = p_1 + srcScanlineStride;
                p02 = p_2 + srcPixelStride;
                p12 = p02 + srcPixelStride;
                p22 = p12 + srcPixelStride;
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
            double s_x = ((Point2D)src_pt).getX();
            double s_y = ((Point2D)src_pt).getY();
            int s_ix = (int)Math.floor(s_x -= 0.5);
            int s_iy = (int)Math.floor(s_y -= 0.5);
            double fracx = s_x - (double)s_ix;
            double fracy = s_y - (double)s_iy;
            int p_x = (s_ix - srcRectX) * srcPixelStride;
            int p_y = (s_iy - srcRectY) * srcScanlineStride;
            int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
            int p0_ = p__ + srcPixelStride;
            int p1_ = p0_ + srcPixelStride;
            int p2_ = p1_ + srcPixelStride;
            int p_0 = p__ + srcScanlineStride;
            int p00 = p_0 + srcPixelStride;
            int p10 = p00 + srcPixelStride;
            int p20 = p10 + srcPixelStride;
            int p_1 = p_0 + srcScanlineStride;
            int p01 = p_1 + srcPixelStride;
            int p11 = p01 + srcPixelStride;
            int p21 = p11 + srcPixelStride;
            int p_2 = p_1 + srcScanlineStride;
            int p02 = p_2 + srcPixelStride;
            int p12 = p02 + srcPixelStride;
            int p22 = p12 + srcPixelStride;
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                if ((float)s_ix >= src_rect_x1 + 1.0f && (float)s_ix < src_rect_x2 - 2.0f && (float)s_iy >= src_rect_y1 + 1.0f && (float)s_iy < src_rect_y2 - 2.0f) {
                    for (int k2 = 0; k2 < dst_num_bands; ++k2) {
                        double s;
                        double[] tmp_row = srcDataArrays[k2];
                        int tmp_col = bandOffsets[k2];
                        double s__ = tmp_row[p__ + tmp_col];
                        double s0_ = tmp_row[p0_ + tmp_col];
                        double s1_ = tmp_row[p1_ + tmp_col];
                        double s2_ = tmp_row[p2_ + tmp_col];
                        double s_0 = tmp_row[p_0 + tmp_col];
                        double s00 = tmp_row[p00 + tmp_col];
                        double s10 = tmp_row[p10 + tmp_col];
                        double s20 = tmp_row[p20 + tmp_col];
                        double s_1 = tmp_row[p_1 + tmp_col];
                        double s01 = tmp_row[p01 + tmp_col];
                        double s11 = tmp_row[p11 + tmp_col];
                        double s21 = tmp_row[p21 + tmp_col];
                        double s_2 = tmp_row[p_2 + tmp_col];
                        double s02 = tmp_row[p02 + tmp_col];
                        double s12 = tmp_row[p12 + tmp_col];
                        double s22 = tmp_row[p22 + tmp_col];
                        dstDataArrays[k2][dstPixelOffset + dstBandOffsets[k2]] = s = this.interp.interpolate(s__, s0_, s1_, s2_, s_0, s00, s10, s20, s_1, s01, s11, s21, s_2, s02, s12, s22, (float)fracx, (float)fracy);
                    }
                } else if (this.setBackground) {
                    for (int k = 0; k < dst_num_bands; ++k) {
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = this.backgroundValues[k];
                    }
                }
                if (fracx < this.fracdx1) {
                    s_ix += this.incx;
                    if ((fracx += this.fracdx) == 1.0) {
                        fracx = 0.999999;
                    }
                } else {
                    s_ix += this.incx1;
                    fracx -= this.fracdx1;
                }
                if (fracy < this.fracdy1) {
                    s_iy += this.incy;
                    if ((fracy += this.fracdy) == 1.0) {
                        fracy = 0.999999;
                    }
                } else {
                    s_iy += this.incy1;
                    fracy -= this.fracdy1;
                }
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
                p0_ = p__ + srcPixelStride;
                p1_ = p0_ + srcPixelStride;
                p2_ = p1_ + srcPixelStride;
                p_0 = p__ + srcScanlineStride;
                p00 = p_0 + srcPixelStride;
                p10 = p00 + srcPixelStride;
                p20 = p10 + srcPixelStride;
                p_1 = p_0 + srcScanlineStride;
                p01 = p_1 + srcPixelStride;
                p11 = p01 + srcPixelStride;
                p21 = p11 + srcPixelStride;
                p_2 = p_1 + srcScanlineStride;
                p02 = p_2 + srcPixelStride;
                p12 = p02 + srcPixelStride;
                p22 = p12 + srcPixelStride;
                dstPixelOffset += dstPixelStride;
            }
            dstOffset += dstScanlineStride;
        }
    }
}

