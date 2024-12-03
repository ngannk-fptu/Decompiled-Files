/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class AbsoluteOpImage
extends PointOpImage {
    public AbsoluteOpImage(RenderedImage source, Map config, ImageLayout layout) {
        super(source, layout, config, true);
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor src = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        if (dst.isBinary()) {
            byte[] dstBits = dst.getBinaryDataArray();
            System.arraycopy(src.getBinaryDataArray(), 0, dstBits, 0, dstBits.length);
            dst.copyBinaryDataToRaster();
            return;
        }
        switch (dst.getDataType()) {
            case 0: {
                this.byteAbsolute(dst.getNumBands(), dst.getWidth(), dst.getHeight(), src.getScanlineStride(), src.getPixelStride(), src.getBandOffsets(), src.getByteDataArrays(), dst.getScanlineStride(), dst.getPixelStride(), dst.getBandOffsets(), dst.getByteDataArrays());
                break;
            }
            case 2: {
                this.shortAbsolute(dst.getNumBands(), dst.getWidth(), dst.getHeight(), src.getScanlineStride(), src.getPixelStride(), src.getBandOffsets(), src.getShortDataArrays(), dst.getScanlineStride(), dst.getPixelStride(), dst.getBandOffsets(), dst.getShortDataArrays());
                break;
            }
            case 1: {
                this.ushortAbsolute(dst.getNumBands(), dst.getWidth(), dst.getHeight(), src.getScanlineStride(), src.getPixelStride(), src.getBandOffsets(), src.getShortDataArrays(), dst.getScanlineStride(), dst.getPixelStride(), dst.getBandOffsets(), dst.getShortDataArrays());
                break;
            }
            case 3: {
                this.intAbsolute(dst.getNumBands(), dst.getWidth(), dst.getHeight(), src.getScanlineStride(), src.getPixelStride(), src.getBandOffsets(), src.getIntDataArrays(), dst.getScanlineStride(), dst.getPixelStride(), dst.getBandOffsets(), dst.getIntDataArrays());
                break;
            }
            case 4: {
                this.floatAbsolute(dst.getNumBands(), dst.getWidth(), dst.getHeight(), src.getScanlineStride(), src.getPixelStride(), src.getBandOffsets(), src.getFloatDataArrays(), dst.getScanlineStride(), dst.getPixelStride(), dst.getBandOffsets(), dst.getFloatDataArrays());
                break;
            }
            case 5: {
                this.doubleAbsolute(dst.getNumBands(), dst.getWidth(), dst.getHeight(), src.getScanlineStride(), src.getPixelStride(), src.getBandOffsets(), src.getDoubleDataArrays(), dst.getScanlineStride(), dst.getPixelStride(), dst.getBandOffsets(), dst.getDoubleDataArrays());
            }
        }
        if (dst.needsClamping()) {
            dst.clampDataArrays();
        }
        dst.copyDataToRaster();
    }

    private void byteAbsolute(int numBands, int dstWidth, int dstHeight, int srcScanlineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int dstScanlineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData) {
        for (int band = 0; band < numBands; ++band) {
            byte[] src = srcData[band];
            byte[] dst = dstData[band];
            int srcLineOffset = srcBandOffsets[band];
            int dstLineOffset = dstBandOffsets[band];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                for (int w = 0; w < dstWidth; ++w) {
                    dst[dstPixelOffset] = src[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcLineOffset += srcScanlineStride;
                dstLineOffset += dstScanlineStride;
            }
        }
    }

    private void shortAbsolute(int numBands, int dstWidth, int dstHeight, int srcScanlineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int dstScanlineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {
        for (int band = 0; band < numBands; ++band) {
            short[] src = srcData[band];
            short[] dst = dstData[band];
            int srcLineOffset = srcBandOffsets[band];
            int dstLineOffset = dstBandOffsets[band];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                for (int w = 0; w < dstWidth; ++w) {
                    short pixelValue = src[srcPixelOffset];
                    dst[dstPixelOffset] = pixelValue != Short.MIN_VALUE && (pixelValue & Short.MIN_VALUE) != 0 ? (short)(-src[srcPixelOffset]) : src[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcLineOffset += srcScanlineStride;
                dstLineOffset += dstScanlineStride;
            }
        }
    }

    private void ushortAbsolute(int numBands, int dstWidth, int dstHeight, int srcScanlineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int dstScanlineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {
        for (int band = 0; band < numBands; ++band) {
            short[] src = srcData[band];
            short[] dst = dstData[band];
            int srcLineOffset = srcBandOffsets[band];
            int dstLineOffset = dstBandOffsets[band];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                for (int w = 0; w < dstWidth; ++w) {
                    dst[dstPixelOffset] = src[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcLineOffset += srcScanlineStride;
                dstLineOffset += dstScanlineStride;
            }
        }
    }

    private void intAbsolute(int numBands, int dstWidth, int dstHeight, int srcScanlineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int dstScanlineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData) {
        for (int band = 0; band < numBands; ++band) {
            int[] src = srcData[band];
            int[] dst = dstData[band];
            int srcLineOffset = srcBandOffsets[band];
            int dstLineOffset = dstBandOffsets[band];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                for (int w = 0; w < dstWidth; ++w) {
                    int pixelValue = src[srcPixelOffset];
                    dst[dstPixelOffset] = pixelValue != Integer.MIN_VALUE && (pixelValue & Integer.MIN_VALUE) != 0 ? -src[srcPixelOffset] : src[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcLineOffset += srcScanlineStride;
                dstLineOffset += dstScanlineStride;
            }
        }
    }

    private void floatAbsolute(int numBands, int dstWidth, int dstHeight, int srcScanlineStride, int srcPixelStride, int[] srcBandOffsets, float[][] srcData, int dstScanlineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData) {
        for (int band = 0; band < numBands; ++band) {
            float[] src = srcData[band];
            float[] dst = dstData[band];
            int srcLineOffset = srcBandOffsets[band];
            int dstLineOffset = dstBandOffsets[band];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                for (int w = 0; w < dstWidth; ++w) {
                    dst[dstPixelOffset] = src[srcPixelOffset] <= 0.0f ? 0.0f - src[srcPixelOffset] : src[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcLineOffset += srcScanlineStride;
                dstLineOffset += dstScanlineStride;
            }
        }
    }

    private void doubleAbsolute(int numBands, int dstWidth, int dstHeight, int srcScanlineStride, int srcPixelStride, int[] srcBandOffsets, double[][] srcData, int dstScanlineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData) {
        for (int band = 0; band < numBands; ++band) {
            double[] src = srcData[band];
            double[] dst = dstData[band];
            int srcLineOffset = srcBandOffsets[band];
            int dstLineOffset = dstBandOffsets[band];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                for (int w = 0; w < dstWidth; ++w) {
                    dst[dstPixelOffset] = src[srcPixelOffset] <= 0.0 ? 0.0 - src[srcPixelOffset] : src[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcLineOffset += srcScanlineStride;
                dstLineOffset += dstScanlineStride;
            }
        }
    }
}

