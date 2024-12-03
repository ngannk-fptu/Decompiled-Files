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

final class MedianFilterSquareOpImage
extends MedianFilterOpImage {
    public MedianFilterSquareOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize) {
        super(source, extender, config, layout, MedianFilterDescriptor.MEDIAN_MASK_SQUARE, maskSize);
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
        int[] values = new int[filterSize * filterSize];
        int wp = filterSize;
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    int valueCount = 0;
                    for (int u = 0; u < wp; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < wp; ++v) {
                            values[valueCount++] = srcData[imageOffset] & 0xFF;
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
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
        int[] values = new int[filterSize * filterSize];
        int wp = filterSize;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    int valueCount = 0;
                    for (int u = 0; u < wp; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < wp; ++v) {
                            values[valueCount++] = srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
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
        int[] values = new int[filterSize * filterSize];
        int wp = filterSize;
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageVerticalOffset = srcPixelOffset;
                    int valueCount = 0;
                    for (int u = 0; u < wp; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < wp; ++v) {
                            values[valueCount++] = srcData[imageOffset] & 0xFFFF;
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
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
        int[] values = new int[filterSize * filterSize];
        int wp = filterSize;
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
                    int imageVerticalOffset = srcPixelOffset;
                    int valueCount = 0;
                    for (int u = 0; u < wp; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < wp; ++v) {
                            values[valueCount++] = srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
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
        float[] values = new float[filterSize * filterSize];
        int wp = filterSize;
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
                    int imageVerticalOffset = srcPixelOffset;
                    int valueCount = 0;
                    for (int u = 0; u < wp; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < wp; ++v) {
                            values[valueCount++] = srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
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
        double[] values = new double[filterSize * filterSize];
        int wp = filterSize;
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
                    int imageVerticalOffset = srcPixelOffset;
                    int valueCount = 0;
                    for (int u = 0; u < wp; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < wp; ++v) {
                            values[valueCount++] = srcData[imageOffset];
                            imageOffset += srcPixelStride;
                        }
                        imageVerticalOffset += srcScanlineStride;
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

