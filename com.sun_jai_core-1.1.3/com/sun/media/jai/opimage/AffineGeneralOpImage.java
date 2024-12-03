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

final class AffineGeneralOpImage
extends AffineOpImage {
    private int subsampleBits;
    private int shiftvalue;
    private int interp_width;
    private int interp_height;
    private int interp_left;
    private int interp_top;
    private int interp_right;
    private int interp_bottom;

    public AffineGeneralOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues) {
        super(source, extender, config, layout, transform, interp, backgroundValues);
        this.subsampleBits = interp.getSubsampleBitsH();
        this.shiftvalue = 1 << this.subsampleBits;
        this.interp_width = interp.getWidth();
        this.interp_height = interp.getHeight();
        this.interp_left = interp.getLeftPadding();
        this.interp_top = interp.getTopPadding();
        this.interp_right = this.interp_width - this.interp_left - 1;
        this.interp_bottom = this.interp_height - this.interp_top - 1;
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
        int[][] samples = new int[this.interp_height][this.interp_width];
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
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                int k;
                if ((float)s_ix >= src_rect_x1 + (float)this.interp_left && (float)s_ix < src_rect_x2 - (float)this.interp_right && (float)s_iy >= src_rect_y1 + (float)this.interp_top && (float)s_iy < src_rect_y2 - (float)this.interp_bottom) {
                    for (k = 0; k < dst_num_bands; ++k) {
                        byte[] srcData = srcDataArrays[k];
                        int tmp = bandOffsets[k];
                        int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                        start = p_x + p_y - start;
                        int countH = 0;
                        int countV = 0;
                        for (int i = 0; i < this.interp_height; ++i) {
                            int startY = start;
                            for (int j = 0; j < this.interp_width; ++j) {
                                samples[countV][countH++] = srcData[start + tmp] & 0xFF;
                                start += srcPixelStride;
                            }
                            ++countV;
                            countH = 0;
                            start = startY + srcScanlineStride;
                        }
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        int s = this.interp.interpolate(samples, xfrac, yfrac);
                        int result = s < 0 ? 0 : (s > 255 ? 255 : s);
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = (byte)(result & 0xFF);
                    }
                } else if (this.setBackground) {
                    for (k = 0; k < dst_num_bands; ++k) {
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
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
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
        int[][] samples = new int[this.interp_height][this.interp_width];
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
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                int k;
                if ((float)s_ix >= src_rect_x1 + (float)this.interp_left && (float)s_ix < src_rect_x2 - (float)this.interp_right && (float)s_iy >= src_rect_y1 + (float)this.interp_top && (float)s_iy < src_rect_y2 - (float)this.interp_bottom) {
                    for (k = 0; k < dst_num_bands; ++k) {
                        int[] srcData = srcDataArrays[k];
                        int tmp = bandOffsets[k];
                        int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                        start = p_x + p_y - start;
                        int countH = 0;
                        int countV = 0;
                        for (int i = 0; i < this.interp_height; ++i) {
                            int startY = start;
                            for (int j = 0; j < this.interp_width; ++j) {
                                samples[countV][countH++] = srcData[start + tmp];
                                start += srcPixelStride;
                            }
                            ++countV;
                            countH = 0;
                            start = startY + srcScanlineStride;
                        }
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        int s = this.interp.interpolate(samples, xfrac, yfrac);
                        int result = s < Integer.MIN_VALUE ? Integer.MIN_VALUE : (s > Integer.MAX_VALUE ? Integer.MAX_VALUE : s);
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = result;
                    }
                } else if (this.setBackground) {
                    for (k = 0; k < dst_num_bands; ++k) {
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
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
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
        int[][] samples = new int[this.interp_height][this.interp_width];
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
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                int k;
                if ((float)s_ix >= src_rect_x1 + (float)this.interp_left && (float)s_ix < src_rect_x2 - (float)this.interp_right && (float)s_iy >= src_rect_y1 + (float)this.interp_top && (float)s_iy < src_rect_y2 - (float)this.interp_bottom) {
                    for (k = 0; k < dst_num_bands; ++k) {
                        short[] srcData = srcDataArrays[k];
                        int tmp = bandOffsets[k];
                        int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                        start = p_x + p_y - start;
                        int countH = 0;
                        int countV = 0;
                        for (int i = 0; i < this.interp_height; ++i) {
                            int startY = start;
                            for (int j = 0; j < this.interp_width; ++j) {
                                samples[countV][countH++] = srcData[start + tmp];
                                start += srcPixelStride;
                            }
                            ++countV;
                            countH = 0;
                            start = startY + srcScanlineStride;
                        }
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        int s = this.interp.interpolate(samples, xfrac, yfrac);
                        int result = s < Short.MIN_VALUE ? Short.MIN_VALUE : (s > Short.MAX_VALUE ? Short.MAX_VALUE : (int)((short)s));
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = result;
                    }
                } else if (this.setBackground) {
                    for (k = 0; k < dst_num_bands; ++k) {
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
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
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
        int[][] samples = new int[this.interp_height][this.interp_width];
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
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                int k;
                if ((float)s_ix >= src_rect_x1 + (float)this.interp_left && (float)s_ix < src_rect_x2 - (float)this.interp_right && (float)s_iy >= src_rect_y1 + (float)this.interp_top && (float)s_iy < src_rect_y2 - (float)this.interp_bottom) {
                    for (k = 0; k < dst_num_bands; ++k) {
                        short[] srcData = srcDataArrays[k];
                        int tmp = bandOffsets[k];
                        int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                        start = p_x + p_y - start;
                        int countH = 0;
                        int countV = 0;
                        for (int i = 0; i < this.interp_height; ++i) {
                            int startY = start;
                            for (int j = 0; j < this.interp_width; ++j) {
                                samples[countV][countH++] = srcData[start + tmp] & 0xFFFF;
                                start += srcPixelStride;
                            }
                            ++countV;
                            countH = 0;
                            start = startY + srcScanlineStride;
                        }
                        int xfrac = (int)(fracx * (float)this.shiftvalue);
                        int yfrac = (int)(fracy * (float)this.shiftvalue);
                        int s = this.interp.interpolate(samples, xfrac, yfrac);
                        int result = s < 0 ? 0 : (s > 65535 ? 65535 : s);
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = (short)(result & 0xFFFF);
                    }
                } else if (this.setBackground) {
                    for (k = 0; k < dst_num_bands; ++k) {
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
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
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
        float[][] samples = new float[this.interp_height][this.interp_width];
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
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                int k;
                if ((float)s_ix >= src_rect_x1 + (float)this.interp_left && (float)s_ix < src_rect_x2 - (float)this.interp_right && (float)s_iy >= src_rect_y1 + (float)this.interp_top && (float)s_iy < src_rect_y2 - (float)this.interp_bottom) {
                    for (k = 0; k < dst_num_bands; ++k) {
                        float s;
                        float[] srcData = srcDataArrays[k];
                        int tmp = bandOffsets[k];
                        int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                        start = p_x + p_y - start;
                        int countH = 0;
                        int countV = 0;
                        for (int i = 0; i < this.interp_height; ++i) {
                            int startY = start;
                            for (int j = 0; j < this.interp_width; ++j) {
                                samples[countV][countH++] = srcData[start + tmp];
                                start += srcPixelStride;
                            }
                            ++countV;
                            countH = 0;
                            start = startY + srcScanlineStride;
                        }
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = s = this.interp.interpolate(samples, fracx, fracy);
                    }
                } else if (this.setBackground) {
                    for (k = 0; k < dst_num_bands; ++k) {
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
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
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
        double[][] samples = new double[this.interp_height][this.interp_width];
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
            for (int x = dst_min_x; x < dst_max_x; ++x) {
                int k;
                if ((float)s_ix >= src_rect_x1 + (float)this.interp_left && (float)s_ix < src_rect_x2 - (float)this.interp_right && (float)s_iy >= src_rect_y1 + (float)this.interp_top && (float)s_iy < src_rect_y2 - (float)this.interp_bottom) {
                    for (k = 0; k < dst_num_bands; ++k) {
                        double s;
                        double[] srcData = srcDataArrays[k];
                        int tmp = bandOffsets[k];
                        int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                        start = p_x + p_y - start;
                        int countH = 0;
                        int countV = 0;
                        for (int i = 0; i < this.interp_height; ++i) {
                            int startY = start;
                            for (int j = 0; j < this.interp_width; ++j) {
                                samples[countV][countH++] = srcData[start + tmp];
                                start += srcPixelStride;
                            }
                            ++countV;
                            countH = 0;
                            start = startY + srcScanlineStride;
                        }
                        dstDataArrays[k][dstPixelOffset + dstBandOffsets[k]] = s = this.interp.interpolate(samples, (float)fracx, (float)fracy);
                    }
                } else if (this.setBackground) {
                    for (k = 0; k < dst_num_bands; ++k) {
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
                p_x = (s_ix - srcRectX) * srcPixelStride;
                p_y = (s_iy - srcRectY) * srcScanlineStride;
                dstPixelOffset += dstPixelStride;
            }
            dstOffset += dstScanlineStride;
        }
    }
}

