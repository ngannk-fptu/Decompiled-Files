/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

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

final class ConvolveOpImage
extends AreaOpImage {
    protected KernelJAI kernel;
    private int kw;
    private int kh;
    private int kx;
    private int ky;

    public ConvolveOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel) {
        super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
        this.kernel = kernel;
        this.kw = kernel.getWidth();
        this.kh = kernel.getHeight();
        this.kx = kernel.getXOrigin();
        this.ky = kernel.getYOrigin();
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
            case 3: {
                this.intLoop(srcAccessor, dstAccessor);
                break;
            }
            case 2: {
                this.shortLoop(srcAccessor, dstAccessor);
                break;
            }
            case 1: {
                this.ushortLoop(srcAccessor, dstAccessor);
                break;
            }
            case 4: {
                this.floatLoop(srcAccessor, dstAccessor);
                break;
            }
            case 5: {
                this.doubleLoop(srcAccessor, dstAccessor);
                break;
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    private void byteLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] kdata = this.kernel.getKernelData();
        int kw = this.kernel.getWidth();
        int kh = this.kernel.getHeight();
        byte[][] dstDataArrays = dst.getByteDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        byte[][] srcDataArrays = src.getByteDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        for (int k = 0; k < dnumBands; ++k) {
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = 0.5f;
                    int kernelVerticalOffset = 0;
                    int imageVerticalOffset = srcPixelOffset;
                    for (int u = 0; u < kh; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < kw; ++v) {
                            f += (float)(srcData[imageOffset] & 0xFF) * kdata[kernelVerticalOffset + v];
                            imageOffset += srcPixelStride;
                        }
                        kernelVerticalOffset += kw;
                        imageVerticalOffset += srcScanlineStride;
                    }
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
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] kdata = this.kernel.getKernelData();
        int kw = this.kernel.getWidth();
        int kh = this.kernel.getHeight();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = 0.5f;
                    int kernelVerticalOffset = 0;
                    int imageVerticalOffset = srcPixelOffset;
                    for (int u = 0; u < kh; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < kw; ++v) {
                            f += (float)srcData[imageOffset] * kdata[kernelVerticalOffset + v];
                            imageOffset += srcPixelStride;
                        }
                        kernelVerticalOffset += kw;
                        imageVerticalOffset += srcScanlineStride;
                    }
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

    private void ushortLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] kdata = this.kernel.getKernelData();
        int kw = this.kernel.getWidth();
        int kh = this.kernel.getHeight();
        short[][] dstDataArrays = dst.getShortDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        short[][] srcDataArrays = src.getShortDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        for (int k = 0; k < dnumBands; ++k) {
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = 0.5f;
                    int kernelVerticalOffset = 0;
                    int imageVerticalOffset = srcPixelOffset;
                    for (int u = 0; u < kh; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < kw; ++v) {
                            f += (float)(srcData[imageOffset] & 0xFFFF) * kdata[kernelVerticalOffset + v];
                            imageOffset += srcPixelStride;
                        }
                        kernelVerticalOffset += kw;
                        imageVerticalOffset += srcScanlineStride;
                    }
                    int val = (int)f;
                    if (val < 0) {
                        val = 0;
                    } else if (val > 65535) {
                        val = 65535;
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
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] kdata = this.kernel.getKernelData();
        int kw = this.kernel.getWidth();
        int kh = this.kernel.getHeight();
        int[][] dstDataArrays = dst.getIntDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        int[][] srcDataArrays = src.getIntDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        for (int k = 0; k < dnumBands; ++k) {
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = 0.5f;
                    int kernelVerticalOffset = 0;
                    int imageVerticalOffset = srcPixelOffset;
                    for (int u = 0; u < kh; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < kw; ++v) {
                            f += (float)srcData[imageOffset] * kdata[kernelVerticalOffset + v];
                            imageOffset += srcPixelStride;
                        }
                        kernelVerticalOffset += kw;
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = (int)f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void floatLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] kdata = this.kernel.getKernelData();
        int kw = this.kernel.getWidth();
        int kh = this.kernel.getHeight();
        float[][] dstDataArrays = dst.getFloatDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        float[][] srcDataArrays = src.getFloatDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        for (int k = 0; k < dnumBands; ++k) {
            float[] dstData = dstDataArrays[k];
            float[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    float f = 0.0f;
                    int kernelVerticalOffset = 0;
                    int imageVerticalOffset = srcPixelOffset;
                    for (int u = 0; u < kh; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < kw; ++v) {
                            f += srcData[imageOffset] * kdata[kernelVerticalOffset + v];
                            imageOffset += srcPixelStride;
                        }
                        kernelVerticalOffset += kw;
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    private void doubleLoop(RasterAccessor src, RasterAccessor dst) {
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dnumBands = dst.getNumBands();
        float[] kdata = this.kernel.getKernelData();
        int kw = this.kernel.getWidth();
        int kh = this.kernel.getHeight();
        double[][] dstDataArrays = dst.getDoubleDataArrays();
        int[] dstBandOffsets = dst.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstScanlineStride = dst.getScanlineStride();
        double[][] srcDataArrays = src.getDoubleDataArrays();
        int[] srcBandOffsets = src.getBandOffsets();
        int srcPixelStride = src.getPixelStride();
        int srcScanlineStride = src.getScanlineStride();
        for (int k = 0; k < dnumBands; ++k) {
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            for (int j = 0; j < dheight; ++j) {
                int srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    double f = 0.5;
                    int kernelVerticalOffset = 0;
                    int imageVerticalOffset = srcPixelOffset;
                    for (int u = 0; u < kh; ++u) {
                        int imageOffset = imageVerticalOffset;
                        for (int v = 0; v < kw; ++v) {
                            f += srcData[imageOffset] * (double)kdata[kernelVerticalOffset + v];
                            imageOffset += srcPixelStride;
                        }
                        kernelVerticalOffset += kw;
                        imageVerticalOffset += srcScanlineStride;
                    }
                    dstData[dstPixelOffset] = f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

