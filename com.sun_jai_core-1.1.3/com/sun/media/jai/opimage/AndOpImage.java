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

final class AndOpImage
extends PointOpImage {
    public AndOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout) {
        super(source1, source2, layout, config, true);
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor s1 = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor s2 = new RasterAccessor(sources[1], destRect, formatTags[1], this.getSourceImage(1).getColorModel());
        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[2], this.getColorModel());
        if (d.isBinary()) {
            byte[] src1Bits = s1.getBinaryDataArray();
            byte[] src2Bits = s2.getBinaryDataArray();
            byte[] dstBits = d.getBinaryDataArray();
            int length = dstBits.length;
            for (int i = 0; i < length; ++i) {
                dstBits[i] = (byte)(src1Bits[i] & src2Bits[i]);
            }
            d.copyBinaryDataToRaster();
            return;
        }
        int src1LineStride = s1.getScanlineStride();
        int src1PixelStride = s1.getPixelStride();
        int[] src1BandOffsets = s1.getBandOffsets();
        int src2LineStride = s2.getScanlineStride();
        int src2PixelStride = s2.getPixelStride();
        int[] src2BandOffsets = s2.getBandOffsets();
        int dstNumBands = d.getNumBands();
        int dstWidth = d.getWidth();
        int dstHeight = d.getHeight();
        int dstLineStride = d.getScanlineStride();
        int dstPixelStride = d.getPixelStride();
        int[] dstBandOffsets = d.getBandOffsets();
        switch (d.getDataType()) {
            case 0: {
                this.byteLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getByteDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getByteDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getByteDataArrays());
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getShortDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getShortDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getShortDataArrays());
                break;
            }
            case 3: {
                this.intLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getIntDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getIntDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getIntDataArrays());
            }
        }
        d.copyDataToRaster();
    }

    private void byteLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, byte[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, byte[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData) {
        for (int b = 0; b < dstNumBands; ++b) {
            byte[] s1 = src1Data[b];
            byte[] s2 = src2Data[b];
            byte[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[b];
            int src2LineOffset = src2BandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = (byte)(s1[src1PixelOffset] & s2[src2PixelOffset]);
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void shortLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, short[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, short[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {
        for (int b = 0; b < dstNumBands; ++b) {
            short[] s1 = src1Data[b];
            short[] s2 = src2Data[b];
            short[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[b];
            int src2LineOffset = src2BandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = (short)(s1[src1PixelOffset] & s2[src2PixelOffset]);
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void intLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, int[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, int[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData) {
        for (int b = 0; b < dstNumBands; ++b) {
            int[] s1 = src1Data[b];
            int[] s2 = src2Data[b];
            int[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[b];
            int src2LineOffset = src2BandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s1[src1PixelOffset] & s2[src2PixelOffset];
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }
}

