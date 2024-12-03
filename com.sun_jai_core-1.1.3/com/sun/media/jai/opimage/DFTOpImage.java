/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.FFT;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.MathJAI;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.UntiledOpImage;
import javax.media.jai.operator.DFTDescriptor;

public class DFTOpImage
extends UntiledOpImage {
    FFT fft;
    protected boolean complexSrc;
    protected boolean complexDst;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, EnumeratedParameter dataNature) {
        int dataType;
        int newWidth;
        int newHeight;
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setMinX(source.getMinX());
        il.setMinY(source.getMinY());
        int currentWidth = il.getWidth(source);
        int currentHeight = il.getHeight(source);
        if (currentWidth == 1 && currentHeight == 1) {
            newHeight = 1;
            newWidth = 1;
        } else if (currentWidth == 1 && currentHeight > 1) {
            newWidth = 1;
            newHeight = MathJAI.nextPositivePowerOf2(currentHeight);
        } else if (currentWidth > 1 && currentHeight == 1) {
            newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
            newHeight = 1;
        } else {
            newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
            newHeight = MathJAI.nextPositivePowerOf2(currentHeight);
        }
        il.setWidth(newWidth);
        il.setHeight(newHeight);
        boolean isComplexSource = !dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX);
        boolean isComplexDest = !dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL);
        boolean createNewSampleModel = false;
        SampleModel srcSampleModel = source.getSampleModel();
        int requiredNumBands = srcSampleModel.getNumBands();
        if (isComplexSource && !isComplexDest) {
            requiredNumBands /= 2;
        } else if (!isComplexSource && isComplexDest) {
            requiredNumBands *= 2;
        }
        SampleModel sm = il.getSampleModel(source);
        int numBands = sm.getNumBands();
        if (numBands != requiredNumBands) {
            numBands = requiredNumBands;
            createNewSampleModel = true;
        }
        if ((dataType = sm.getTransferType()) != 4 && dataType != 5) {
            dataType = 4;
            createNewSampleModel = true;
        }
        if (createNewSampleModel) {
            sm = RasterFactory.createComponentSampleModel(sm, dataType, newWidth, newHeight, numBands);
            il.setSampleModel(sm);
            ColorModel cm = il.getColorModel(null);
            if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
                il.unsetValid(512);
            }
        }
        return il;
    }

    public DFTOpImage(RenderedImage source, Map config, ImageLayout layout, EnumeratedParameter dataNature, FFT fft) {
        super(source, config, DFTOpImage.layoutHelper(layout, source, dataNature));
        this.fft = fft;
        this.complexSrc = !dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX);
        this.complexDst = !dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL);
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        if (destRect.width == 1 && destRect.height == 1) {
            int nDstBands = this.sampleModel.getNumBands();
            double[] srcPixel = new double[source.getSampleModel().getNumBands()];
            source.getPixel(destRect.x, destRect.y, srcPixel);
            if (this.complexSrc && this.complexDst) {
                dest.setPixel(destRect.x, destRect.y, srcPixel);
            } else if (this.complexSrc) {
                for (int i = 0; i < nDstBands; ++i) {
                    dest.setSample(destRect.x, destRect.y, i, srcPixel[2 * i]);
                }
            } else if (this.complexDst) {
                for (int i = 0; i < nDstBands; ++i) {
                    dest.setSample(destRect.x, destRect.y, i, i % 2 == 0 ? srcPixel[i / 2] : 0.0);
                }
            } else {
                throw new RuntimeException(JaiI18N.getString("DFTOpImage1"));
            }
            return;
        }
        this.fft.setLength(destRect.width > 1 ? this.getWidth() : this.getHeight());
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        int srcX = source.getMinX();
        int srcY = source.getMinY();
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor srcAccessor = new RasterAccessor(source, new Rectangle(srcX, srcY, srcWidth, srcHeight), formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        int srcDataType = srcAccessor.getDataType();
        int dstDataType = dstAccessor.getDataType();
        int srcPixelStride = srcAccessor.getPixelStride();
        int srcScanlineStride = srcAccessor.getScanlineStride();
        int dstPixelStride = dstAccessor.getPixelStride();
        int dstScanlineStride = dstAccessor.getScanlineStride();
        int dstPixelStrideImag = 1;
        int dstLineStrideImag = destRect.width;
        if (this.complexDst) {
            dstPixelStrideImag = dstPixelStride;
            dstLineStrideImag = dstScanlineStride;
        }
        int srcBandIndex = 0;
        int srcBandStride = this.complexSrc ? 2 : 1;
        int dstBandIndex = 0;
        int dstBandStride = this.complexDst ? 2 : 1;
        int numComponents = this.complexDst ? dest.getSampleModel().getNumBands() / 2 : dest.getSampleModel().getNumBands();
        for (int comp = 0; comp < numComponents; ++comp) {
            int dstOffsetImag;
            int dstOffsetReal;
            int srcOffsetImag;
            int srcOffsetReal;
            Object srcReal = srcAccessor.getDataArray(srcBandIndex);
            Object srcImag = null;
            if (this.complexSrc) {
                srcImag = srcAccessor.getDataArray(srcBandIndex + 1);
            }
            Object dstReal = dstAccessor.getDataArray(dstBandIndex);
            Object dstImag = null;
            dstImag = this.complexDst ? dstAccessor.getDataArray(dstBandIndex + 1) : (dstDataType == 4 ? (Object)new float[destRect.width * destRect.height] : (Object)new double[destRect.width * destRect.height]);
            if (destRect.width > 1) {
                this.fft.setLength(this.getWidth());
                srcOffsetReal = srcAccessor.getBandOffset(srcBandIndex);
                srcOffsetImag = 0;
                if (this.complexSrc) {
                    srcOffsetImag = srcAccessor.getBandOffset(srcBandIndex + 1);
                }
                dstOffsetReal = dstAccessor.getBandOffset(dstBandIndex);
                dstOffsetImag = 0;
                if (this.complexDst) {
                    dstOffsetImag = dstAccessor.getBandOffset(dstBandIndex + 1);
                }
                for (int row = 0; row < srcHeight; ++row) {
                    this.fft.setData(srcDataType, srcReal, srcOffsetReal, srcPixelStride, srcImag, srcOffsetImag, srcPixelStride, srcWidth);
                    this.fft.transform();
                    this.fft.getData(dstDataType, dstReal, dstOffsetReal, dstPixelStride, dstImag, dstOffsetImag, dstPixelStrideImag);
                    srcOffsetReal += srcScanlineStride;
                    srcOffsetImag += srcScanlineStride;
                    dstOffsetReal += dstScanlineStride;
                    dstOffsetImag += dstLineStrideImag;
                }
            }
            if (destRect.width == 1) {
                srcOffsetReal = srcAccessor.getBandOffset(srcBandIndex);
                srcOffsetImag = 0;
                if (this.complexSrc) {
                    srcOffsetImag = srcAccessor.getBandOffset(srcBandIndex + 1);
                }
                dstOffsetReal = dstAccessor.getBandOffset(dstBandIndex);
                dstOffsetImag = 0;
                if (this.complexDst) {
                    dstOffsetImag = dstAccessor.getBandOffset(dstBandIndex + 1);
                }
                this.fft.setData(srcDataType, srcReal, srcOffsetReal, srcScanlineStride, srcImag, srcOffsetImag, srcScanlineStride, srcHeight);
                this.fft.transform();
                this.fft.getData(dstDataType, dstReal, dstOffsetReal, dstScanlineStride, dstImag, dstOffsetImag, dstLineStrideImag);
            } else if (destRect.height > 1) {
                this.fft.setLength(this.getHeight());
                int dstOffsetReal2 = dstAccessor.getBandOffset(dstBandIndex);
                int dstOffsetImag2 = 0;
                if (this.complexDst) {
                    dstOffsetImag2 = dstAccessor.getBandOffset(dstBandIndex + 1);
                }
                for (int col = 0; col < destRect.width; ++col) {
                    this.fft.setData(dstDataType, dstReal, dstOffsetReal2, dstScanlineStride, dstImag, dstOffsetImag2, dstLineStrideImag, destRect.height);
                    this.fft.transform();
                    this.fft.getData(dstDataType, dstReal, dstOffsetReal2, dstScanlineStride, this.complexDst ? dstImag : null, dstOffsetImag2, dstLineStrideImag);
                    dstOffsetReal2 += dstPixelStride;
                    dstOffsetImag2 += dstPixelStrideImag;
                }
            }
            srcBandIndex += srcBandStride;
            dstBandIndex += dstBandStride;
        }
        if (dstAccessor.needsClamping()) {
            dstAccessor.clampDataArrays();
        }
        dstAccessor.copyDataToRaster();
    }
}

