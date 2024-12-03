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

final class ClampOpImage
extends PointOpImage {
    private byte[][] byteTable = null;
    private final double[] low;
    private final double[] high;

    private synchronized void initByteTable() {
        if (this.byteTable == null) {
            int numBands = this.getSampleModel().getNumBands();
            this.byteTable = new byte[numBands][256];
            for (int b = 0; b < numBands; ++b) {
                byte[] t = this.byteTable[b];
                int l = (int)this.low[b];
                int h = (int)this.high[b];
                byte bl = (byte)l;
                byte bh = (byte)h;
                for (int i = 0; i < 256; ++i) {
                    t[i] = i < l ? bl : (i > h ? bh : (byte)i);
                }
            }
        }
    }

    public ClampOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high) {
        super(source, layout, config, true);
        int numBands = this.getSampleModel().getNumBands();
        if (low.length < numBands || high.length < numBands) {
            this.low = new double[numBands];
            this.high = new double[numBands];
            for (int i = 0; i < numBands; ++i) {
                this.low[i] = low[0];
                this.high[i] = high[0];
            }
        } else {
            this.low = (double[])low.clone();
            this.high = (double[])high.clone();
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (dst.getDataType()) {
            case 0: {
                this.computeRectByte(src, dst);
                break;
            }
            case 1: {
                this.computeRectUShort(src, dst);
                break;
            }
            case 2: {
                this.computeRectShort(src, dst);
                break;
            }
            case 3: {
                this.computeRectInt(src, dst);
                break;
            }
            case 4: {
                this.computeRectFloat(src, dst);
                break;
            }
            case 5: {
                this.computeRectDouble(src, dst);
            }
        }
        dst.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        this.initByteTable();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        byte[][] srcData = src.getByteDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            byte[] d = dstData[b];
            byte[] s = srcData[b];
            byte[] t = this.byteTable[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] & 0xFF];
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            short[] d = dstData[b];
            short[] s = srcData[b];
            int lo = (int)this.low[b];
            int hi = (int)this.high[b];
            short slo = (short)lo;
            short shi = (short)hi;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    int p = s[srcPixelOffset] & 0xFFFF;
                    d[dstPixelOffset] = p < lo ? slo : (p > hi ? shi : (short)p);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            short[] d = dstData[b];
            short[] s = srcData[b];
            int lo = (int)this.low[b];
            int hi = (int)this.high[b];
            short slo = (short)lo;
            short shi = (short)hi;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    short p = s[srcPixelOffset];
                    d[dstPixelOffset] = p < lo ? slo : (p > hi ? shi : p);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int[][] srcData = src.getIntDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            int[] d = dstData[b];
            int[] s = srcData[b];
            double lo = this.low[b];
            double hi = this.high[b];
            int ilo = (int)lo;
            int ihi = (int)hi;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    int p = s[srcPixelOffset];
                    d[dstPixelOffset] = (double)p < lo ? ilo : ((double)p > hi ? ihi : p);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        float[][] srcData = src.getFloatDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            float[] d = dstData[b];
            float[] s = srcData[b];
            double lo = this.low[b];
            double hi = this.high[b];
            float flo = (float)lo;
            float fhi = (float)hi;
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    float p = s[srcPixelOffset];
                    d[dstPixelOffset] = (double)p < lo ? flo : ((double)p > hi ? fhi : p);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        double[][] srcData = src.getDoubleDataArrays();
        for (int b = 0; b < dstBands; ++b) {
            double[] d = dstData[b];
            double[] s = srcData[b];
            double lo = this.low[b];
            double hi = this.high[b];
            int dstLineOffset = dstBandOffsets[b];
            int srcLineOffset = srcBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int dstPixelOffset = dstLineOffset;
                int srcPixelOffset = srcLineOffset;
                dstLineOffset += dstLineStride;
                srcLineOffset += srcLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    double p = s[srcPixelOffset];
                    d[dstPixelOffset] = p < lo ? lo : (p > hi ? hi : p);
                    dstPixelOffset += dstPixelStride;
                    srcPixelOffset += srcPixelStride;
                }
            }
        }
    }
}

