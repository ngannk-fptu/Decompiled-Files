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

final class MagnitudePhaseOpImage
extends PointOpImage {
    public static final int MAGNITUDE = 1;
    public static final int MAGNITUDE_SQUARED = 2;
    public static final int PHASE = 3;
    protected int operationType;
    private double phaseGain = 1.0;
    private double phaseBias = 0.0;

    public MagnitudePhaseOpImage(RenderedImage source, Map config, ImageLayout layout, int operationType) {
        super(source, layout, config, true);
        int numBands;
        this.operationType = operationType;
        boolean needNewSampleModel = false;
        int dataType = this.sampleModel.getTransferType();
        if (layout != null && dataType != layout.getSampleModel(source).getTransferType()) {
            dataType = layout.getSampleModel(source).getTransferType();
            needNewSampleModel = true;
        }
        if ((numBands = this.sampleModel.getNumBands()) > source.getSampleModel().getNumBands() / 2) {
            numBands = source.getSampleModel().getNumBands() / 2;
            needNewSampleModel = true;
        }
        if (needNewSampleModel) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, dataType, this.sampleModel.getWidth(), this.sampleModel.getHeight(), numBands);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        if (operationType == 3) {
            switch (dataType) {
                case 0: {
                    this.phaseGain = 40.58451048843331;
                    this.phaseBias = Math.PI;
                    break;
                }
                case 2: {
                    this.phaseGain = 5215.030020292134;
                    this.phaseBias = Math.PI;
                    break;
                }
                case 1: {
                    this.phaseGain = 10430.219195527361;
                    this.phaseBias = Math.PI;
                    break;
                }
                case 3: {
                    this.phaseGain = 3.4178263762906086E8;
                    this.phaseBias = Math.PI;
                    break;
                }
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor srcAccessor = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (dstAccessor.getDataType()) {
            case 0: {
                this.computeRectByte(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 2: {
                this.computeRectShort(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 1: {
                this.computeRectUShort(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 3: {
                this.computeRectInt(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 4: {
                this.computeRectFloat(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            case 5: {
                this.computeRectDouble(srcAccessor, dstAccessor, destRect.height, destRect.width);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("MagnitudePhaseOpImage0"));
            }
        }
        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }
        dstAccessor.copyDataToRaster();
    }

    private void computeRectDouble(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numDstBands = this.sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; ++dstBand) {
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;
            double[] srcReal = srcAccessor.getDoubleDataArray(srcBandReal);
            double[] srcImag = srcAccessor.getDoubleDataArray(srcBandImag);
            double[] dstData = dstAccessor.getDoubleDataArray(dstBand);
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;
            for (int row = 0; row < numRows; ++row) {
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;
                switch (this.operationType) {
                    case 1: {
                        double imag;
                        double real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = Math.sqrt(real * real + imag * imag);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 2: {
                        double imag;
                        double real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = real * real + imag * imag;
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 3: {
                        double imag;
                        double real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = Math.atan2(imag, real);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                }
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectFloat(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numDstBands = this.sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; ++dstBand) {
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;
            float[] srcReal = srcAccessor.getFloatDataArray(srcBandReal);
            float[] srcImag = srcAccessor.getFloatDataArray(srcBandImag);
            float[] dstData = dstAccessor.getFloatDataArray(dstBand);
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;
            for (int row = 0; row < numRows; ++row) {
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;
                switch (this.operationType) {
                    case 1: {
                        float imag;
                        float real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampFloat(Math.sqrt(real * real + imag * imag));
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 2: {
                        float imag;
                        float real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = real * real + imag * imag;
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 3: {
                        float imag;
                        float real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampFloat(Math.atan2(imag, real));
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                }
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectInt(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numDstBands = this.sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; ++dstBand) {
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;
            int[] srcReal = srcAccessor.getIntDataArray(srcBandReal);
            int[] srcImag = srcAccessor.getIntDataArray(srcBandImag);
            int[] dstData = dstAccessor.getIntDataArray(dstBand);
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;
            for (int row = 0; row < numRows; ++row) {
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;
                switch (this.operationType) {
                    case 1: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampRoundInt(Math.sqrt(real * real + imag * imag));
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 2: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = real * real + imag * imag;
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 3: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampRoundInt((Math.atan2(imag, real) + this.phaseBias) * this.phaseGain);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                }
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectUShort(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numDstBands = this.sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; ++dstBand) {
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;
            short[] srcReal = srcAccessor.getShortDataArray(srcBandReal);
            short[] srcImag = srcAccessor.getShortDataArray(srcBandImag);
            short[] dstData = dstAccessor.getShortDataArray(dstBand);
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;
            for (int row = 0; row < numRows; ++row) {
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;
                switch (this.operationType) {
                    case 1: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal] & 0xFFFF;
                            imag = srcImag[srcPixelImag] & 0xFFFF;
                            dstData[dstPixel] = ImageUtil.clampRoundUShort(Math.sqrt(real * real + imag * imag));
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 2: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal] & 0xFFFF;
                            imag = srcImag[srcPixelImag] & 0xFFFF;
                            dstData[dstPixel] = ImageUtil.clampUShort(real * real + imag * imag);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 3: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal] & 0xFFFF;
                            imag = srcImag[srcPixelImag] & 0xFFFF;
                            dstData[dstPixel] = ImageUtil.clampRoundUShort((Math.atan2(imag, real) + this.phaseBias) * this.phaseGain);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                }
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectShort(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numDstBands = this.sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; ++dstBand) {
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;
            short[] srcReal = srcAccessor.getShortDataArray(srcBandReal);
            short[] srcImag = srcAccessor.getShortDataArray(srcBandImag);
            short[] dstData = dstAccessor.getShortDataArray(dstBand);
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;
            for (int row = 0; row < numRows; ++row) {
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;
                switch (this.operationType) {
                    case 1: {
                        short imag;
                        short real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampRoundShort(Math.sqrt(real * real + imag * imag));
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 2: {
                        short imag;
                        short real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampShort(real * real + imag * imag);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 3: {
                        short imag;
                        short real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal];
                            imag = srcImag[srcPixelImag];
                            dstData[dstPixel] = ImageUtil.clampRoundShort((Math.atan2(imag, real) + this.phaseBias) * this.phaseGain);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                }
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }

    private void computeRectByte(RasterAccessor srcAccessor, RasterAccessor dstAccessor, int numRows, int numCols) {
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int numDstBands = this.sampleModel.getNumBands();
        for (int dstBand = 0; dstBand < numDstBands; ++dstBand) {
            int srcBandReal = 2 * dstBand;
            int srcBandImag = srcBandReal + 1;
            byte[] srcReal = srcAccessor.getByteDataArray(srcBandReal);
            byte[] srcImag = srcAccessor.getByteDataArray(srcBandImag);
            byte[] dstData = dstAccessor.getByteDataArray(dstBand);
            int srcOffsetReal = srcAccessor.getBandOffset(srcBandReal);
            int srcOffsetImag = srcAccessor.getBandOffset(srcBandImag);
            int dstOffset = dstAccessor.getBandOffset(dstBand);
            int srcLineReal = srcOffsetReal;
            int srcLineImag = srcOffsetImag;
            int dstLine = dstOffset;
            for (int row = 0; row < numRows; ++row) {
                int srcPixelReal = srcLineReal;
                int srcPixelImag = srcLineImag;
                int dstPixel = dstLine;
                switch (this.operationType) {
                    case 1: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal] & 0xFF;
                            imag = srcImag[srcPixelImag] & 0xFF;
                            dstData[dstPixel] = ImageUtil.clampRoundByte(Math.sqrt(real * real + imag * imag));
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 2: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal] & 0xFF;
                            imag = srcImag[srcPixelImag] & 0xFF;
                            dstData[dstPixel] = ImageUtil.clampByte(real * real + imag * imag);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                    case 3: {
                        int imag;
                        int real;
                        int col;
                        for (col = 0; col < numCols; ++col) {
                            real = srcReal[srcPixelReal] & 0xFF;
                            imag = srcImag[srcPixelImag] & 0xFF;
                            dstData[dstPixel] = ImageUtil.clampRoundByte((Math.atan2(imag, real) + this.phaseBias) * this.phaseGain);
                            srcPixelReal += srcPixelStride;
                            srcPixelImag += srcPixelStride;
                            dstPixel += dstPixelStride;
                        }
                        break;
                    }
                }
                srcLineReal += srcScanlineStride;
                srcLineImag += srcScanlineStride;
                dstLine += dstScanlineStride;
            }
        }
    }
}

