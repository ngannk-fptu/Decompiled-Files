/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MedianFilterOpImage;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.operator.MedianFilterDescriptor;

final class MedianFilterPlusOpImage
extends MedianFilterOpImage {
    public MedianFilterPlusOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize) {
        super(source, extender, config, layout, MedianFilterDescriptor.MEDIAN_MASK_PLUS, maskSize);
    }

    protected void byteLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        int[] values = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int valueCount = 0;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; ++u) {
                        values[valueCount++] = srcData[imageOffset] & 0xFF;
                        imageOffset += srcScanlineStride;
                    }
                    values[offset] = values[--valueCount];
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        values[valueCount++] = srcData[imageOffset] & 0xFF;
                        imageOffset += srcPixelStride;
                    }
                    int val = this.medianFilter(values);
                    dstData[dstPixelOffset] = (byte)val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void shortLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
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
        int srcScanlineStride = src.getScanlineStride();
        int[] values = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int valueCount = 0;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; ++u) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }
                    values[offset] = values[--valueCount];
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }
                    int val = this.medianFilter(values);
                    dstData[dstPixelOffset] = (short)val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void ushortLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
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
        int srcScanlineStride = src.getScanlineStride();
        int[] values = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int valueCount = 0;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; ++u) {
                        values[valueCount++] = srcData[imageOffset] & 0xFFFF;
                        imageOffset += srcScanlineStride;
                    }
                    values[offset] = values[--valueCount];
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        values[valueCount++] = srcData[imageOffset] & 0xFFFF;
                        imageOffset += srcPixelStride;
                    }
                    int val = this.medianFilter(values);
                    dstData[dstPixelOffset] = (short)val;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void intLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
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
        int srcScanlineStride = src.getScanlineStride();
        int[] values = new int[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int val;
                    int valueCount = 0;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; ++u) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }
                    values[offset] = values[--valueCount];
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }
                    dstData[dstPixelOffset] = val = this.medianFilter(values);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void floatLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[][] dstDataArrays = dst.getFloatDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        float[][] srcDataArrays = src.getFloatDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        float[] values = new float[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;
        for (int k = 0; k < dnumBands; ++k) {
            float[] dstData = dstDataArrays[k];
            float[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float val;
                    int valueCount = 0;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; ++u) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }
                    values[offset] = values[--valueCount];
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }
                    dstData[dstPixelOffset] = val = this.medianFilterFloat(values);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void doubleLoop(RasterAccessor src, RasterAccessor dst, int filterSize) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        double[] values = new double[filterSize * 2 - 1];
        int wp = filterSize;
        int offset = filterSize / 2;
        for (int k = 0; k < dnumBands; ++k) {
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    double val;
                    int valueCount = 0;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    for (int u = 0; u < wp; ++u) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                    }
                    values[offset] = values[--valueCount];
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        values[valueCount++] = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                    }
                    dstData[dstPixelOffset] = val = this.medianFilterDouble(values);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

