/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.Rational;
import java.awt.Rectangle;
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
import javax.media.jai.ScaleOpImage;

final class ScaleNearestOpImage
extends ScaleOpImage {
    long invScaleXInt;
    long invScaleXFrac;
    long invScaleYInt;
    long invScaleYFrac;

    public ScaleNearestOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
        ColorModel srcColorModel = source.getColorModel();
        if (srcColorModel instanceof IndexColorModel) {
            this.sampleModel = source.getSampleModel().createCompatibleSampleModel(this.tileWidth, this.tileHeight);
            this.colorModel = srcColorModel;
        }
        if (this.invScaleXRational.num > this.invScaleXRational.denom) {
            this.invScaleXInt = this.invScaleXRational.num / this.invScaleXRational.denom;
            this.invScaleXFrac = this.invScaleXRational.num % this.invScaleXRational.denom;
        } else {
            this.invScaleXInt = 0L;
            this.invScaleXFrac = this.invScaleXRational.num;
        }
        if (this.invScaleYRational.num > this.invScaleYRational.denom) {
            this.invScaleYInt = this.invScaleYRational.num / this.invScaleYRational.denom;
            this.invScaleYFrac = this.invScaleYRational.num % this.invScaleYRational.denom;
        } else {
            this.invScaleYInt = 0L;
            this.invScaleYFrac = this.invScaleYRational.num;
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int srcRectX = srcRect.x;
        int srcRectY = srcRect.y;
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int srcPixelStride = srcAccessor.getPixelStride();
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int[] xvalues = new int[dwidth];
        long sxNum = dx;
        long sxDenom = 1L;
        sxNum = sxNum * this.transXRationalDenom - this.transXRationalNum * sxDenom;
        sxNum = 2L * sxNum + (sxDenom *= this.transXRationalDenom);
        sxDenom *= 2L;
        int srcXInt = Rational.floor(sxNum *= this.invScaleXRationalNum, sxDenom *= this.invScaleXRationalDenom);
        long srcXFrac = sxNum % sxDenom;
        if (srcXInt < 0) {
            srcXFrac = sxDenom + srcXFrac;
        }
        long commonXDenom = sxDenom * this.invScaleXRationalDenom;
        srcXFrac *= this.invScaleXRationalDenom;
        long newInvScaleXFrac = this.invScaleXFrac * sxDenom;
        for (int i = 0; i < dwidth; ++i) {
            xvalues[i] = (srcXInt - srcRectX) * srcPixelStride;
            srcXInt = (int)((long)srcXInt + this.invScaleXInt);
            if ((srcXFrac += newInvScaleXFrac) < commonXDenom) continue;
            ++srcXInt;
            srcXFrac -= commonXDenom;
        }
        int[] yvalues = new int[dheight];
        long syNum = dy;
        long syDenom = 1L;
        syNum = syNum * this.transYRationalDenom - this.transYRationalNum * syDenom;
        syNum = 2L * syNum + (syDenom *= this.transYRationalDenom);
        syDenom *= 2L;
        int srcYInt = Rational.floor(syNum *= this.invScaleYRationalNum, syDenom *= this.invScaleYRationalDenom);
        long srcYFrac = syNum % syDenom;
        if (srcYInt < 0) {
            srcYFrac = syDenom + srcYFrac;
        }
        long commonYDenom = syDenom * this.invScaleYRationalDenom;
        srcYFrac *= this.invScaleYRationalDenom;
        long newInvScaleYFrac = this.invScaleYFrac * syDenom;
        for (int i = 0; i < dheight; ++i) {
            yvalues[i] = (srcYInt - srcRectY) * srcScanlineStride;
            srcYInt = (int)((long)srcYInt + this.invScaleYInt);
            if ((srcYFrac += newInvScaleYFrac) < commonYDenom) continue;
            ++srcYInt;
            srcYFrac -= commonYDenom;
        }
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.byteLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
                break;
            }
            case 4: {
                this.floatLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
                break;
            }
            case 5: {
                this.doubleLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
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

    private void byteLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues) {
        int dwidth = dstRect.width;
        int dheight = dstRect.height;
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int dnumBands = dst.getNumBands();
        int[] bandOffsets = src.getBandOffsets();
        byte[][] srcDataArrays = src.getByteDataArrays();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int bandOffset = bandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int posy = yvalues[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int posx = xvalues[i];
                    int pos = posx + posy;
                    dstData[dstPixelOffset] = srcData[pos];
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void shortLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues) {
        int dwidth = dstRect.width;
        int dheight = dstRect.height;
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int dnumBands = dst.getNumBands();
        int[] bandOffsets = src.getBandOffsets();
        short[][] srcDataArrays = src.getShortDataArrays();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int bandOffset = bandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int posy = yvalues[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int posx = xvalues[i];
                    int pos = posx + posy;
                    dstData[dstPixelOffset] = srcData[pos];
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void intLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues) {
        int dwidth = dstRect.width;
        int dheight = dstRect.height;
        int dnumBands = dst.getNumBands();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[] bandOffsets = src.getBandOffsets();
        int[][] srcDataArrays = src.getIntDataArrays();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int bandOffset = bandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int posy = yvalues[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int posx = xvalues[i];
                    int pos = posx + posy;
                    dstData[dstPixelOffset] = srcData[pos];
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void floatLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues) {
        int dwidth = dstRect.width;
        int dheight = dstRect.height;
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
            int bandOffset = bandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int posy = yvalues[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int posx = xvalues[i];
                    int pos = posx + posy;
                    dstData[dstPixelOffset] = srcData[pos];
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void doubleLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues) {
        int dwidth = dstRect.width;
        int dheight = dstRect.height;
        int dnumBands = dst.getNumBands();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[] bandOffsets = src.getBandOffsets();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        boolean dstOffset = false;
        for (int k = 0; k < dnumBands; ++k) {
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int bandOffset = bandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int dstPixelOffset = dstScanlineOffset;
                int posy = yvalues[j] + bandOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int posx = xvalues[i];
                    int pos = posx + posy;
                    dstData[dstPixelOffset] = srcData[pos];
                    dstPixelOffset += dstPixelStride;
                }
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

