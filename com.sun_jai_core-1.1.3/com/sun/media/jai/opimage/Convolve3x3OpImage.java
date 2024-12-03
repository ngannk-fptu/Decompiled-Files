/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class Convolve3x3OpImage
extends AreaOpImage {
    protected KernelJAI kernel;
    float[][] tables = new float[9][256];

    public Convolve3x3OpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel) {
        super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
        this.kernel = kernel;
        if (kernel.getWidth() != 3 || kernel.getHeight() != 3 || kernel.getXOrigin() != 1 || kernel.getYOrigin() != 1) {
            throw new RuntimeException(JaiI18N.getString("Convolve3x3OpImage0"));
        }
        if (this.sampleModel.getDataType() == 0) {
            float[] kdata = kernel.getKernelData();
            float k0 = kdata[0];
            float k1 = kdata[1];
            float k2 = kdata[2];
            float k3 = kdata[3];
            float k4 = kdata[4];
            float k5 = kdata[5];
            float k6 = kdata[6];
            float k7 = kdata[7];
            float k8 = kdata[8];
            for (int j = 0; j < 256; ++j) {
                byte b = (byte)j;
                float f = j;
                this.tables[0][b + 128] = k0 * f + 0.5f;
                this.tables[1][b + 128] = k1 * f;
                this.tables[2][b + 128] = k2 * f;
                this.tables[3][b + 128] = k3 * f;
                this.tables[4][b + 128] = k4 * f;
                this.tables[5][b + 128] = k5 * f;
                this.tables[6][b + 128] = k6 * f;
                this.tables[7][b + 128] = k7 * f;
                this.tables[8][b + 128] = k8 * f;
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.byteLoop(srcAccessor, dstAccessor);
                break;
            }
            case 2: {
                this.shortLoop(srcAccessor, dstAccessor);
                break;
            }
            case 3: {
                this.intLoop(srcAccessor, dstAccessor);
                break;
            }
            default: {
                String className = this.getClass().getName();
                throw new RuntimeException(JaiI18N.getString("Convolve3x3OpImage1"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    private void byteLoop(RasterAccessor src, RasterAccessor dst) {
        int srcScanlineStride;
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] t0 = this.tables[0];
        float[] t1 = this.tables[1];
        float[] t2 = this.tables[2];
        float[] t3 = this.tables[3];
        float[] t4 = this.tables[4];
        float[] t5 = this.tables[5];
        float[] t6 = this.tables[6];
        float[] t7 = this.tables[7];
        float[] t8 = this.tables[8];
        float[] kdata = this.kernel.getKernelData();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int centerScanlineOffset = srcScanlineStride = src.getScanlineStride();
        int bottomScanlineOffset = srcScanlineStride * 2;
        int middlePixelOffset = dnumBands;
        int rightPixelOffset = dnumBands * 2;
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = t0[128 + srcData[srcPixelOffset]] + t1[128 + srcData[srcPixelOffset + middlePixelOffset]] + t2[128 + srcData[srcPixelOffset + rightPixelOffset]] + t3[128 + srcData[srcPixelOffset + centerScanlineOffset]] + t4[128 + srcData[srcPixelOffset + centerScanlineOffset + middlePixelOffset]] + t5[128 + srcData[srcPixelOffset + centerScanlineOffset + rightPixelOffset]] + t6[128 + srcData[srcPixelOffset + bottomScanlineOffset]] + t7[128 + srcData[srcPixelOffset + bottomScanlineOffset + middlePixelOffset]] + t8[128 + srcData[srcPixelOffset + bottomScanlineOffset + rightPixelOffset]];
                    int val = (int)f;
                    if (val < 0) {
                        val = 0;
                    } else if (val > 255) {
                        val = 255;
                    }
                    dstData[dstPixelOffset] = (byte)val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void shortLoop(RasterAccessor src, RasterAccessor dst) {
        int srcScanlineStride;
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int centerScanlineOffset = srcScanlineStride = src.getScanlineStride();
        int bottomScanlineOffset = srcScanlineStride * 2;
        int middlePixelOffset = dnumBands;
        int rightPixelOffset = dnumBands * 2;
        float[] kdata = this.kernel.getKernelData();
        float k0 = kdata[0];
        float k1 = kdata[1];
        float k2 = kdata[2];
        float k3 = kdata[3];
        float k4 = kdata[4];
        float k5 = kdata[5];
        float k6 = kdata[6];
        float k7 = kdata[7];
        float k8 = kdata[8];
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = k0 * (float)srcData[srcPixelOffset] + k1 * (float)srcData[srcPixelOffset + middlePixelOffset] + k2 * (float)srcData[srcPixelOffset + rightPixelOffset] + k3 * (float)srcData[srcPixelOffset + centerScanlineOffset] + k4 * (float)srcData[srcPixelOffset + centerScanlineOffset + middlePixelOffset] + k5 * (float)srcData[srcPixelOffset + centerScanlineOffset + rightPixelOffset] + k6 * (float)srcData[srcPixelOffset + bottomScanlineOffset] + k7 * (float)srcData[srcPixelOffset + bottomScanlineOffset + middlePixelOffset] + k8 * (float)srcData[srcPixelOffset + bottomScanlineOffset + rightPixelOffset];
                    int val = (int)f;
                    if (val < Short.MIN_VALUE) {
                        val = Short.MIN_VALUE;
                    } else if (val > Short.MAX_VALUE) {
                        val = Short.MAX_VALUE;
                    }
                    dstData[dstPixelOffset] = (short)val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void intLoop(RasterAccessor src, RasterAccessor dst) {
        int srcScanlineStride;
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[][] srcDataArrays = src.getIntDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int centerScanlineOffset = srcScanlineStride = src.getScanlineStride();
        int bottomScanlineOffset = srcScanlineStride * 2;
        int middlePixelOffset = dnumBands;
        int rightPixelOffset = dnumBands * 2;
        float[] kdata = this.kernel.getKernelData();
        float k0 = kdata[0];
        float k1 = kdata[1];
        float k2 = kdata[2];
        float k3 = kdata[3];
        float k4 = kdata[4];
        float k5 = kdata[5];
        float k6 = kdata[6];
        float k7 = kdata[7];
        float k8 = kdata[8];
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = k0 * (float)srcData[srcPixelOffset] + k1 * (float)srcData[srcPixelOffset + middlePixelOffset] + k2 * (float)srcData[srcPixelOffset + rightPixelOffset] + k3 * (float)srcData[srcPixelOffset + centerScanlineOffset] + k4 * (float)srcData[srcPixelOffset + centerScanlineOffset + middlePixelOffset] + k5 * (float)srcData[srcPixelOffset + centerScanlineOffset + rightPixelOffset] + k6 * (float)srcData[srcPixelOffset + bottomScanlineOffset] + k7 * (float)srcData[srcPixelOffset + bottomScanlineOffset + middlePixelOffset] + k8 * (float)srcData[srcPixelOffset + bottomScanlineOffset + rightPixelOffset];
                    dstData[dstPixelOffset] = (int)f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

