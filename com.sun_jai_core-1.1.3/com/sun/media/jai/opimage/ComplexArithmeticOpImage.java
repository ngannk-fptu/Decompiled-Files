/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
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

final class ComplexArithmeticOpImage
extends PointOpImage {
    protected boolean isDivision = false;
    private int[] s1r;
    private int[] s1i;
    private int[] s2r;
    private int[] s2i;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source) {
        SampleModel sm;
        int nBands;
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        if (il.isValid(256) && (nBands = (sm = il.getSampleModel(null)).getNumBands()) % 2 != 0) {
            sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), ++nBands);
            il.setSampleModel(sm);
            ColorModel cm = layout.getColorModel(null);
            if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
                il.unsetValid(512);
            }
        }
        return il;
    }

    public ComplexArithmeticOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout, boolean isDivision) {
        super(source1, source2, ComplexArithmeticOpImage.layoutHelper(layout, source1), config, true);
        this.isDivision = isDivision;
        int numBands1 = source1.getSampleModel().getNumBands();
        int numBands2 = source2.getSampleModel().getNumBands();
        int numBandsDst = Math.min(numBands1, numBands2);
        int numBandsFromHint = 0;
        if (layout != null) {
            numBandsFromHint = layout.getSampleModel(null).getNumBands();
        }
        if (layout != null && layout.isValid(256) && (numBands1 == 2 && numBands2 > 2 || numBands2 == 2 && numBands1 > 2 || numBands1 >= numBandsFromHint && numBands2 >= numBandsFromHint && numBandsFromHint > 0) && numBandsFromHint % 2 == 0) {
            numBandsDst = numBandsFromHint;
            numBandsDst = Math.min(Math.max(numBands1, numBands2), numBandsDst);
        }
        if (numBandsDst != this.sampleModel.getNumBands()) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, this.sampleModel.getTransferType(), this.sampleModel.getWidth(), this.sampleModel.getHeight(), numBandsDst);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        int numElements = this.sampleModel.getNumBands() / 2;
        this.s1r = new int[numElements];
        this.s1i = new int[numElements];
        this.s2r = new int[numElements];
        this.s2i = new int[numElements];
        int s1Inc = numBands1 > 2 ? 2 : 0;
        int s2Inc = numBands2 > 2 ? 2 : 0;
        int i1 = 0;
        int i2 = 0;
        for (int b = 0; b < numElements; ++b) {
            this.s1r[b] = i1;
            this.s1i[b] = i1 + 1;
            this.s2r[b] = i2;
            this.s2i[b] = i2 + 1;
            i1 += s1Inc;
            i2 += s2Inc;
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor src1Accessor = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor src2Accessor = new RasterAccessor(sources[1], destRect, formatTags[1], this.getSourceImage(1).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[2], this.getColorModel());
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.computeRectByte(src1Accessor, src2Accessor, dstAccessor);
                break;
            }
            case 2: {
                this.computeRectShort(src1Accessor, src2Accessor, dstAccessor);
                break;
            }
            case 1: {
                this.computeRectUShort(src1Accessor, src2Accessor, dstAccessor);
                break;
            }
            case 3: {
                this.computeRectInt(src1Accessor, src2Accessor, dstAccessor);
                break;
            }
            case 4: {
                this.computeRectFloat(src1Accessor, src2Accessor, dstAccessor);
                break;
            }
            case 5: {
                this.computeRectDouble(src1Accessor, src2Accessor, dstAccessor);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("ComplexArithmeticOpImage0"));
            }
        }
        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }
        dstAccessor.copyDataToRaster();
    }

    private void computeRectDouble(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor) {
        int numRows = dstAccessor.getHeight();
        int numCols = dstAccessor.getWidth();
        int src1PixelStride = src1Accessor.getPixelStride();
        int src1ScanlineStride = src1Accessor.getScanlineStride();
        int src2PixelStride = src2Accessor.getPixelStride();
        int src2ScanlineStride = src2Accessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numElements = this.sampleModel.getNumBands() / 2;
        for (int element = 0; element < numElements; ++element) {
            int realBand = 2 * element;
            int imagBand = realBand + 1;
            double[] src1Real = src1Accessor.getDoubleDataArray(this.s1r[element]);
            double[] src1Imag = src1Accessor.getDoubleDataArray(this.s1i[element]);
            double[] src2Real = src2Accessor.getDoubleDataArray(this.s2r[element]);
            double[] src2Imag = src2Accessor.getDoubleDataArray(this.s2i[element]);
            double[] dstReal = dstAccessor.getDoubleDataArray(realBand);
            double[] dstImag = dstAccessor.getDoubleDataArray(imagBand);
            int src1OffsetReal = src1Accessor.getBandOffset(this.s1r[element]);
            int src1OffsetImag = src1Accessor.getBandOffset(this.s1i[element]);
            int src2OffsetReal = src2Accessor.getBandOffset(this.s2r[element]);
            int src2OffsetImag = src2Accessor.getBandOffset(this.s2i[element]);
            int dstOffsetReal = dstAccessor.getBandOffset(realBand);
            int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
            int src1LineReal = src1OffsetReal;
            int src1LineImag = src1OffsetImag;
            int src2LineReal = src2OffsetReal;
            int src2LineImag = src2OffsetImag;
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            for (int row = 0; row < numRows; ++row) {
                double d;
                double c;
                double b;
                double a;
                int col;
                int src1PixelReal = src1LineReal;
                int src1PixelImag = src1LineImag;
                int src2PixelReal = src2LineReal;
                int src2PixelImag = src2LineImag;
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                if (this.isDivision) {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal];
                        b = src1Imag[src1PixelImag];
                        c = src2Real[src2PixelReal];
                        d = src2Imag[src2PixelImag];
                        double denom = c * c + d * d;
                        dstReal[dstPixelReal] = (a * c + b * d) / denom;
                        dstImag[dstPixelImag] = (b * c - a * d) / denom;
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                } else {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal];
                        b = src1Imag[src1PixelImag];
                        c = src2Real[src2PixelReal];
                        d = src2Imag[src2PixelImag];
                        dstReal[dstPixelReal] = a * c - b * d;
                        dstImag[dstPixelImag] = a * d + b * c;
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                }
                src1LineReal += src1ScanlineStride;
                src1LineImag += src1ScanlineStride;
                src2LineReal += src2ScanlineStride;
                src2LineImag += src2ScanlineStride;
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
            }
        }
    }

    private void computeRectFloat(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor) {
        int numRows = dstAccessor.getHeight();
        int numCols = dstAccessor.getWidth();
        int src1PixelStride = src1Accessor.getPixelStride();
        int src1ScanlineStride = src1Accessor.getScanlineStride();
        int src2PixelStride = src2Accessor.getPixelStride();
        int src2ScanlineStride = src2Accessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numElements = this.sampleModel.getNumBands() / 2;
        for (int element = 0; element < numElements; ++element) {
            int realBand = 2 * element;
            int imagBand = realBand + 1;
            float[] src1Real = src1Accessor.getFloatDataArray(this.s1r[element]);
            float[] src1Imag = src1Accessor.getFloatDataArray(this.s1i[element]);
            float[] src2Real = src2Accessor.getFloatDataArray(this.s2r[element]);
            float[] src2Imag = src2Accessor.getFloatDataArray(this.s2i[element]);
            float[] dstReal = dstAccessor.getFloatDataArray(realBand);
            float[] dstImag = dstAccessor.getFloatDataArray(imagBand);
            int src1OffsetReal = src1Accessor.getBandOffset(this.s1r[element]);
            int src1OffsetImag = src1Accessor.getBandOffset(this.s1i[element]);
            int src2OffsetReal = src2Accessor.getBandOffset(this.s2r[element]);
            int src2OffsetImag = src2Accessor.getBandOffset(this.s2i[element]);
            int dstOffsetReal = dstAccessor.getBandOffset(realBand);
            int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
            int src1LineReal = src1OffsetReal;
            int src1LineImag = src1OffsetImag;
            int src2LineReal = src2OffsetReal;
            int src2LineImag = src2OffsetImag;
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            for (int row = 0; row < numRows; ++row) {
                float d;
                float c;
                float b;
                float a;
                int col;
                int src1PixelReal = src1LineReal;
                int src1PixelImag = src1LineImag;
                int src2PixelReal = src2LineReal;
                int src2PixelImag = src2LineImag;
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                if (this.isDivision) {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal];
                        b = src1Imag[src1PixelImag];
                        c = src2Real[src2PixelReal];
                        d = src2Imag[src2PixelImag];
                        float denom = c * c + d * d;
                        dstReal[dstPixelReal] = (a * c + b * d) / denom;
                        dstImag[dstPixelImag] = (b * c - a * d) / denom;
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                } else {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal];
                        b = src1Imag[src1PixelImag];
                        c = src2Real[src2PixelReal];
                        d = src2Imag[src2PixelImag];
                        dstReal[dstPixelReal] = a * c - b * d;
                        dstImag[dstPixelImag] = a * d + b * c;
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                }
                src1LineReal += src1ScanlineStride;
                src1LineImag += src1ScanlineStride;
                src2LineReal += src2ScanlineStride;
                src2LineImag += src2ScanlineStride;
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
            }
        }
    }

    private void computeRectInt(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor) {
        int numRows = dstAccessor.getHeight();
        int numCols = dstAccessor.getWidth();
        int src1PixelStride = src1Accessor.getPixelStride();
        int src1ScanlineStride = src1Accessor.getScanlineStride();
        int src2PixelStride = src2Accessor.getPixelStride();
        int src2ScanlineStride = src2Accessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numElements = this.sampleModel.getNumBands() / 2;
        for (int element = 0; element < numElements; ++element) {
            int realBand = 2 * element;
            int imagBand = realBand + 1;
            int[] src1Real = src1Accessor.getIntDataArray(this.s1r[element]);
            int[] src1Imag = src1Accessor.getIntDataArray(this.s1i[element]);
            int[] src2Real = src2Accessor.getIntDataArray(this.s2r[element]);
            int[] src2Imag = src2Accessor.getIntDataArray(this.s2i[element]);
            int[] dstReal = dstAccessor.getIntDataArray(realBand);
            int[] dstImag = dstAccessor.getIntDataArray(imagBand);
            int src1OffsetReal = src1Accessor.getBandOffset(this.s1r[element]);
            int src1OffsetImag = src1Accessor.getBandOffset(this.s1i[element]);
            int src2OffsetReal = src2Accessor.getBandOffset(this.s2r[element]);
            int src2OffsetImag = src2Accessor.getBandOffset(this.s2i[element]);
            int dstOffsetReal = dstAccessor.getBandOffset(realBand);
            int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
            int src1LineReal = src1OffsetReal;
            int src1LineImag = src1OffsetImag;
            int src2LineReal = src2OffsetReal;
            int src2LineImag = src2OffsetImag;
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            for (int row = 0; row < numRows; ++row) {
                int col;
                int src1PixelReal = src1LineReal;
                int src1PixelImag = src1LineImag;
                int src2PixelReal = src2LineReal;
                int src2PixelImag = src2LineImag;
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                if (this.isDivision) {
                    for (col = 0; col < numCols; ++col) {
                        int a = src1Real[src1PixelReal];
                        int b = src1Imag[src1PixelImag];
                        int c = src2Real[src2PixelReal];
                        int d = src2Imag[src2PixelImag];
                        float denom = c * c + d * d;
                        dstReal[dstPixelReal] = ImageUtil.clampRoundInt((float)(a * c + b * d) / denom);
                        dstImag[dstPixelImag] = ImageUtil.clampRoundInt((float)(b * c - a * d) / denom);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                } else {
                    for (col = 0; col < numCols; ++col) {
                        long a = src1Real[src1PixelReal];
                        long b = src1Imag[src1PixelImag];
                        long c = src2Real[src2PixelReal];
                        long d = src2Imag[src2PixelImag];
                        dstReal[dstPixelReal] = ImageUtil.clampInt(a * c - b * d);
                        dstImag[dstPixelImag] = ImageUtil.clampInt(a * d + b * c);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                }
                src1LineReal += src1ScanlineStride;
                src1LineImag += src1ScanlineStride;
                src2LineReal += src2ScanlineStride;
                src2LineImag += src2ScanlineStride;
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
            }
        }
    }

    private void computeRectUShort(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor) {
        int numRows = dstAccessor.getHeight();
        int numCols = dstAccessor.getWidth();
        int src1PixelStride = src1Accessor.getPixelStride();
        int src1ScanlineStride = src1Accessor.getScanlineStride();
        int src2PixelStride = src2Accessor.getPixelStride();
        int src2ScanlineStride = src2Accessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numElements = this.sampleModel.getNumBands() / 2;
        for (int element = 0; element < numElements; ++element) {
            int realBand = 2 * element;
            int imagBand = realBand + 1;
            short[] src1Real = src1Accessor.getShortDataArray(this.s1r[element]);
            short[] src1Imag = src1Accessor.getShortDataArray(this.s1i[element]);
            short[] src2Real = src2Accessor.getShortDataArray(this.s2r[element]);
            short[] src2Imag = src2Accessor.getShortDataArray(this.s2i[element]);
            short[] dstReal = dstAccessor.getShortDataArray(realBand);
            short[] dstImag = dstAccessor.getShortDataArray(imagBand);
            int src1OffsetReal = src1Accessor.getBandOffset(this.s1r[element]);
            int src1OffsetImag = src1Accessor.getBandOffset(this.s1i[element]);
            int src2OffsetReal = src2Accessor.getBandOffset(this.s2r[element]);
            int src2OffsetImag = src2Accessor.getBandOffset(this.s2i[element]);
            int dstOffsetReal = dstAccessor.getBandOffset(realBand);
            int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
            int src1LineReal = src1OffsetReal;
            int src1LineImag = src1OffsetImag;
            int src2LineReal = src2OffsetReal;
            int src2LineImag = src2OffsetImag;
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            for (int row = 0; row < numRows; ++row) {
                int d;
                int c;
                int b;
                int a;
                int col;
                int src1PixelReal = src1LineReal;
                int src1PixelImag = src1LineImag;
                int src2PixelReal = src2LineReal;
                int src2PixelImag = src2LineImag;
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                if (this.isDivision) {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal] & 0xFFFF;
                        b = src1Imag[src1PixelImag] & 0xFFFF;
                        c = src2Real[src2PixelReal] & 0xFFFF;
                        d = src2Imag[src2PixelImag] & 0xFFFF;
                        int denom = c * c + d * d;
                        dstReal[dstPixelReal] = ImageUtil.clampUShort((a * c + b * d) / denom);
                        dstImag[dstPixelImag] = ImageUtil.clampUShort((b * c - a * d) / denom);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                } else {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal] & 0xFFFF;
                        b = src1Imag[src1PixelImag] & 0xFFFF;
                        c = src2Real[src2PixelReal] & 0xFFFF;
                        d = src2Imag[src2PixelImag] & 0xFFFF;
                        dstReal[dstPixelReal] = ImageUtil.clampUShort(a * c - b * d);
                        dstImag[dstPixelImag] = ImageUtil.clampUShort(a * d + b * c);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                }
                src1LineReal += src1ScanlineStride;
                src1LineImag += src1ScanlineStride;
                src2LineReal += src2ScanlineStride;
                src2LineImag += src2ScanlineStride;
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
            }
        }
    }

    private void computeRectShort(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor) {
        int numRows = dstAccessor.getHeight();
        int numCols = dstAccessor.getWidth();
        int src1PixelStride = src1Accessor.getPixelStride();
        int src1ScanlineStride = src1Accessor.getScanlineStride();
        int src2PixelStride = src2Accessor.getPixelStride();
        int src2ScanlineStride = src2Accessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numElements = this.sampleModel.getNumBands() / 2;
        for (int element = 0; element < numElements; ++element) {
            int realBand = 2 * element;
            int imagBand = realBand + 1;
            short[] src1Real = src1Accessor.getShortDataArray(this.s1r[element]);
            short[] src1Imag = src1Accessor.getShortDataArray(this.s1i[element]);
            short[] src2Real = src2Accessor.getShortDataArray(this.s2r[element]);
            short[] src2Imag = src2Accessor.getShortDataArray(this.s2i[element]);
            short[] dstReal = dstAccessor.getShortDataArray(realBand);
            short[] dstImag = dstAccessor.getShortDataArray(imagBand);
            int src1OffsetReal = src1Accessor.getBandOffset(this.s1r[element]);
            int src1OffsetImag = src1Accessor.getBandOffset(this.s1i[element]);
            int src2OffsetReal = src2Accessor.getBandOffset(this.s2r[element]);
            int src2OffsetImag = src2Accessor.getBandOffset(this.s2i[element]);
            int dstOffsetReal = dstAccessor.getBandOffset(realBand);
            int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
            int src1LineReal = src1OffsetReal;
            int src1LineImag = src1OffsetImag;
            int src2LineReal = src2OffsetReal;
            int src2LineImag = src2OffsetImag;
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            for (int row = 0; row < numRows; ++row) {
                short d;
                short c;
                short b;
                short a;
                int col;
                int src1PixelReal = src1LineReal;
                int src1PixelImag = src1LineImag;
                int src2PixelReal = src2LineReal;
                int src2PixelImag = src2LineImag;
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                if (this.isDivision) {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal];
                        b = src1Imag[src1PixelImag];
                        c = src2Real[src2PixelReal];
                        d = src2Imag[src2PixelImag];
                        int denom = c * c + d * d;
                        dstReal[dstPixelReal] = ImageUtil.clampShort((a * c + b * d) / denom);
                        dstImag[dstPixelImag] = ImageUtil.clampShort((b * c - a * d) / denom);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                } else {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal];
                        b = src1Imag[src1PixelImag];
                        c = src2Real[src2PixelReal];
                        d = src2Imag[src2PixelImag];
                        dstReal[dstPixelReal] = ImageUtil.clampShort(a * c - b * d);
                        dstImag[dstPixelImag] = ImageUtil.clampShort(a * d + b * c);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                }
                src1LineReal += src1ScanlineStride;
                src1LineImag += src1ScanlineStride;
                src2LineReal += src2ScanlineStride;
                src2LineImag += src2ScanlineStride;
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
            }
        }
    }

    private void computeRectByte(RasterAccessor src1Accessor, RasterAccessor src2Accessor, RasterAccessor dstAccessor) {
        int numRows = dstAccessor.getHeight();
        int numCols = dstAccessor.getWidth();
        int src1PixelStride = src1Accessor.getPixelStride();
        int src1ScanlineStride = src1Accessor.getScanlineStride();
        int src2PixelStride = src2Accessor.getPixelStride();
        int src2ScanlineStride = src2Accessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numElements = this.sampleModel.getNumBands() / 2;
        for (int element = 0; element < numElements; ++element) {
            int realBand = 2 * element;
            int imagBand = realBand + 1;
            byte[] src1Real = src1Accessor.getByteDataArray(this.s1r[element]);
            byte[] src1Imag = src1Accessor.getByteDataArray(this.s1i[element]);
            byte[] src2Real = src2Accessor.getByteDataArray(this.s2r[element]);
            byte[] src2Imag = src2Accessor.getByteDataArray(this.s2i[element]);
            byte[] dstReal = dstAccessor.getByteDataArray(realBand);
            byte[] dstImag = dstAccessor.getByteDataArray(imagBand);
            int src1OffsetReal = src1Accessor.getBandOffset(this.s1r[element]);
            int src1OffsetImag = src1Accessor.getBandOffset(this.s1i[element]);
            int src2OffsetReal = src2Accessor.getBandOffset(this.s2r[element]);
            int src2OffsetImag = src2Accessor.getBandOffset(this.s2i[element]);
            int dstOffsetReal = dstAccessor.getBandOffset(realBand);
            int dstOffsetImag = dstAccessor.getBandOffset(imagBand);
            int src1LineReal = src1OffsetReal;
            int src1LineImag = src1OffsetImag;
            int src2LineReal = src2OffsetReal;
            int src2LineImag = src2OffsetImag;
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            for (int row = 0; row < numRows; ++row) {
                int d;
                int c;
                int b;
                int a;
                int col;
                int src1PixelReal = src1LineReal;
                int src1PixelImag = src1LineImag;
                int src2PixelReal = src2LineReal;
                int src2PixelImag = src2LineImag;
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                if (this.isDivision) {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal] & 0xFF;
                        b = src1Imag[src1PixelImag] & 0xFF;
                        c = src2Real[src2PixelReal] & 0xFF;
                        d = src2Imag[src2PixelImag] & 0xFF;
                        int denom = c * c + d * d;
                        dstReal[dstPixelReal] = ImageUtil.clampByte((a * c + b * d) / denom);
                        dstImag[dstPixelImag] = ImageUtil.clampByte((b * c - a * d) / denom);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                } else {
                    for (col = 0; col < numCols; ++col) {
                        a = src1Real[src1PixelReal] & 0xFF;
                        b = src1Imag[src1PixelImag] & 0xFF;
                        c = src2Real[src2PixelReal] & 0xFF;
                        d = src2Imag[src2PixelImag] & 0xFF;
                        dstReal[dstPixelReal] = ImageUtil.clampByte(a * c - b * d);
                        dstImag[dstPixelImag] = ImageUtil.clampByte(a * d + b * c);
                        src1PixelReal += src1PixelStride;
                        src1PixelImag += src1PixelStride;
                        src2PixelReal += src2PixelStride;
                        src2PixelImag += src2PixelStride;
                        dstPixelReal += dstPixelStride;
                        dstPixelImag += dstPixelStride;
                    }
                }
                src1LineReal += src1ScanlineStride;
                src1LineImag += src1ScanlineStride;
                src2LineReal += src2ScanlineStride;
                src2LineImag += src2ScanlineStride;
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
            }
        }
    }
}

