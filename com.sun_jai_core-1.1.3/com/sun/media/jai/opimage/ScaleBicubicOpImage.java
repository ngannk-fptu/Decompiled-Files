/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.Rational;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.ScaleOpImage;

final class ScaleBicubicOpImage
extends ScaleOpImage {
    private int subsampleBits;
    private int one;
    private int[] tableDataHi = null;
    private int[] tableDataVi = null;
    private float[] tableDataHf = null;
    private float[] tableDataVf = null;
    private double[] tableDataHd = null;
    private double[] tableDataVd = null;
    private int precisionBits;
    private int round;
    private Rational half = new Rational(1L, 2L);
    InterpolationTable interpTable;
    long invScaleYInt;
    long invScaleYFrac;
    long invScaleXInt;
    long invScaleXFrac;

    public ScaleBicubicOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
        this.subsampleBits = interp.getSubsampleBitsH();
        this.interpTable = (InterpolationTable)interp;
        this.one = 1 << this.subsampleBits;
        this.precisionBits = this.interpTable.getPrecisionBits();
        if (this.precisionBits > 0) {
            this.round = 1 << this.precisionBits - 1;
        }
        if (this.invScaleYRational.num > this.invScaleYRational.denom) {
            this.invScaleYInt = this.invScaleYRational.num / this.invScaleYRational.denom;
            this.invScaleYFrac = this.invScaleYRational.num % this.invScaleYRational.denom;
        } else {
            this.invScaleYInt = 0L;
            this.invScaleYFrac = this.invScaleYRational.num;
        }
        if (this.invScaleXRational.num > this.invScaleXRational.denom) {
            this.invScaleXInt = this.invScaleXRational.num / this.invScaleXRational.denom;
            this.invScaleXFrac = this.invScaleXRational.num % this.invScaleXRational.denom;
        } else {
            this.invScaleXInt = 0L;
            this.invScaleXFrac = this.invScaleXRational.num;
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int i;
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int srcRectX = srcRect.x;
        int srcRectY = srcRect.y;
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int[] ypos = new int[dheight];
        int[] xpos = new int[dwidth];
        int[] yfracvalues = new int[dheight];
        int[] xfracvalues = new int[dwidth];
        long syNum = dy;
        long syDenom = 1L;
        syNum = syNum * this.transYRationalDenom - this.transYRationalNum * syDenom;
        syNum = 2L * syNum + (syDenom *= this.transYRationalDenom);
        syDenom *= 2L;
        syNum *= this.invScaleYRationalNum;
        syNum = 2L * syNum - (syDenom *= this.invScaleYRationalDenom);
        int srcYInt = Rational.floor(syNum, syDenom *= 2L);
        long srcYFrac = syNum % syDenom;
        if (srcYInt < 0) {
            srcYFrac = syDenom + srcYFrac;
        }
        long commonYDenom = syDenom * this.invScaleYRationalDenom;
        srcYFrac *= this.invScaleYRationalDenom;
        long newInvScaleYFrac = this.invScaleYFrac * syDenom;
        long sxNum = dx;
        long sxDenom = 1L;
        sxNum = sxNum * this.transXRationalDenom - this.transXRationalNum * sxDenom;
        sxNum = 2L * sxNum + (sxDenom *= this.transXRationalDenom);
        sxDenom *= 2L;
        sxNum *= this.invScaleXRationalNum;
        sxNum = 2L * sxNum - (sxDenom *= this.invScaleXRationalDenom);
        int srcXInt = Rational.floor(sxNum, sxDenom *= 2L);
        long srcXFrac = sxNum % sxDenom;
        if (srcXInt < 0) {
            srcXFrac = sxDenom + srcXFrac;
        }
        long commonXDenom = sxDenom * this.invScaleXRationalDenom;
        srcXFrac *= this.invScaleXRationalDenom;
        long newInvScaleXFrac = this.invScaleXFrac * sxDenom;
        for (i = 0; i < dwidth; ++i) {
            xpos[i] = (srcXInt - srcRectX) * srcPixelStride;
            xfracvalues[i] = (int)((float)srcXFrac / (float)commonXDenom * (float)this.one);
            srcXInt = (int)((long)srcXInt + this.invScaleXInt);
            if ((srcXFrac += newInvScaleXFrac) < commonXDenom) continue;
            ++srcXInt;
            srcXFrac -= commonXDenom;
        }
        for (i = 0; i < dheight; ++i) {
            ypos[i] = (srcYInt - srcRectY) * srcScanlineStride;
            yfracvalues[i] = (int)((float)srcYFrac / (float)commonYDenom * (float)this.one);
            srcYInt = (int)((long)srcYInt + this.invScaleYInt);
            if ((srcYFrac += newInvScaleYFrac) < commonYDenom) continue;
            ++srcYInt;
            srcYFrac -= commonYDenom;
        }
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.initTableDataI();
                this.byteLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 2: {
                this.initTableDataI();
                this.shortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 1: {
                this.initTableDataI();
                this.ushortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 3: {
                this.initTableDataI();
                this.intLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 4: {
                this.initTableDataF();
                this.floatLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 5: {
                this.initTableDataD();
                this.doubleLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    private void byteLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dnumBands = dst.getNumBands();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                int posylow = posy - srcScanlineStride;
                int posyhigh = posy + srcScanlineStride;
                int posyhigh2 = posyhigh + srcScanlineStride;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int posxlow = posx - srcPixelStride;
                    int posxhigh = posx + srcPixelStride;
                    int posxhigh2 = posxhigh + srcPixelStride;
                    int s__ = srcData[posxlow + posylow] & 0xFF;
                    int s_0 = srcData[posx + posylow] & 0xFF;
                    int s_1 = srcData[posxhigh + posylow] & 0xFF;
                    int s_2 = srcData[posxhigh2 + posylow] & 0xFF;
                    int s0_ = srcData[posxlow + posy] & 0xFF;
                    int s00 = srcData[posx + posy] & 0xFF;
                    int s01 = srcData[posxhigh + posy] & 0xFF;
                    int s02 = srcData[posxhigh2 + posy] & 0xFF;
                    int s1_ = srcData[posxlow + posyhigh] & 0xFF;
                    int s10 = srcData[posx + posyhigh] & 0xFF;
                    int s11 = srcData[posxhigh + posyhigh] & 0xFF;
                    int s12 = srcData[posxhigh2 + posyhigh] & 0xFF;
                    int s2_ = srcData[posxlow + posyhigh2] & 0xFF;
                    int s20 = srcData[posx + posyhigh2] & 0xFF;
                    int s21 = srcData[posxhigh + posyhigh2] & 0xFF;
                    int s22 = srcData[posxhigh2 + posyhigh2] & 0xFF;
                    int offsetX = 4 * xfrac;
                    int offsetX1 = offsetX + 1;
                    int offsetX2 = offsetX + 2;
                    int offsetX3 = offsetX + 3;
                    long sum_ = (long)this.tableDataHi[offsetX] * (long)s__;
                    sum_ += (long)this.tableDataHi[offsetX1] * (long)s_0;
                    sum_ += (long)this.tableDataHi[offsetX2] * (long)s_1;
                    sum_ += (long)this.tableDataHi[offsetX3] * (long)s_2;
                    long sum0 = (long)this.tableDataHi[offsetX] * (long)s0_;
                    sum0 += (long)this.tableDataHi[offsetX1] * (long)s00;
                    sum0 += (long)this.tableDataHi[offsetX2] * (long)s01;
                    sum0 += (long)this.tableDataHi[offsetX3] * (long)s02;
                    long sum1 = (long)this.tableDataHi[offsetX] * (long)s1_;
                    sum1 += (long)this.tableDataHi[offsetX1] * (long)s10;
                    sum1 += (long)this.tableDataHi[offsetX2] * (long)s11;
                    sum1 += (long)this.tableDataHi[offsetX3] * (long)s12;
                    long sum2 = (long)this.tableDataHi[offsetX] * (long)s2_;
                    sum2 += (long)this.tableDataHi[offsetX1] * (long)s20;
                    sum2 += (long)this.tableDataHi[offsetX2] * (long)s21;
                    sum2 += (long)this.tableDataHi[offsetX3] * (long)s22;
                    sum_ = sum_ + (long)this.round >> this.precisionBits;
                    sum0 = sum0 + (long)this.round >> this.precisionBits;
                    sum1 = sum1 + (long)this.round >> this.precisionBits;
                    sum2 = sum2 + (long)this.round >> this.precisionBits;
                    int offsetY = 4 * yfrac;
                    long sum = (long)this.tableDataVi[offsetY] * sum_;
                    sum += (long)this.tableDataVi[offsetY + 1] * sum0;
                    sum += (long)this.tableDataVi[offsetY + 2] * sum1;
                    int s = (int)((sum += (long)this.tableDataVi[offsetY + 3] * sum2) + (long)this.round >> this.precisionBits);
                    if (s > 255) {
                        s = 255;
                    } else if (s < 0) {
                        s = 0;
                    }
                    dstData[dstPixelOffset] = (byte)(s & 0xFF);
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void shortLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dnumBands = dst.getNumBands();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                int posylow = posy - srcScanlineStride;
                int posyhigh = posy + srcScanlineStride;
                int posyhigh2 = posyhigh + srcScanlineStride;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int posxlow = posx - srcPixelStride;
                    int posxhigh = posx + srcPixelStride;
                    int posxhigh2 = posxhigh + srcPixelStride;
                    short s__ = srcData[posxlow + posylow];
                    short s_0 = srcData[posx + posylow];
                    short s_1 = srcData[posxhigh + posylow];
                    short s_2 = srcData[posxhigh2 + posylow];
                    short s0_ = srcData[posxlow + posy];
                    short s00 = srcData[posx + posy];
                    short s01 = srcData[posxhigh + posy];
                    short s02 = srcData[posxhigh2 + posy];
                    short s1_ = srcData[posxlow + posyhigh];
                    short s10 = srcData[posx + posyhigh];
                    short s11 = srcData[posxhigh + posyhigh];
                    short s12 = srcData[posxhigh2 + posyhigh];
                    short s2_ = srcData[posxlow + posyhigh2];
                    short s20 = srcData[posx + posyhigh2];
                    short s21 = srcData[posxhigh + posyhigh2];
                    short s22 = srcData[posxhigh2 + posyhigh2];
                    int offsetX = 4 * xfrac;
                    int offsetX1 = offsetX + 1;
                    int offsetX2 = offsetX + 2;
                    int offsetX3 = offsetX + 3;
                    long sum_ = (long)this.tableDataHi[offsetX] * (long)s__;
                    sum_ += (long)this.tableDataHi[offsetX1] * (long)s_0;
                    sum_ += (long)this.tableDataHi[offsetX2] * (long)s_1;
                    sum_ += (long)this.tableDataHi[offsetX3] * (long)s_2;
                    long sum0 = (long)this.tableDataHi[offsetX] * (long)s0_;
                    sum0 += (long)this.tableDataHi[offsetX1] * (long)s00;
                    sum0 += (long)this.tableDataHi[offsetX2] * (long)s01;
                    sum0 += (long)this.tableDataHi[offsetX3] * (long)s02;
                    long sum1 = (long)this.tableDataHi[offsetX] * (long)s1_;
                    sum1 += (long)this.tableDataHi[offsetX1] * (long)s10;
                    sum1 += (long)this.tableDataHi[offsetX2] * (long)s11;
                    sum1 += (long)this.tableDataHi[offsetX3] * (long)s12;
                    long sum2 = (long)this.tableDataHi[offsetX] * (long)s2_;
                    sum2 += (long)this.tableDataHi[offsetX1] * (long)s20;
                    sum2 += (long)this.tableDataHi[offsetX2] * (long)s21;
                    sum2 += (long)this.tableDataHi[offsetX3] * (long)s22;
                    sum_ = sum_ + (long)this.round >> this.precisionBits;
                    sum0 = sum0 + (long)this.round >> this.precisionBits;
                    sum1 = sum1 + (long)this.round >> this.precisionBits;
                    sum2 = sum2 + (long)this.round >> this.precisionBits;
                    int offsetY = 4 * yfrac;
                    long sum = (long)this.tableDataVi[offsetY] * sum_;
                    sum += (long)this.tableDataVi[offsetY + 1] * sum0;
                    sum += (long)this.tableDataVi[offsetY + 2] * sum1;
                    int s = (int)((sum += (long)this.tableDataVi[offsetY + 3] * sum2) + (long)this.round >> this.precisionBits);
                    if (s > Short.MAX_VALUE) {
                        s = Short.MAX_VALUE;
                    } else if (s < Short.MIN_VALUE) {
                        s = Short.MIN_VALUE;
                    }
                    dstData[dstPixelOffset] = (short)s;
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void ushortLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dnumBands = dst.getNumBands();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                int posylow = posy - srcScanlineStride;
                int posyhigh = posy + srcScanlineStride;
                int posyhigh2 = posyhigh + srcScanlineStride;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int posxlow = posx - srcPixelStride;
                    int posxhigh = posx + srcPixelStride;
                    int posxhigh2 = posxhigh + srcPixelStride;
                    int s__ = srcData[posxlow + posylow] & 0xFFFF;
                    int s_0 = srcData[posx + posylow] & 0xFFFF;
                    int s_1 = srcData[posxhigh + posylow] & 0xFFFF;
                    int s_2 = srcData[posxhigh2 + posylow] & 0xFFFF;
                    int s0_ = srcData[posxlow + posy] & 0xFFFF;
                    int s00 = srcData[posx + posy] & 0xFFFF;
                    int s01 = srcData[posxhigh + posy] & 0xFFFF;
                    int s02 = srcData[posxhigh2 + posy] & 0xFFFF;
                    int s1_ = srcData[posxlow + posyhigh] & 0xFFFF;
                    int s10 = srcData[posx + posyhigh] & 0xFFFF;
                    int s11 = srcData[posxhigh + posyhigh] & 0xFFFF;
                    int s12 = srcData[posxhigh2 + posyhigh] & 0xFFFF;
                    int s2_ = srcData[posxlow + posyhigh2] & 0xFFFF;
                    int s20 = srcData[posx + posyhigh2] & 0xFFFF;
                    int s21 = srcData[posxhigh + posyhigh2] & 0xFFFF;
                    int s22 = srcData[posxhigh2 + posyhigh2] & 0xFFFF;
                    int offsetX = 4 * xfrac;
                    int offsetX1 = offsetX + 1;
                    int offsetX2 = offsetX + 2;
                    int offsetX3 = offsetX + 3;
                    long sum_ = (long)this.tableDataHi[offsetX] * (long)s__;
                    sum_ += (long)this.tableDataHi[offsetX1] * (long)s_0;
                    sum_ += (long)this.tableDataHi[offsetX2] * (long)s_1;
                    sum_ += (long)this.tableDataHi[offsetX3] * (long)s_2;
                    long sum0 = (long)this.tableDataHi[offsetX] * (long)s0_;
                    sum0 += (long)this.tableDataHi[offsetX1] * (long)s00;
                    sum0 += (long)this.tableDataHi[offsetX2] * (long)s01;
                    sum0 += (long)this.tableDataHi[offsetX3] * (long)s02;
                    long sum1 = (long)this.tableDataHi[offsetX] * (long)s1_;
                    sum1 += (long)this.tableDataHi[offsetX1] * (long)s10;
                    sum1 += (long)this.tableDataHi[offsetX2] * (long)s11;
                    sum1 += (long)this.tableDataHi[offsetX3] * (long)s12;
                    long sum2 = (long)this.tableDataHi[offsetX] * (long)s2_;
                    sum2 += (long)this.tableDataHi[offsetX1] * (long)s20;
                    sum2 += (long)this.tableDataHi[offsetX2] * (long)s21;
                    sum2 += (long)this.tableDataHi[offsetX3] * (long)s22;
                    sum_ = sum_ + (long)this.round >> this.precisionBits;
                    sum0 = sum0 + (long)this.round >> this.precisionBits;
                    sum1 = sum1 + (long)this.round >> this.precisionBits;
                    sum2 = sum2 + (long)this.round >> this.precisionBits;
                    int offsetY = 4 * yfrac;
                    long sum = (long)this.tableDataVi[offsetY] * sum_;
                    sum += (long)this.tableDataVi[offsetY + 1] * sum0;
                    sum += (long)this.tableDataVi[offsetY + 2] * sum1;
                    int s = (int)((sum += (long)this.tableDataVi[offsetY + 3] * sum2) + (long)this.round >> this.precisionBits);
                    if (s > 65536) {
                        s = 65536;
                    } else if (s < 0) {
                        s = 0;
                    }
                    dstData[dstPixelOffset] = (short)(s & 0xFFFF);
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void intLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dnumBands = dst.getNumBands();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[][] srcDataArrays = src.getIntDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                long yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                int posylow = posy - srcScanlineStride;
                int posyhigh = posy + srcScanlineStride;
                int posyhigh2 = posyhigh + srcScanlineStride;
                for (int i = 0; i < dwidth; ++i) {
                    int s;
                    long xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int posxlow = posx - srcPixelStride;
                    int posxhigh = posx + srcPixelStride;
                    int posxhigh2 = posxhigh + srcPixelStride;
                    int s__ = srcData[posxlow + posylow];
                    int s_0 = srcData[posx + posylow];
                    int s_1 = srcData[posxhigh + posylow];
                    int s_2 = srcData[posxhigh2 + posylow];
                    int s0_ = srcData[posxlow + posy];
                    int s00 = srcData[posx + posy];
                    int s01 = srcData[posxhigh + posy];
                    int s02 = srcData[posxhigh2 + posy];
                    int s1_ = srcData[posxlow + posyhigh];
                    int s10 = srcData[posx + posyhigh];
                    int s11 = srcData[posxhigh + posyhigh];
                    int s12 = srcData[posxhigh2 + posyhigh];
                    int s2_ = srcData[posxlow + posyhigh2];
                    int s20 = srcData[posx + posyhigh2];
                    int s21 = srcData[posxhigh + posyhigh2];
                    int s22 = srcData[posxhigh2 + posyhigh2];
                    int offsetX = (int)(4L * xfrac);
                    int offsetX1 = offsetX + 1;
                    int offsetX2 = offsetX + 2;
                    int offsetX3 = offsetX + 3;
                    long sum_ = (long)this.tableDataHi[offsetX] * (long)s__;
                    sum_ += (long)this.tableDataHi[offsetX1] * (long)s_0;
                    sum_ += (long)this.tableDataHi[offsetX2] * (long)s_1;
                    sum_ += (long)this.tableDataHi[offsetX3] * (long)s_2;
                    long sum0 = (long)this.tableDataHi[offsetX] * (long)s0_;
                    sum0 += (long)this.tableDataHi[offsetX1] * (long)s00;
                    sum0 += (long)this.tableDataHi[offsetX2] * (long)s01;
                    sum0 += (long)this.tableDataHi[offsetX3] * (long)s02;
                    long sum1 = (long)this.tableDataHi[offsetX] * (long)s1_;
                    sum1 += (long)this.tableDataHi[offsetX1] * (long)s10;
                    sum1 += (long)this.tableDataHi[offsetX2] * (long)s11;
                    sum1 += (long)this.tableDataHi[offsetX3] * (long)s12;
                    long sum2 = (long)this.tableDataHi[offsetX] * (long)s2_;
                    sum2 += (long)this.tableDataHi[offsetX1] * (long)s20;
                    sum2 += (long)this.tableDataHi[offsetX2] * (long)s21;
                    sum2 += (long)this.tableDataHi[offsetX3] * (long)s22;
                    sum_ = sum_ + (long)this.round >> this.precisionBits;
                    sum0 = sum0 + (long)this.round >> this.precisionBits;
                    sum1 = sum1 + (long)this.round >> this.precisionBits;
                    sum2 = sum2 + (long)this.round >> this.precisionBits;
                    int offsetY = (int)(4L * yfrac);
                    long sum = (long)this.tableDataVi[offsetY] * sum_;
                    sum += (long)this.tableDataVi[offsetY + 1] * sum0;
                    sum += (long)this.tableDataVi[offsetY + 2] * sum1;
                    dstData[dstPixelOffset] = s = (int)((sum += (long)this.tableDataVi[offsetY + 3] * sum2) + (long)this.round >> this.precisionBits);
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void floatLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dnumBands = dst.getNumBands();
        float[][] dstDataArrays = dst.getFloatDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        float[][] srcDataArrays = src.getFloatDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            float[] dstData = dstDataArrays[k];
            float[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                int posylow = posy - srcScanlineStride;
                int posyhigh = posy + srcScanlineStride;
                int posyhigh2 = posyhigh + srcScanlineStride;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int posxlow = posx - srcPixelStride;
                    int posxhigh = posx + srcPixelStride;
                    int posxhigh2 = posxhigh + srcPixelStride;
                    float s__ = srcData[posxlow + posylow];
                    float s_0 = srcData[posx + posylow];
                    float s_1 = srcData[posxhigh + posylow];
                    float s_2 = srcData[posxhigh2 + posylow];
                    float s0_ = srcData[posxlow + posy];
                    float s00 = srcData[posx + posy];
                    float s01 = srcData[posxhigh + posy];
                    float s02 = srcData[posxhigh2 + posy];
                    float s1_ = srcData[posxlow + posyhigh];
                    float s10 = srcData[posx + posyhigh];
                    float s11 = srcData[posxhigh + posyhigh];
                    float s12 = srcData[posxhigh2 + posyhigh];
                    float s2_ = srcData[posxlow + posyhigh2];
                    float s20 = srcData[posx + posyhigh2];
                    float s21 = srcData[posxhigh + posyhigh2];
                    float s22 = srcData[posxhigh2 + posyhigh2];
                    int offsetX = 4 * xfrac;
                    int offsetX1 = offsetX + 1;
                    int offsetX2 = offsetX + 2;
                    int offsetX3 = offsetX + 3;
                    double sum_ = this.tableDataHf[offsetX] * s__;
                    sum_ += (double)(this.tableDataHf[offsetX1] * s_0);
                    sum_ += (double)(this.tableDataHf[offsetX2] * s_1);
                    sum_ += (double)(this.tableDataHf[offsetX3] * s_2);
                    double sum0 = this.tableDataHf[offsetX] * s0_;
                    sum0 += (double)(this.tableDataHf[offsetX1] * s00);
                    sum0 += (double)(this.tableDataHf[offsetX2] * s01);
                    sum0 += (double)(this.tableDataHf[offsetX3] * s02);
                    double sum1 = this.tableDataHf[offsetX] * s1_;
                    sum1 += (double)(this.tableDataHf[offsetX1] * s10);
                    sum1 += (double)(this.tableDataHf[offsetX2] * s11);
                    sum1 += (double)(this.tableDataHf[offsetX3] * s12);
                    double sum2 = this.tableDataHf[offsetX] * s2_;
                    sum2 += (double)(this.tableDataHf[offsetX1] * s20);
                    sum2 += (double)(this.tableDataHf[offsetX2] * s21);
                    sum2 += (double)(this.tableDataHf[offsetX3] * s22);
                    int offsetY = 4 * yfrac;
                    double sum = (double)this.tableDataVf[offsetY] * sum_;
                    sum += (double)this.tableDataVf[offsetY + 1] * sum0;
                    sum += (double)this.tableDataVf[offsetY + 2] * sum1;
                    if ((sum += (double)this.tableDataVf[offsetY + 3] * sum2) > 3.4028234663852886E38) {
                        sum = 3.4028234663852886E38;
                    } else if (sum < -3.4028234663852886E38) {
                        sum = -3.4028234663852886E38;
                    }
                    dstData[dstPixelOffset] = (float)sum;
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void doubleLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dnumBands = dst.getNumBands();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        int[] bandOffsets = src.getBandOffsets();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                int posylow = posy - srcScanlineStride;
                int posyhigh = posy + srcScanlineStride;
                int posyhigh2 = posyhigh + srcScanlineStride;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int posxlow = posx - srcPixelStride;
                    int posxhigh = posx + srcPixelStride;
                    int posxhigh2 = posxhigh + srcPixelStride;
                    double s__ = srcData[posxlow + posylow];
                    double s_0 = srcData[posx + posylow];
                    double s_1 = srcData[posxhigh + posylow];
                    double s_2 = srcData[posxhigh2 + posylow];
                    double s0_ = srcData[posxlow + posy];
                    double s00 = srcData[posx + posy];
                    double s01 = srcData[posxhigh + posy];
                    double s02 = srcData[posxhigh2 + posy];
                    double s1_ = srcData[posxlow + posyhigh];
                    double s10 = srcData[posx + posyhigh];
                    double s11 = srcData[posxhigh + posyhigh];
                    double s12 = srcData[posxhigh2 + posyhigh];
                    double s2_ = srcData[posxlow + posyhigh2];
                    double s20 = srcData[posx + posyhigh2];
                    double s21 = srcData[posxhigh + posyhigh2];
                    double s22 = srcData[posxhigh2 + posyhigh2];
                    int offsetX = 4 * xfrac;
                    int offsetX1 = offsetX + 1;
                    int offsetX2 = offsetX + 2;
                    int offsetX3 = offsetX + 3;
                    double sum_ = this.tableDataHd[offsetX] * s__;
                    sum_ += this.tableDataHd[offsetX1] * s_0;
                    sum_ += this.tableDataHd[offsetX2] * s_1;
                    sum_ += this.tableDataHd[offsetX3] * s_2;
                    double sum0 = this.tableDataHd[offsetX] * s0_;
                    sum0 += this.tableDataHd[offsetX1] * s00;
                    sum0 += this.tableDataHd[offsetX2] * s01;
                    sum0 += this.tableDataHd[offsetX3] * s02;
                    double sum1 = this.tableDataHd[offsetX] * s1_;
                    sum1 += this.tableDataHd[offsetX1] * s10;
                    sum1 += this.tableDataHd[offsetX2] * s11;
                    sum1 += this.tableDataHd[offsetX3] * s12;
                    double sum2 = this.tableDataHd[offsetX] * s2_;
                    sum2 += this.tableDataHd[offsetX1] * s20;
                    sum2 += this.tableDataHd[offsetX2] * s21;
                    int offsetY = 4 * yfrac;
                    double s = this.tableDataVd[offsetY] * sum_;
                    s += this.tableDataVd[offsetY + 1] * sum0;
                    s += this.tableDataVd[offsetY + 2] * sum1;
                    dstData[dstPixelOffset] = s += this.tableDataVd[offsetY + 3] * (sum2 += this.tableDataHd[offsetX3] * s22);
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private synchronized void initTableDataI() {
        if (this.tableDataHi == null || this.tableDataVi == null) {
            this.tableDataHi = this.interpTable.getHorizontalTableData();
            this.tableDataVi = this.interpTable.getVerticalTableData();
        }
    }

    private synchronized void initTableDataF() {
        if (this.tableDataHf == null || this.tableDataVf == null) {
            this.tableDataHf = this.interpTable.getHorizontalTableDataFloat();
            this.tableDataVf = this.interpTable.getVerticalTableDataFloat();
        }
    }

    private synchronized void initTableDataD() {
        if (this.tableDataHd == null || this.tableDataVd == null) {
            this.tableDataHd = this.interpTable.getHorizontalTableDataDouble();
            this.tableDataVd = this.interpTable.getVerticalTableDataDouble();
        }
    }
}

