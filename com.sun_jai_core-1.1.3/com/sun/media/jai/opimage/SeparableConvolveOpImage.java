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

final class SeparableConvolveOpImage
extends AreaOpImage {
    static int byteLoopCounter = 0;
    protected KernelJAI kernel;
    protected int kw;
    protected int kh;
    protected int kx;
    protected int ky;
    protected float[] hValues;
    protected float[] vValues;
    protected float[][] hTables;

    public SeparableConvolveOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel) {
        super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
        this.kernel = kernel;
        this.kw = kernel.getWidth();
        this.kh = kernel.getHeight();
        this.kx = kernel.getXOrigin();
        this.ky = kernel.getYOrigin();
        this.hValues = kernel.getHorizontalKernelData();
        this.vValues = kernel.getVerticalKernelData();
        if (this.sampleModel.getDataType() == 0) {
            this.hTables = new float[this.hValues.length][256];
            for (int i = 0; i < this.hValues.length; ++i) {
                float k = this.hValues[i];
                for (int j = 0; j < 256; ++j) {
                    byte b = (byte)j;
                    float f = j;
                    this.hTables[i][b + 128] = k * f;
                }
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], this.getSource(0).getColorModel());
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

    protected void byteLoop(RasterAccessor src, RasterAccessor dst) {
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
        float[] tmpBuffer = new float[this.kh * dwidth];
        int tmpBufferSize = this.kh * dwidth;
        for (int k = 0; k < dnumBands; ++k) {
            int srcPixelOffset;
            int j;
            byte[] dstData = dstDataArrays[k];
            byte[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int revolver = 0;
            int kvRevolver = 0;
            for (j = 0; j < this.kh - 1; ++j) {
                srcPixelOffset = srcScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += this.hTables[v][srcData[imageOffset] + 128];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    srcPixelOffset += srcPixelStride;
                }
                revolver += dwidth;
                srcScanlineOffset += srcScanlineStride;
            }
            for (j = 0; j < dheight; ++j) {
                srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += this.hTables[v][srcData[imageOffset] + 128];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    f = 0.5f;
                    int b = kvRevolver + i;
                    for (int a = 0; a < this.kh; ++a) {
                        f += tmpBuffer[b] * this.vValues[a];
                        if ((b += dwidth) < tmpBufferSize) continue;
                        b -= tmpBufferSize;
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
                if ((revolver += dwidth) == tmpBufferSize) {
                    revolver = 0;
                }
                if ((kvRevolver += dwidth) == tmpBufferSize) {
                    kvRevolver = 0;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void shortLoop(RasterAccessor src, RasterAccessor dst) {
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
        float[] tmpBuffer = new float[this.kh * dwidth];
        int tmpBufferSize = this.kh * dwidth;
        for (int k = 0; k < dnumBands; ++k) {
            int srcPixelOffset;
            int j;
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int revolver = 0;
            int kvRevolver = 0;
            for (j = 0; j < this.kh - 1; ++j) {
                srcPixelOffset = srcScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += (float)srcData[imageOffset] * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    srcPixelOffset += srcPixelStride;
                }
                revolver += dwidth;
                srcScanlineOffset += srcScanlineStride;
            }
            for (j = 0; j < dheight; ++j) {
                srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += (float)srcData[imageOffset] * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    f = 0.5f;
                    int b = kvRevolver + i;
                    for (int a = 0; a < this.kh; ++a) {
                        f += tmpBuffer[b] * this.vValues[a];
                        if ((b += dwidth) < tmpBufferSize) continue;
                        b -= tmpBufferSize;
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
                if ((revolver += dwidth) == tmpBufferSize) {
                    revolver = 0;
                }
                if ((kvRevolver += dwidth) == tmpBufferSize) {
                    kvRevolver = 0;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void ushortLoop(RasterAccessor src, RasterAccessor dst) {
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
        float[] tmpBuffer = new float[this.kh * dwidth];
        int tmpBufferSize = this.kh * dwidth;
        for (int k = 0; k < dnumBands; ++k) {
            int srcPixelOffset;
            int j;
            short[] dstData = dstDataArrays[k];
            short[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int revolver = 0;
            int kvRevolver = 0;
            for (j = 0; j < this.kh - 1; ++j) {
                srcPixelOffset = srcScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += (float)(srcData[imageOffset] & 0xFFFF) * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    srcPixelOffset += srcPixelStride;
                }
                revolver += dwidth;
                srcScanlineOffset += srcScanlineStride;
            }
            for (j = 0; j < dheight; ++j) {
                srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += (float)(srcData[imageOffset] & 0xFFFF) * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    f = 0.5f;
                    int b = kvRevolver + i;
                    for (int a = 0; a < this.kh; ++a) {
                        f += tmpBuffer[b] * this.vValues[a];
                        if ((b += dwidth) < tmpBufferSize) continue;
                        b -= tmpBufferSize;
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
                if ((revolver += dwidth) == tmpBufferSize) {
                    revolver = 0;
                }
                if ((kvRevolver += dwidth) == tmpBufferSize) {
                    kvRevolver = 0;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void intLoop(RasterAccessor src, RasterAccessor dst) {
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
        float[] tmpBuffer = new float[this.kh * dwidth];
        int tmpBufferSize = this.kh * dwidth;
        for (int k = 0; k < dnumBands; ++k) {
            int srcPixelOffset;
            int j;
            int[] dstData = dstDataArrays[k];
            int[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int revolver = 0;
            int kvRevolver = 0;
            for (j = 0; j < this.kh - 1; ++j) {
                srcPixelOffset = srcScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += (float)srcData[imageOffset] * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    srcPixelOffset += srcPixelStride;
                }
                revolver += dwidth;
                srcScanlineOffset += srcScanlineStride;
            }
            for (j = 0; j < dheight; ++j) {
                srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int val;
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += (float)srcData[imageOffset] * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    f = 0.5f;
                    int b = kvRevolver + i;
                    for (int a = 0; a < this.kh; ++a) {
                        f += tmpBuffer[b] * this.vValues[a];
                        if ((b += dwidth) < tmpBufferSize) continue;
                        b -= tmpBufferSize;
                    }
                    dstData[dstPixelOffset] = val = (int)f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                if ((revolver += dwidth) == tmpBufferSize) {
                    revolver = 0;
                }
                if ((kvRevolver += dwidth) == tmpBufferSize) {
                    kvRevolver = 0;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void floatLoop(RasterAccessor src, RasterAccessor dst) {
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
        float[] tmpBuffer = new float[this.kh * dwidth];
        int tmpBufferSize = this.kh * dwidth;
        for (int k = 0; k < dnumBands; ++k) {
            int srcPixelOffset;
            int j;
            float[] dstData = dstDataArrays[k];
            float[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int revolver = 0;
            int kvRevolver = 0;
            for (j = 0; j < this.kh - 1; ++j) {
                srcPixelOffset = srcScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += srcData[imageOffset] * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    srcPixelOffset += srcPixelStride;
                }
                revolver += dwidth;
                srcScanlineOffset += srcScanlineStride;
            }
            for (j = 0; j < dheight; ++j) {
                srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    float f = 0.0f;
                    for (int v = 0; v < this.kw; ++v) {
                        f += srcData[imageOffset] * this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    f = 0.0f;
                    int b = kvRevolver + i;
                    for (int a = 0; a < this.kh; ++a) {
                        f += tmpBuffer[b] * this.vValues[a];
                        if ((b += dwidth) < tmpBufferSize) continue;
                        b -= tmpBufferSize;
                    }
                    dstData[dstPixelOffset] = f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                if ((revolver += dwidth) == tmpBufferSize) {
                    revolver = 0;
                }
                if ((kvRevolver += dwidth) == tmpBufferSize) {
                    kvRevolver = 0;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }

    protected void doubleLoop(RasterAccessor src, RasterAccessor dst) {
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
        double[] tmpBuffer = new double[this.kh * dwidth];
        int tmpBufferSize = this.kh * dwidth;
        for (int k = 0; k < dnumBands; ++k) {
            int srcPixelOffset;
            int j;
            double[] dstData = dstDataArrays[k];
            double[] srcData = srcDataArrays[k];
            int srcScanlineOffset = srcBandOffsets[k];
            int dstScanlineOffset = dstBandOffsets[k];
            int revolver = 0;
            int kvRevolver = 0;
            for (j = 0; j < this.kh - 1; ++j) {
                srcPixelOffset = srcScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    double f = 0.0;
                    for (int v = 0; v < this.kw; ++v) {
                        f += srcData[imageOffset] * (double)this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    srcPixelOffset += srcPixelStride;
                }
                revolver += dwidth;
                srcScanlineOffset += srcScanlineStride;
            }
            for (j = 0; j < dheight; ++j) {
                srcPixelOffset = srcScanlineOffset;
                int dstPixelOffset = dstScanlineOffset;
                for (int i = 0; i < dwidth; ++i) {
                    int imageOffset = srcPixelOffset;
                    double f = 0.0;
                    for (int v = 0; v < this.kw; ++v) {
                        f += srcData[imageOffset] * (double)this.hValues[v];
                        imageOffset += srcPixelStride;
                    }
                    tmpBuffer[revolver + i] = f;
                    f = 0.0;
                    int b = kvRevolver + i;
                    for (int a = 0; a < this.kh; ++a) {
                        f += tmpBuffer[b] * (double)this.vValues[a];
                        if ((b += dwidth) < tmpBufferSize) continue;
                        b -= tmpBufferSize;
                    }
                    dstData[dstPixelOffset] = f;
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
                if ((revolver += dwidth) == tmpBufferSize) {
                    revolver = 0;
                }
                if ((kvRevolver += dwidth) == tmpBufferSize) {
                    kvRevolver = 0;
                }
                srcScanlineOffset += srcScanlineStride;
                dstScanlineOffset += dstScanlineStride;
            }
        }
    }
}

