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
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.ScaleOpImage;

final class ScaleGeneralOpImage
extends ScaleOpImage {
    private int subsampleBits;
    private int one;
    Rational half = new Rational(1L, 2L);
    private int interp_width;
    private int interp_height;
    private int interp_left;
    private int interp_top;
    long invScaleYInt;
    long invScaleYFrac;
    long invScaleXInt;
    long invScaleXFrac;

    public ScaleGeneralOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
        this.subsampleBits = interp.getSubsampleBitsH();
        this.one = 1 << this.subsampleBits;
        this.interp_width = interp.getWidth();
        this.interp_height = interp.getHeight();
        this.interp_left = interp.getLeftPadding();
        this.interp_top = interp.getTopPadding();
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
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int[] ypos = new int[dheight];
        int[] xpos = new int[dwidth];
        int[] xfracvalues = null;
        int[] yfracvalues = null;
        float[] xfracvaluesFloat = null;
        float[] yfracvaluesFloat = null;
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                yfracvalues = new int[dheight];
                xfracvalues = new int[dwidth];
                this.preComputePositionsInt(destRect, srcRect.x, srcRect.y, srcPixelStride, srcScanlineStride, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 4: 
            case 5: {
                yfracvaluesFloat = new float[dheight];
                xfracvaluesFloat = new float[dwidth];
                this.preComputePositionsFloat(destRect, srcRect.x, srcRect.y, srcPixelStride, srcScanlineStride, xpos, ypos, xfracvaluesFloat, yfracvaluesFloat);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
            }
        }
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.byteLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 2: {
                this.shortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 1: {
                this.ushortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
                break;
            }
            case 4: {
                this.floatLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvaluesFloat, yfracvaluesFloat);
                break;
            }
            case 5: {
                this.doubleLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvaluesFloat, yfracvaluesFloat);
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

    private void preComputePositionsInt(Rectangle destRect, int srcRectX, int srcRectY, int srcPixelStride, int srcScanlineStride, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues) {
        int i;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dx = destRect.x;
        int dy = destRect.y;
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
    }

    private void preComputePositionsFloat(Rectangle destRect, int srcRectX, int srcRectY, int srcPixelStride, int srcScanlineStride, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat) {
        int i;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int dx = destRect.x;
        int dy = destRect.y;
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
            xfracvaluesFloat[i] = (float)srcXFrac / (float)commonXDenom;
            srcXInt = (int)((long)srcXInt + this.invScaleXInt);
            if ((srcXFrac += newInvScaleXFrac) < commonXDenom) continue;
            ++srcXInt;
            srcXFrac -= commonXDenom;
        }
        for (i = 0; i < dheight; ++i) {
            ypos[i] = (srcYInt - srcRectY) * srcScanlineStride;
            yfracvaluesFloat[i] = (float)srcYFrac / (float)commonYDenom;
            srcYInt = (int)((long)srcYInt + this.invScaleYInt);
            if ((srcYFrac += newInvScaleYFrac) < commonYDenom) continue;
            ++srcYInt;
            srcYFrac -= commonYDenom;
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
        int[][] samples = new int[this.interp_height][this.interp_width];
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                    start = posx + posy - start;
                    int countH = 0;
                    int countV = 0;
                    for (int yloop = 0; yloop < this.interp_height; ++yloop) {
                        int startY = start;
                        for (int xloop = 0; xloop < this.interp_width; ++xloop) {
                            samples[countV][countH++] = srcData[start] & 0xFF;
                            start += srcPixelStride;
                        }
                        ++countV;
                        countH = 0;
                        start = startY + srcScanlineStride;
                    }
                    int s = this.interp.interpolate(samples, xfrac, yfrac);
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
        int[][] samples = new int[this.interp_height][this.interp_width];
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                    start = posx + posy - start;
                    int countH = 0;
                    int countV = 0;
                    for (int yloop = 0; yloop < this.interp_height; ++yloop) {
                        int startY = start;
                        for (int xloop = 0; xloop < this.interp_width; ++xloop) {
                            samples[countV][countH++] = srcData[start];
                            start += srcPixelStride;
                        }
                        ++countV;
                        countH = 0;
                        start = startY + srcScanlineStride;
                    }
                    int s = this.interp.interpolate(samples, xfrac, yfrac);
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
        int[][] samples = new int[this.interp_height][this.interp_width];
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                    start = posx + posy - start;
                    int countH = 0;
                    int countV = 0;
                    for (int yloop = 0; yloop < this.interp_height; ++yloop) {
                        int startY = start;
                        for (int xloop = 0; xloop < this.interp_width; ++xloop) {
                            samples[countV][countH++] = srcData[start] & 0xFFFF;
                            start += srcPixelStride;
                        }
                        ++countV;
                        countH = 0;
                        start = startY + srcScanlineStride;
                    }
                    int s = this.interp.interpolate(samples, xfrac, yfrac);
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
        int[][] samples = new int[this.interp_height][this.interp_width];
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int yfrac = yfracvalues[j];
                int posy = ypos[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int s;
                    int xfrac = xfracvalues[i];
                    int posx = xpos[i];
                    int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                    start = posx + posy - start;
                    int countH = 0;
                    int countV = 0;
                    for (int yloop = 0; yloop < this.interp_height; ++yloop) {
                        int startY = start;
                        for (int xloop = 0; xloop < this.interp_width; ++xloop) {
                            samples[countV][countH++] = srcData[start];
                            start += srcPixelStride;
                        }
                        ++countV;
                        countH = 0;
                        start = startY + srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = s = this.interp.interpolate(samples, xfrac, yfrac);
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void floatLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat) {
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
        float[][] samples = new float[this.interp_height][this.interp_width];
        for (int k = 0; k < dnumBands; ++k) {
            float[] dstData = dstDataArrays[k];
            float[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                float yfrac = yfracvaluesFloat[j];
                int posy = ypos[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float xfrac = xfracvaluesFloat[i];
                    int posx = xpos[i];
                    int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                    start = posx + posy - start;
                    int countH = 0;
                    int countV = 0;
                    for (int yloop = 0; yloop < this.interp_height; ++yloop) {
                        int startY = start;
                        for (int xloop = 0; xloop < this.interp_width; ++xloop) {
                            samples[countV][countH++] = srcData[start];
                            start += srcPixelStride;
                        }
                        ++countV;
                        countH = 0;
                        start = startY + srcScanlineStride;
                    }
                    float s = this.interp.interpolate(samples, xfrac, yfrac);
                    if (s > Float.MAX_VALUE) {
                        s = Float.MAX_VALUE;
                    } else if (s < -3.4028235E38f) {
                        s = -3.4028235E38f;
                    }
                    dstData[dstPixelOffset] = s;
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void doubleLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat) {
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
        double[][] samples = new double[this.interp_height][this.interp_width];
        for (int k = 0; k < dnumBands; ++k) {
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int bandOffset = bandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                float yfrac = yfracvaluesFloat[j];
                int posy = ypos[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    double s;
                    float xfrac = xfracvaluesFloat[i];
                    int posx = xpos[i];
                    int start = this.interp_left * srcPixelStride + this.interp_top * srcScanlineStride;
                    start = posx + posy - start;
                    int countH = 0;
                    int countV = 0;
                    for (int yloop = 0; yloop < this.interp_height; ++yloop) {
                        int startY = start;
                        for (int xloop = 0; xloop < this.interp_width; ++xloop) {
                            samples[countV][countH++] = srcData[start];
                            start += srcPixelStride;
                        }
                        ++countV;
                        countH = 0;
                        start = startY + srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = s = this.interp.interpolate(samples, xfrac, yfrac);
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

