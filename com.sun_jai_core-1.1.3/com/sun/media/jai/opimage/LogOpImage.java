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

final class LogOpImage
extends ColormapOpImage {
    private byte[] byteTable = null;

    public LogOpImage(RenderedImage source, Map config, ImageLayout layout) {
        super(source, layout, config, true);
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        this.initByteTable();
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            int mapSize = map.length;
            for (int i = 0; i < mapSize; ++i) {
                map[i] = this.byteTable[map[i] & 0xFF];
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor s = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (d.getDataType()) {
            case 0: {
                this.computeRectByte(s, d);
                break;
            }
            case 1: {
                this.computeRectUShort(s, d);
                break;
            }
            case 2: {
                this.computeRectShort(s, d);
                break;
            }
            case 3: {
                this.computeRectInt(s, d);
                break;
            }
            case 4: {
                this.computeRectFloat(s, d);
                break;
            }
            case 5: {
                this.computeRectDouble(s, d);
            }
        }
        if (d.needsClamping()) {
            d.clampDataArrays();
        }
        d.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        this.initByteTable();
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        byte[][] srcData = src.getByteDataArrays();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        for (int b = 0; b < dstBands; ++b) {
            byte[] s = srcData[b];
            byte[] d = dstData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = this.byteTable[s[srcPixelOffset] & 0xFF];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        for (int b = 0; b < dstBands; ++b) {
            short[] s = srcData[b];
            short[] d = dstData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = (short)(Math.log(s[srcPixelOffset] & 0xFFFF) + 0.5);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        short[][] srcData = src.getShortDataArrays();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        for (int b = 0; b < dstBands; ++b) {
            short[] s = srcData[b];
            short[] d = dstData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = (short)(Math.log(s[srcPixelOffset]) + 0.5);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int[][] srcData = src.getIntDataArrays();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        for (int b = 0; b < dstBands; ++b) {
            int[] s = srcData[b];
            int[] d = dstData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    double p = s[srcPixelOffset];
                    d[dstPixelOffset] = p > 0.0 ? (int)(Math.log(p) + 0.5) : (p == 0.0 ? 0 : -1);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        float[][] srcData = src.getFloatDataArrays();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        for (int b = 0; b < dstBands; ++b) {
            float[] s = srcData[b];
            float[] d = dstData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = (float)Math.log(s[srcPixelOffset]);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        double[][] srcData = src.getDoubleDataArrays();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstBands = dst.getNumBands();
        for (int b = 0; b < dstBands; ++b) {
            double[] s = srcData[b];
            double[] d = dstData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = Math.log(s[srcPixelOffset]);
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private synchronized void initByteTable() {
        if (this.byteTable != null) {
            return;
        }
        this.byteTable = new byte[256];
        this.byteTable[0] = 0;
        this.byteTable[1] = 0;
        for (int i = 2; i < 256; ++i) {
            this.byteTable[i] = (byte)(Math.log(i) + 0.5);
        }
    }
}

