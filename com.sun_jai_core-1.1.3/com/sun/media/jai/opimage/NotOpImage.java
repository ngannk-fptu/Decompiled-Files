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

final class NotOpImage
extends ColormapOpImage {
    public NotOpImage(RenderedImage source, Map config, ImageLayout layout) {
        super(source, layout, config, true);
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            int mapSize = map.length;
            for (int i = 0; i < mapSize; ++i) {
                map[i] = ~map[i];
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor src = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        if (dst.isBinary()) {
            byte[] srcBits = src.getBinaryDataArray();
            byte[] dstBits = dst.getBinaryDataArray();
            int length = dstBits.length;
            for (int i = 0; i < length; ++i) {
                dstBits[i] = ~srcBits[i];
            }
            dst.copyBinaryDataToRaster();
            return;
        }
        int srcLineStride = src.getScanlineStride();
        int srcPixelStride = src.getPixelStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int dstNumBands = dst.getNumBands();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        switch (dst.getDataType()) {
            case 0: {
                this.byteLoop(dstNumBands, dstWidth, dstHeight, srcLineStride, srcPixelStride, srcBandOffsets, src.getByteDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, dst.getByteDataArrays());
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(dstNumBands, dstWidth, dstHeight, srcLineStride, srcPixelStride, srcBandOffsets, src.getShortDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, dst.getShortDataArrays());
                break;
            }
            case 3: {
                this.intLoop(dstNumBands, dstWidth, dstHeight, srcLineStride, srcPixelStride, srcBandOffsets, src.getIntDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, dst.getIntDataArrays());
            }
        }
        dst.copyDataToRaster();
    }

    private void byteLoop(int dstNumBands, int dstWidth, int dstHeight, int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData) {
        for (int b = 0; b < dstNumBands; ++b) {
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
                    d[dstPixelOffset] = ~s[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void shortLoop(int dstNumBands, int dstWidth, int dstHeight, int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {
        for (int b = 0; b < dstNumBands; ++b) {
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
                    d[dstPixelOffset] = ~s[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void intLoop(int dstNumBands, int dstWidth, int dstHeight, int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData) {
        for (int b = 0; b < dstNumBands; ++b) {
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
                    d[dstPixelOffset] = ~s[srcPixelOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }
}

