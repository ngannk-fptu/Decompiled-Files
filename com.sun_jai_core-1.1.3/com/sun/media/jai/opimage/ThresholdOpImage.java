/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

final class ThresholdOpImage
extends ColormapOpImage {
    private double[] low;
    private double[] high;
    private double[] constants;
    private byte[][] byteTable = null;

    public ThresholdOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high, double[] constants) {
        super(source, layout, config, true);
        int numBands = this.getSampleModel().getNumBands();
        this.low = new double[numBands];
        this.high = new double[numBands];
        this.constants = new double[numBands];
        for (int i = 0; i < numBands; ++i) {
            this.low[i] = low.length < numBands ? low[0] : low[i];
            this.high[i] = high.length < numBands ? high[0] : high[i];
            this.constants[i] = constants.length < numBands ? constants[0] : constants[i];
        }
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        this.initByteTable();
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            byte[] luTable = this.byteTable[b >= this.byteTable.length ? 0 : b];
            int mapSize = map.length;
            for (int i = 0; i < mapSize; ++i) {
                map[i] = luTable[map[i] & 0xFF];
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int srcPixelStride = src.getPixelStride();
        int srcLineStride = src.getScanlineStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstLineStride = dst.getScanlineStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int width = dst.getWidth() * dstPixelStride;
        int height = dst.getHeight() * dstLineStride;
        int bands = dst.getNumBands();
        switch (dst.getDataType()) {
            case 0: {
                this.byteLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getByteDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getByteDataArrays());
                break;
            }
            case 2: {
                this.shortLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getShortDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getShortDataArrays());
                break;
            }
            case 1: {
                this.ushortLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getShortDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getShortDataArrays());
                break;
            }
            case 3: {
                this.intLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getIntDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getIntDataArrays());
                break;
            }
            case 4: {
                this.floatLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getFloatDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getFloatDataArrays());
                break;
            }
            case 5: {
                this.doubleLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getDoubleDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getDoubleDataArrays());
            }
        }
        if (dst.isDataCopy()) {
            dst.clampDataArrays();
            dst.copyDataToRaster();
        }
    }

    private void byteLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, byte[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, byte[][] dstData) {
        this.initByteTable();
        for (int b = 0; b < bands; ++b) {
            byte[] s = srcData[b];
            byte[] d = dstData[b];
            byte[] t = this.byteTable[b];
            int heightEnd = dstBandOffsets[b] + height;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            while (dstLineOffset < heightEnd) {
                int widthEnd = dstLineOffset + width;
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                while (dstPixelOffset < widthEnd) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] & 0xFF];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
            }
        }
    }

    private void shortLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, short[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, short[][] dstData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            short[] d = dstData[b];
            double l = this.low[b];
            double h = this.high[b];
            short c = (short)this.constants[b];
            int heightEnd = dstBandOffsets[b] + height;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            while (dstLineOffset < heightEnd) {
                int widthEnd = dstLineOffset + width;
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                while (dstPixelOffset < widthEnd) {
                    short p = s[srcPixelOffset];
                    d[dstPixelOffset] = (double)p >= l && (double)p <= h ? c : p;
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
            }
        }
    }

    private void ushortLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, short[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, short[][] dstData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            short[] d = dstData[b];
            double l = this.low[b];
            double h = this.high[b];
            short c = (short)this.constants[b];
            int heightEnd = dstBandOffsets[b] + height;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            while (dstLineOffset < heightEnd) {
                int widthEnd = dstLineOffset + width;
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                while (dstPixelOffset < widthEnd) {
                    int p = s[srcPixelOffset] & 0xFFFF;
                    d[dstPixelOffset] = (double)p >= l && (double)p <= h ? c : (short)p;
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
            }
        }
    }

    private void intLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, int[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, int[][] dstData) {
        for (int b = 0; b < bands; ++b) {
            int[] s = srcData[b];
            int[] d = dstData[b];
            double l = this.low[b];
            double h = this.high[b];
            int c = (int)this.constants[b];
            int heightEnd = dstBandOffsets[b] + height;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            while (dstLineOffset < heightEnd) {
                int widthEnd = dstLineOffset + width;
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                while (dstPixelOffset < widthEnd) {
                    int p = s[srcPixelOffset];
                    d[dstPixelOffset] = (double)p >= l && (double)p <= h ? c : p;
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
            }
        }
    }

    private void floatLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, float[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, float[][] dstData) {
        for (int b = 0; b < bands; ++b) {
            float[] s = srcData[b];
            float[] d = dstData[b];
            double l = this.low[b];
            double h = this.high[b];
            float c = (float)this.constants[b];
            int heightEnd = dstBandOffsets[b] + height;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            while (dstLineOffset < heightEnd) {
                int widthEnd = dstLineOffset + width;
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                while (dstPixelOffset < widthEnd) {
                    float p = s[srcPixelOffset];
                    d[dstPixelOffset] = (double)p >= l && (double)p <= h ? c : p;
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
            }
        }
    }

    private void doubleLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, double[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, double[][] dstData) {
        for (int b = 0; b < bands; ++b) {
            double[] s = srcData[b];
            double[] d = dstData[b];
            double l = this.low[b];
            double h = this.high[b];
            double c = this.constants[b];
            int heightEnd = dstBandOffsets[b] + height;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            while (dstLineOffset < heightEnd) {
                int widthEnd = dstLineOffset + width;
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                while (dstPixelOffset < widthEnd) {
                    double p = s[srcPixelOffset];
                    d[dstPixelOffset] = p >= l && p <= h ? c : p;
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
            }
        }
    }

    private synchronized void initByteTable() {
        if (this.byteTable != null) {
            return;
        }
        int numBands = this.getSampleModel().getNumBands();
        this.byteTable = new byte[numBands][256];
        for (int b = 0; b < numBands; ++b) {
            double l = this.low[b];
            double h = this.high[b];
            byte c = (byte)this.constants[b];
            byte[] t = this.byteTable[b];
            for (int i = 0; i < 256; ++i) {
                t[i] = (double)i >= l && (double)i <= h ? c : (byte)i;
            }
        }
    }
}

