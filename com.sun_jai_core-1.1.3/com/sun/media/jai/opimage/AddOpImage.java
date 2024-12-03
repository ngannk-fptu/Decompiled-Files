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

final class AddOpImage
extends PointOpImage {
    private int s1bd = 1;
    private int s2bd = 1;
    private boolean areBinarySampleModels = false;

    public AddOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout) {
        super(source1, source2, layout, config, true);
        if (ImageUtil.isBinary(this.getSampleModel()) && ImageUtil.isBinary(source1.getSampleModel()) && ImageUtil.isBinary(source2.getSampleModel())) {
            this.areBinarySampleModels = true;
        } else {
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
                this.s2bd = numBands2 == 1 ? 0 : 1;
            }
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterAccessor d;
        RasterAccessor s2;
        RasterAccessor s1;
        RasterFormatTag[] formatTags;
        if (this.areBinarySampleModels) {
            formatTags = this.getFormatTags();
            s1 = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
            s2 = new RasterAccessor(sources[1], destRect, formatTags[1], this.getSourceImage(1).getColorModel());
            d = new RasterAccessor(dest, destRect, formatTags[2], this.getColorModel());
            if (d.isBinary()) {
                byte[] src1Bits = s1.getBinaryDataArray();
                byte[] src2Bits = s2.getBinaryDataArray();
                byte[] dstBits = d.getBinaryDataArray();
                int length = dstBits.length;
                for (int i = 0; i < length; ++i) {
                    dstBits[i] = (byte)(src1Bits[i] | src2Bits[i]);
                }
                d.copyBinaryDataToRaster();
                return;
            }
        }
        formatTags = this.getFormatTags();
        s1 = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        s2 = new RasterAccessor(sources[1], destRect, formatTags[1], this.getSourceImage(1).getColorModel());
        d = new RasterAccessor(dest, destRect, formatTags[2], this.getColorModel());
        switch (d.getDataType()) {
            case 0: {
                this.computeRectByte(s1, s2, d);
                break;
            }
            case 1: {
                this.computeRectUShort(s1, s2, d);
                break;
            }
            case 2: {
                this.computeRectShort(s1, s2, d);
                break;
            }
            case 3: {
                this.computeRectInt(s1, s2, d);
                break;
            }
            case 4: {
                this.computeRectFloat(s1, s2, d);
                break;
            }
            case 5: {
                this.computeRectDouble(s1, s2, d);
            }
        }
        if (d.needsClamping()) {
            d.clampDataArrays();
        }
        d.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        byte[][] s1Data = src1.getByteDataArrays();
        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        byte[][] s2Data = src2.getByteDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        byte[][] dData = dst.getByteDataArrays();
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < bands) {
            byte[] s1 = s1Data[s1b];
            byte[] s2 = s2Data[s2b];
            byte[] d = dData[b];
            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                dLineOffset += dLineStride;
                int sum = 0;
                for (int w = 0; w < dwidth; ++w) {
                    sum = (s1[s1PixelOffset] & 0xFF) + (s2[s2PixelOffset] & 0xFF);
                    d[dPixelOffset] = (byte)((sum << 23 >> 31 | sum) & 0xFF);
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void computeRectUShort(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        short[][] s1Data = src1.getShortDataArrays();
        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        short[][] s2Data = src2.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < bands) {
            short[] s1 = s1Data[s1b];
            short[] s2 = s2Data[s2b];
            short[] d = dData[b];
            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    d[dPixelOffset] = ImageUtil.clampUShortPositive((s1[s1PixelOffset] & 0xFFFF) + (s2[s2PixelOffset] & 0xFFFF));
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void computeRectShort(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        short[][] s1Data = src1.getShortDataArrays();
        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        short[][] s2Data = src2.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < bands) {
            short[] s1 = s1Data[s1b];
            short[] s2 = s2Data[s2b];
            short[] d = dData[b];
            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    d[dPixelOffset] = ImageUtil.clampShort(s1[s1PixelOffset] + s2[s2PixelOffset]);
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void computeRectInt(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        int[][] s1Data = src1.getIntDataArrays();
        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        int[][] s2Data = src2.getIntDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        int[][] dData = dst.getIntDataArrays();
        switch (this.sampleModel.getTransferType()) {
            case 0: {
                int b = 0;
                int s1b = 0;
                int s2b = 0;
                while (b < bands) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] d = dData[b];
                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int dLineOffset = dBandOffsets[b];
                    for (int h = 0; h < dheight; ++h) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int dPixelOffset = dLineOffset;
                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        dLineOffset += dLineStride;
                        int sum = 0;
                        for (int w = 0; w < dwidth; ++w) {
                            sum = (s1[s1PixelOffset] & 0xFF) + (s2[s2PixelOffset] & 0xFF);
                            d[dPixelOffset] = (sum << 23 >> 31 | sum) & 0xFF;
                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            dPixelOffset += dPixelStride;
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
                while (b < bands) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] d = dData[b];
                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int dLineOffset = dBandOffsets[b];
                    for (int h = 0; h < dheight; ++h) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int dPixelOffset = dLineOffset;
                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        dLineOffset += dLineStride;
                        for (int w = 0; w < dwidth; ++w) {
                            d[dPixelOffset] = ImageUtil.clampUShortPositive((s1[s1PixelOffset] & 0xFFFF) + (s2[s2PixelOffset] & 0xFFFF));
                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            dPixelOffset += dPixelStride;
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
                while (b < bands) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] d = dData[b];
                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int dLineOffset = dBandOffsets[b];
                    for (int h = 0; h < dheight; ++h) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int dPixelOffset = dLineOffset;
                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        dLineOffset += dLineStride;
                        for (int w = 0; w < dwidth; ++w) {
                            d[dPixelOffset] = ImageUtil.clampShort(s1[s1PixelOffset] + s2[s2PixelOffset]);
                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            dPixelOffset += dPixelStride;
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
                while (b < bands) {
                    int[] s1 = s1Data[s1b];
                    int[] s2 = s2Data[s2b];
                    int[] d = dData[b];
                    int s1LineOffset = s1BandOffsets[s1b];
                    int s2LineOffset = s2BandOffsets[s2b];
                    int dLineOffset = dBandOffsets[b];
                    for (int h = 0; h < dheight; ++h) {
                        int s1PixelOffset = s1LineOffset;
                        int s2PixelOffset = s2LineOffset;
                        int dPixelOffset = dLineOffset;
                        s1LineOffset += s1LineStride;
                        s2LineOffset += s2LineStride;
                        dLineOffset += dLineStride;
                        for (int w = 0; w < dwidth; ++w) {
                            d[dPixelOffset] = ImageUtil.clampInt((long)s1[s1PixelOffset] + (long)s2[s2PixelOffset]);
                            s1PixelOffset += s1PixelStride;
                            s2PixelOffset += s2PixelStride;
                            dPixelOffset += dPixelStride;
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

    private void computeRectFloat(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        float[][] s1Data = src1.getFloatDataArrays();
        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        float[][] s2Data = src2.getFloatDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        float[][] dData = dst.getFloatDataArrays();
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < bands) {
            float[] s1 = s1Data[s1b];
            float[] s2 = s2Data[s2b];
            float[] d = dData[b];
            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    d[dPixelOffset] = s1[s1PixelOffset] + s2[s2PixelOffset];
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }

    private void computeRectDouble(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst) {
        int s1LineStride = src1.getScanlineStride();
        int s1PixelStride = src1.getPixelStride();
        int[] s1BandOffsets = src1.getBandOffsets();
        double[][] s1Data = src1.getDoubleDataArrays();
        int s2LineStride = src2.getScanlineStride();
        int s2PixelStride = src2.getPixelStride();
        int[] s2BandOffsets = src2.getBandOffsets();
        double[][] s2Data = src2.getDoubleDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int bands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        double[][] dData = dst.getDoubleDataArrays();
        int b = 0;
        int s1b = 0;
        int s2b = 0;
        while (b < bands) {
            double[] s1 = s1Data[s1b];
            double[] s2 = s2Data[s2b];
            double[] d = dData[b];
            int s1LineOffset = s1BandOffsets[s1b];
            int s2LineOffset = s2BandOffsets[s2b];
            int dLineOffset = dBandOffsets[b];
            for (int h = 0; h < dheight; ++h) {
                int s1PixelOffset = s1LineOffset;
                int s2PixelOffset = s2LineOffset;
                int dPixelOffset = dLineOffset;
                s1LineOffset += s1LineStride;
                s2LineOffset += s2LineStride;
                dLineOffset += dLineStride;
                for (int w = 0; w < dwidth; ++w) {
                    d[dPixelOffset] = s1[s1PixelOffset] + s2[s2PixelOffset];
                    s1PixelOffset += s1PixelStride;
                    s2PixelOffset += s2PixelStride;
                    dPixelOffset += dPixelStride;
                }
            }
            ++b;
            s1b += this.s1bd;
            s2b += this.s2bd;
        }
    }
}

