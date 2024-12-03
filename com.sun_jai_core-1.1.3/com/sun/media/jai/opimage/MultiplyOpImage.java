/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;

final class MultiplyOpImage
extends PointOpImage {
    private byte[][] multiplyTableByte;
    private int s1bd = 1;
    private int s2bd = 1;

    public MultiplyOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout) {
        super(source1, source2, layout, config, true);
        SampleModel sm;
        int numBandsDst;
        int numBands1 = source1.getSampleModel().getNumBands();
        int numBands2 = source2.getSampleModel().getNumBands();
        if (layout != null && layout.isValid(256) && (numBandsDst = (sm = layout.getSampleModel(null)).getNumBands()) > 1 && (numBands1 == 1 && numBands2 > 1 || numBands2 == 1 && numBands1 > 1)) {
            numBandsDst = Math.min(Math.max(numBands1, numBands2), numBandsDst);
            if (numBandsDst != this.sampleModel.getNumBands()) {
                this.sampleModel = RasterFactory.createComponentSampleModel(sm, this.sampleModel.getTransferType(), this.sampleModel.getWidth(), this.sampleModel.getHeight(), numBandsDst);
                if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                    this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
                }
            }
            this.s1bd = numBands1 == 1 ? 0 : 1;
            int n = this.s2bd = numBands2 == 1 ? 0 : 1;
        }
        if (this.sampleModel.getTransferType() == 0) {
            this.multiplyTableByte = new byte[256][256];
            for (int j = 0; j < 256; ++j) {
                byte[] array = this.multiplyTableByte[j];
                for (int i = 0; i < 256; ++i) {
                    array[i] = ImageUtil.clampByte(i * j);
                }
            }
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor s1 = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor s2 = new RasterAccessor(sources[1], destRect, formatTags[1], this.getSource(1).getColorModel());
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
            case 1: {
                this.ushortLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getShortDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getShortDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getShortDataArrays());
                break;
            }
            case 2: {
                this.shortLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getShortDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getShortDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getShortDataArrays());
                break;
            }
            case 3: {
                this.intLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getIntDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getIntDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getIntDataArrays());
                break;
            }
            case 4: {
                this.floatLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getFloatDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getFloatDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getFloatDataArrays());
                break;
            }
            case 5: {
                this.doubleLoop(dstNumBands, dstWidth, dstHeight, src1LineStride, src1PixelStride, src1BandOffsets, s1.getDoubleDataArrays(), src2LineStride, src2PixelStride, src2BandOffsets, s2.getDoubleDataArrays(), dstLineStride, dstPixelStride, dstBandOffsets, d.getDoubleDataArrays());
            }
        }
        if (d.needsClamping()) {
            d.clampDataArrays();
        }
        d.copyDataToRaster();
    }

    private void byteLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, byte[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, byte[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData) {
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < dstNumBands) {
            byte[] s1 = src1Data[s1b];
            byte[] s2 = src2Data[s2b];
            byte[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[s1b];
            int src2LineOffset = src2BandOffsets[s2b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = this.multiplyTableByte[s1[src1PixelOffset] & 0xFF][s2[src2PixelOffset] & 0xFF];
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void ushortLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, short[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, short[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < dstNumBands) {
            short[] s1 = src1Data[s1b];
            short[] s2 = src2Data[s2b];
            short[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[s1b];
            int src2LineOffset = src2BandOffsets[s2b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = ImageUtil.clampUShort((s1[src1PixelOffset] & 0xFFFF) * (s2[src2PixelOffset] & 0xFFFF));
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void shortLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, short[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, short[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData) {
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < dstNumBands) {
            short[] s1 = src1Data[s1b];
            short[] s2 = src2Data[s2b];
            short[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[s1b];
            int src2LineOffset = src2BandOffsets[s2b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = ImageUtil.clampShort(s1[src1PixelOffset] * s2[src2PixelOffset]);
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void intLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, int[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, int[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData) {
        switch (this.sampleModel.getTransferType()) {
            case 0: {
                int b = 0;
                int s1b = 0;
                int s2b = 0;
                while (b < dstNumBands) {
                    int[] s1 = src1Data[s1b];
                    int[] s2 = src2Data[s2b];
                    int[] d = dstData[b];
                    int src1LineOffset = src1BandOffsets[s1b];
                    int src2LineOffset = src2BandOffsets[s2b];
                    int dstLineOffset = dstBandOffsets[b];
                    for (int h = 0; h < dstHeight; ++h) {
                        int src1PixelOffset = src1LineOffset;
                        int src2PixelOffset = src2LineOffset;
                        int dstPixelOffset = dstLineOffset;
                        src1LineOffset += src1LineStride;
                        src2LineOffset += src2LineStride;
                        dstLineOffset += dstLineStride;
                        for (int w = 0; w < dstWidth; ++w) {
                            d[dstPixelOffset] = ImageUtil.clampByte(s1[src1PixelOffset] * s2[src2PixelOffset]);
                            src1PixelOffset += src1PixelStride;
                            src2PixelOffset += src2PixelStride;
                            dstPixelOffset += dstPixelStride;
                        }
                    }
                    ++b;
                    s1b += this.s1bd;
                    s2b += this.s2bd;
                }
                break;
            }
            case 1: {
                int b = 0;
                int s1b = 0;
                int s2b = 0;
                while (b < dstNumBands) {
                    int[] s1 = src1Data[s1b];
                    int[] s2 = src2Data[s2b];
                    int[] d = dstData[b];
                    int src1LineOffset = src1BandOffsets[s1b];
                    int src2LineOffset = src2BandOffsets[s2b];
                    int dstLineOffset = dstBandOffsets[b];
                    for (int h = 0; h < dstHeight; ++h) {
                        int src1PixelOffset = src1LineOffset;
                        int src2PixelOffset = src2LineOffset;
                        int dstPixelOffset = dstLineOffset;
                        src1LineOffset += src1LineStride;
                        src2LineOffset += src2LineStride;
                        dstLineOffset += dstLineStride;
                        for (int w = 0; w < dstWidth; ++w) {
                            d[dstPixelOffset] = ImageUtil.clampUShort(s1[src1PixelOffset] * s2[src2PixelOffset]);
                            src1PixelOffset += src1PixelStride;
                            src2PixelOffset += src2PixelStride;
                            dstPixelOffset += dstPixelStride;
                        }
                    }
                    ++b;
                    s1b += this.s1bd;
                    s2b += this.s2bd;
                }
                break;
            }
            case 2: {
                int b = 0;
                int s1b = 0;
                int s2b = 0;
                while (b < dstNumBands) {
                    int[] s1 = src1Data[s1b];
                    int[] s2 = src2Data[s2b];
                    int[] d = dstData[b];
                    int src1LineOffset = src1BandOffsets[s1b];
                    int src2LineOffset = src2BandOffsets[s2b];
                    int dstLineOffset = dstBandOffsets[b];
                    for (int h = 0; h < dstHeight; ++h) {
                        int src1PixelOffset = src1LineOffset;
                        int src2PixelOffset = src2LineOffset;
                        int dstPixelOffset = dstLineOffset;
                        src1LineOffset += src1LineStride;
                        src2LineOffset += src2LineStride;
                        dstLineOffset += dstLineStride;
                        for (int w = 0; w < dstWidth; ++w) {
                            d[dstPixelOffset] = ImageUtil.clampShort(s1[src1PixelOffset] * s2[src2PixelOffset]);
                            src1PixelOffset += src1PixelStride;
                            src2PixelOffset += src2PixelStride;
                            dstPixelOffset += dstPixelStride;
                        }
                    }
                    ++b;
                    s1b += this.s1bd;
                    s2b += this.s2bd;
                }
                break;
            }
            case 3: {
                int b = 0;
                int s1b = 0;
                int s2b = 0;
                while (b < dstNumBands) {
                    int[] s1 = src1Data[s1b];
                    int[] s2 = src2Data[s2b];
                    int[] d = dstData[b];
                    int src1LineOffset = src1BandOffsets[s1b];
                    int src2LineOffset = src2BandOffsets[s2b];
                    int dstLineOffset = dstBandOffsets[b];
                    for (int h = 0; h < dstHeight; ++h) {
                        int src1PixelOffset = src1LineOffset;
                        int src2PixelOffset = src2LineOffset;
                        int dstPixelOffset = dstLineOffset;
                        src1LineOffset += src1LineStride;
                        src2LineOffset += src2LineStride;
                        dstLineOffset += dstLineStride;
                        for (int w = 0; w < dstWidth; ++w) {
                            d[dstPixelOffset] = ImageUtil.clampInt((long)s1[src1PixelOffset] * (long)s2[src2PixelOffset]);
                            src1PixelOffset += src1PixelStride;
                            src2PixelOffset += src2PixelStride;
                            dstPixelOffset += dstPixelStride;
                        }
                    }
                    ++b;
                    s1b += this.s1bd;
                    s2b += this.s2bd;
                }
                break;
            }
        }
    }

    private void floatLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, float[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, float[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData) {
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < dstNumBands) {
            float[] s1 = src1Data[s1b];
            float[] s2 = src2Data[s2b];
            float[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[s1b];
            int src2LineOffset = src2BandOffsets[s2b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s1[src1PixelOffset] * s2[src2PixelOffset];
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void doubleLoop(int dstNumBands, int dstWidth, int dstHeight, int src1LineStride, int src1PixelStride, int[] src1BandOffsets, double[][] src1Data, int src2LineStride, int src2PixelStride, int[] src2BandOffsets, double[][] src2Data, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData) {
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < dstNumBands) {
            double[] s1 = src1Data[s1b];
            double[] s2 = src2Data[s2b];
            double[] d = dstData[b];
            int src1LineOffset = src1BandOffsets[s1b];
            int src2LineOffset = src2BandOffsets[s2b];
            int dstLineOffset = dstBandOffsets[b];
            for (int h = 0; h < dstHeight; ++h) {
                int src1PixelOffset = src1LineOffset;
                int src2PixelOffset = src2LineOffset;
                int dstPixelOffset = dstLineOffset;
                src1LineOffset += src1LineStride;
                src2LineOffset += src2LineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < dstWidth; ++w) {
                    d[dstPixelOffset] = s1[src1PixelOffset] * s2[src2PixelOffset];
                    src1PixelOffset += src1PixelStride;
                    src2PixelOffset += src2PixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }
}

