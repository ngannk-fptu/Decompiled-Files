/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MinFilterOpImage;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.operator.MinFilterDescriptor;

final class MinFilterPlusOpImage
extends MinFilterOpImage {
    public MinFilterPlusOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize) {
        super(source, extender, config, layout, MinFilterDescriptor.MIN_MASK_PLUS, maskSize);
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
                    int val;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    int minval = Integer.MAX_VALUE;
                    for (int u = 0; u < wp; ++u) {
                        val = srcData[imageOffset] & 0xFF;
                        imageOffset += srcScanlineStride;
                        minval = val < minval ? val : minval;
                    }
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        val = srcData[imageOffset] & 0xFF;
                        imageOffset += srcPixelStride;
                        minval = val < minval ? val : minval;
                    }
                    dstData[dstPixelOffset] = (byte)minval;
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
                    short val;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    short s = Integer.MAX_VALUE;
                    for (int u = 0; u < wp; ++u) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        s = val < s ? val : s;
                    }
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        s = val < s ? val : s;
                    }
                    dstData[dstPixelOffset] = s;
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
                    int val;
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    int minval = Integer.MAX_VALUE;
                    for (int u = 0; u < wp; ++u) {
                        val = srcData[imageOffset] & 0xFFFF;
                        imageOffset += srcScanlineStride;
                        minval = val < minval ? val : minval;
                    }
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        val = srcData[imageOffset] & 0xFFFF;
                        imageOffset += srcPixelStride;
                        minval = val < minval ? val : minval;
                    }
                    dstData[dstPixelOffset] = (short)minval;
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
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    int minval = Integer.MAX_VALUE;
                    for (int u = 0; u < wp; ++u) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        minval = val < minval ? val : minval;
                    }
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = val < minval ? val : minval;
                    }
                    dstData[dstPixelOffset] = minval;
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
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    float minval = Float.MAX_VALUE;
                    for (int u = 0; u < wp; ++u) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        minval = val < minval ? val : minval;
                    }
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = val < minval ? val : minval;
                    }
                    dstData[dstPixelOffset] = minval;
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
                    int imageOffset = srcPixelOffset + srcPixelStride * offset;
                    double minval = Double.MAX_VALUE;
                    for (int u = 0; u < wp; ++u) {
                        val = srcData[imageOffset];
                        imageOffset += srcScanlineStride;
                        minval = val < minval ? val : minval;
                    }
                    imageOffset = srcPixelOffset + srcScanlineStride * offset;
                    for (int v = 0; v < wp; ++v) {
                        val = srcData[imageOffset];
                        imageOffset += srcPixelStride;
                        minval = val < minval ? val : minval;
                    }
                    dstData[dstPixelOffset] = minval;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

