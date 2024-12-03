/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;

final class PolarToComplexOpImage
extends PointOpImage {
    private double phaseGain = 1.0;
    private double phaseBias = 0.0;

    public PolarToComplexOpImage(RenderedImage magnitude, RenderedImage phase, Map config, ImageLayout layout) {
        super(magnitude, phase, layout, config, true);
        int numBands = 2 * Math.min(magnitude.getSampleModel().getNumBands(), phase.getSampleModel().getNumBands());
        if (this.sampleModel.getNumBands() != numBands) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, this.sampleModel.getTransferType(), this.sampleModel.getWidth(), this.sampleModel.getHeight(), numBands);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        switch (phase.getSampleModel().getTransferType()) {
            case 0: {
                this.phaseGain = 0.024639942381096416;
                this.phaseBias = -Math.PI;
                break;
            }
            case 2: {
                this.phaseGain = 1.9175345033660654E-4;
                this.phaseBias = -Math.PI;
                break;
            }
            case 1: {
                this.phaseGain = 9.587526218325454E-5;
                this.phaseBias = -Math.PI;
                break;
            }
            case 3: {
                this.phaseGain = 2.925836159896768E-9;
                this.phaseBias = -Math.PI;
                break;
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor magAccessor = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor phsAccessor = new RasterAccessor(sources[1], destRect, formatTags[1], this.getSource(1).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[2], this.getColorModel());
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.computeRectByte(magAccessor, phsAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 2: {
                this.computeRectShort(magAccessor, phsAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 1: {
                this.computeRectUShort(magAccessor, phsAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 3: {
                this.computeRectInt(magAccessor, phsAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 4: {
                this.computeRectFloat(magAccessor, phsAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 5: {
                this.computeRectDouble(magAccessor, phsAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("PolarToComplexOpImage0"));
            }
        }
        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }
        dstAccessor.copyDataToRaster();
    }

    private void computeRectDouble(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int magPixelStride = magAccessor.getPixelStride();
        int magScanlineStride = magAccessor.getScanlineStride();
        int phsPixelStride = phsAccessor.getPixelStride();
        int phsScanlineStride = phsAccessor.getScanlineStride();
        int numComponents = this.sampleModel.getNumBands() / 2;
        for (int component = 0; component < numComponents; ++component) {
            int dstBandReal = 2 * component;
            int dstBandImag = dstBandReal + 1;
            double[] dstReal = dstAccessor.getDoubleDataArray(dstBandReal);
            double[] dstImag = dstAccessor.getDoubleDataArray(dstBandImag);
            double[] magData = magAccessor.getDoubleDataArray(component);
            double[] phsData = phsAccessor.getDoubleDataArray(component);
            int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
            int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
            int magOffset = magAccessor.getBandOffset(component);
            int phsOffset = phsAccessor.getBandOffset(component);
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            int magLine = magOffset;
            int phsLine = phsOffset;
            for (int row = 0; row < numRows; ++row) {
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                int magPixel = magLine;
                int phsPixel = phsLine;
                for (int col = 0; col < numCols; ++col) {
                    double mag = magData[magPixel];
                    double phs = phsData[phsPixel] * this.phaseGain + this.phaseBias;
                    dstReal[dstPixelReal] = mag * Math.cos(phs);
                    dstImag[dstPixelImag] = mag * Math.sin(phs);
                    dstPixelReal += dstPixelStride;
                    dstPixelImag += dstPixelStride;
                    magPixel += magPixelStride;
                    phsPixel += phsPixelStride;
                }
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
                magLine += magScanlineStride;
                phsLine += phsScanlineStride;
            }
        }
    }

    private void computeRectFloat(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int magPixelStride = magAccessor.getPixelStride();
        int magScanlineStride = magAccessor.getScanlineStride();
        int phsPixelStride = phsAccessor.getPixelStride();
        int phsScanlineStride = phsAccessor.getScanlineStride();
        int numComponents = this.sampleModel.getNumBands() / 2;
        for (int component = 0; component < numComponents; ++component) {
            int dstBandReal = 2 * component;
            int dstBandImag = dstBandReal + 1;
            float[] dstReal = dstAccessor.getFloatDataArray(dstBandReal);
            float[] dstImag = dstAccessor.getFloatDataArray(dstBandImag);
            float[] magData = magAccessor.getFloatDataArray(component);
            float[] phsData = phsAccessor.getFloatDataArray(component);
            int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
            int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
            int magOffset = magAccessor.getBandOffset(component);
            int phsOffset = phsAccessor.getBandOffset(component);
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            int magLine = magOffset;
            int phsLine = phsOffset;
            for (int row = 0; row < numRows; ++row) {
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                int magPixel = magLine;
                int phsPixel = phsLine;
                for (int col = 0; col < numCols; ++col) {
                    double mag = magData[magPixel];
                    double phs = (double)phsData[phsPixel] * this.phaseGain + this.phaseBias;
                    dstReal[dstPixelReal] = ImageUtil.clampFloat(mag * Math.cos(phs));
                    dstImag[dstPixelImag] = ImageUtil.clampFloat(mag * Math.sin(phs));
                    dstPixelReal += dstPixelStride;
                    dstPixelImag += dstPixelStride;
                    magPixel += magPixelStride;
                    phsPixel += phsPixelStride;
                }
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
                magLine += magScanlineStride;
                phsLine += phsScanlineStride;
            }
        }
    }

    private void computeRectInt(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int magPixelStride = magAccessor.getPixelStride();
        int magScanlineStride = magAccessor.getScanlineStride();
        int phsPixelStride = phsAccessor.getPixelStride();
        int phsScanlineStride = phsAccessor.getScanlineStride();
        int numComponents = this.sampleModel.getNumBands() / 2;
        for (int component = 0; component < numComponents; ++component) {
            int dstBandReal = 2 * component;
            int dstBandImag = dstBandReal + 1;
            int[] dstReal = dstAccessor.getIntDataArray(dstBandReal);
            int[] dstImag = dstAccessor.getIntDataArray(dstBandImag);
            int[] magData = magAccessor.getIntDataArray(component);
            int[] phsData = phsAccessor.getIntDataArray(component);
            int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
            int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
            int magOffset = magAccessor.getBandOffset(component);
            int phsOffset = phsAccessor.getBandOffset(component);
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            int magLine = magOffset;
            int phsLine = phsOffset;
            for (int row = 0; row < numRows; ++row) {
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                int magPixel = magLine;
                int phsPixel = phsLine;
                for (int col = 0; col < numCols; ++col) {
                    double mag = magData[magPixel];
                    double phs = (double)phsData[phsPixel] * this.phaseGain + this.phaseBias;
                    dstReal[dstPixelReal] = ImageUtil.clampRoundInt(mag * Math.cos(phs));
                    dstImag[dstPixelImag] = ImageUtil.clampRoundInt(mag * Math.sin(phs));
                    dstPixelReal += dstPixelStride;
                    dstPixelImag += dstPixelStride;
                    magPixel += magPixelStride;
                    phsPixel += phsPixelStride;
                }
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
                magLine += magScanlineStride;
                phsLine += phsScanlineStride;
            }
        }
    }

    private void computeRectUShort(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int magPixelStride = magAccessor.getPixelStride();
        int magScanlineStride = magAccessor.getScanlineStride();
        int phsPixelStride = phsAccessor.getPixelStride();
        int phsScanlineStride = phsAccessor.getScanlineStride();
        int numComponents = this.sampleModel.getNumBands() / 2;
        for (int component = 0; component < numComponents; ++component) {
            int dstBandReal = 2 * component;
            int dstBandImag = dstBandReal + 1;
            short[] dstReal = dstAccessor.getShortDataArray(dstBandReal);
            short[] dstImag = dstAccessor.getShortDataArray(dstBandImag);
            short[] magData = magAccessor.getShortDataArray(component);
            short[] phsData = phsAccessor.getShortDataArray(component);
            int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
            int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
            int magOffset = magAccessor.getBandOffset(component);
            int phsOffset = phsAccessor.getBandOffset(component);
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            int magLine = magOffset;
            int phsLine = phsOffset;
            for (int row = 0; row < numRows; ++row) {
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                int magPixel = magLine;
                int phsPixel = phsLine;
                for (int col = 0; col < numCols; ++col) {
                    double mag = magData[magPixel] & 0xFFFF;
                    double phs = (double)(phsData[phsPixel] & 0xFFFF) * this.phaseGain + this.phaseBias;
                    dstReal[dstPixelReal] = ImageUtil.clampRoundUShort(mag * Math.cos(phs));
                    dstImag[dstPixelImag] = ImageUtil.clampRoundUShort(mag * Math.sin(phs));
                    dstPixelReal += dstPixelStride;
                    dstPixelImag += dstPixelStride;
                    magPixel += magPixelStride;
                    phsPixel += phsPixelStride;
                }
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
                magLine += magScanlineStride;
                phsLine += phsScanlineStride;
            }
        }
    }

    private void computeRectShort(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int magPixelStride = magAccessor.getPixelStride();
        int magScanlineStride = magAccessor.getScanlineStride();
        int phsPixelStride = phsAccessor.getPixelStride();
        int phsScanlineStride = phsAccessor.getScanlineStride();
        int numComponents = this.sampleModel.getNumBands() / 2;
        for (int component = 0; component < numComponents; ++component) {
            int dstBandReal = 2 * component;
            int dstBandImag = dstBandReal + 1;
            short[] dstReal = dstAccessor.getShortDataArray(dstBandReal);
            short[] dstImag = dstAccessor.getShortDataArray(dstBandImag);
            short[] magData = magAccessor.getShortDataArray(component);
            short[] phsData = phsAccessor.getShortDataArray(component);
            int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
            int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
            int magOffset = magAccessor.getBandOffset(component);
            int phsOffset = phsAccessor.getBandOffset(component);
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            int magLine = magOffset;
            int phsLine = phsOffset;
            for (int row = 0; row < numRows; ++row) {
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                int magPixel = magLine;
                int phsPixel = phsLine;
                for (int col = 0; col < numCols; ++col) {
                    double mag = magData[magPixel];
                    double phs = (double)phsData[phsPixel] * this.phaseGain + this.phaseBias;
                    dstReal[dstPixelReal] = ImageUtil.clampRoundShort(mag * Math.cos(phs));
                    dstImag[dstPixelImag] = ImageUtil.clampRoundShort(mag * Math.sin(phs));
                    dstPixelReal += dstPixelStride;
                    dstPixelImag += dstPixelStride;
                    magPixel += magPixelStride;
                    phsPixel += phsPixelStride;
                }
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
                magLine += magScanlineStride;
                phsLine += phsScanlineStride;
            }
        }
    }

    private void computeRectByte(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int magPixelStride = magAccessor.getPixelStride();
        int magScanlineStride = magAccessor.getScanlineStride();
        int phsPixelStride = phsAccessor.getPixelStride();
        int phsScanlineStride = phsAccessor.getScanlineStride();
        int numComponents = this.sampleModel.getNumBands() / 2;
        for (int component = 0; component < numComponents; ++component) {
            int dstBandReal = 2 * component;
            int dstBandImag = dstBandReal + 1;
            byte[] dstReal = dstAccessor.getByteDataArray(dstBandReal);
            byte[] dstImag = dstAccessor.getByteDataArray(dstBandImag);
            byte[] magData = magAccessor.getByteDataArray(component);
            byte[] phsData = phsAccessor.getByteDataArray(component);
            int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
            int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
            int magOffset = magAccessor.getBandOffset(component);
            int phsOffset = phsAccessor.getBandOffset(component);
            int dstLineReal = dstOffsetReal;
            int dstLineImag = dstOffsetImag;
            int magLine = magOffset;
            int phsLine = phsOffset;
            for (int row = 0; row < numRows; ++row) {
                int dstPixelReal = dstLineReal;
                int dstPixelImag = dstLineImag;
                int magPixel = magLine;
                int phsPixel = phsLine;
                for (int col = 0; col < numCols; ++col) {
                    double mag = magData[magPixel] & 0xFF;
                    double phs = (double)(phsData[phsPixel] & 0xFF) * this.phaseGain + this.phaseBias;
                    dstReal[dstPixelReal] = ImageUtil.clampRoundByte(mag * Math.cos(phs));
                    dstImag[dstPixelImag] = ImageUtil.clampRoundByte(mag * Math.sin(phs));
                    dstPixelReal += dstPixelStride;
                    dstPixelImag += dstPixelStride;
                    magPixel += magPixelStride;
                    phsPixel += phsPixelStride;
                }
                dstLineReal += dstScanlineStride;
                dstLineImag += dstScanlineStride;
                magLine += magScanlineStride;
                phsLine += phsScanlineStride;
            }
        }
    }
}

