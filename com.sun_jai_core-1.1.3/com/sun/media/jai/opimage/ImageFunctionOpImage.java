/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageFunction;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.SourcelessOpImage;

final class ImageFunctionOpImage
extends SourcelessOpImage {
    protected ImageFunction function;
    protected float xScale;
    protected float yScale;
    protected float xTrans;
    protected float yTrans;

    private static SampleModel sampleModelHelper(int numBands, ImageLayout layout) {
        SampleModel sampleModel;
        if (layout != null && layout.isValid(256)) {
            sampleModel = layout.getSampleModel(null);
            if (sampleModel.getNumBands() != numBands) {
                throw new RuntimeException(JaiI18N.getString("ImageFunctionRIF0"));
            }
        } else {
            sampleModel = RasterFactory.createBandedSampleModel(4, 1, 1, numBands);
        }
        return sampleModel;
    }

    public ImageFunctionOpImage(ImageFunction function, int minX, int minY, int width, int height, float xScale, float yScale, float xTrans, float yTrans, Map config, ImageLayout layout) {
        super(layout, config, ImageFunctionOpImage.sampleModelHelper(function.getNumElements() * (function.isComplex() ? 2 : 1), layout), minX, minY, width, height);
        this.function = function;
        this.xScale = xScale;
        this.yScale = yScale;
        this.xTrans = xTrans;
        this.yTrans = yTrans;
    }

    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        int dataType = this.sampleModel.getTransferType();
        int numBands = this.sampleModel.getNumBands();
        int length = this.width * this.height;
        Object data = dataType == 5 ? (this.function.isComplex() ? (Object)new double[2][length] : new double[length]) : (Object)(this.function.isComplex() ? (Object)new float[2][length] : new float[length]);
        if (dataType == 5) {
            Object real = this.function.isComplex() ? ((double[][])data)[0] : data;
            double[] imag = this.function.isComplex() ? ((double[][])data)[1] : null;
            int element = 0;
            for (int band = 0; band < numBands; ++band) {
                this.function.getElements((double)(this.xScale * ((float)destRect.x - this.xTrans)), (double)(this.yScale * ((float)destRect.y - this.yTrans)), (double)this.xScale, (double)this.yScale, destRect.width, destRect.height, element++, (double[])real, imag);
                dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, band, (double[])real);
                if (!this.function.isComplex()) continue;
                dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, ++band, imag);
            }
        } else {
            float[] real = this.function.isComplex() ? ((float[][])data)[0] : (float[])data;
            float[] imag = this.function.isComplex() ? ((float[][])data)[1] : null;
            int element = 0;
            for (int band = 0; band < numBands; ++band) {
                this.function.getElements(this.xScale * ((float)destRect.x - this.xTrans), this.yScale * ((float)destRect.y - this.yTrans), this.xScale, this.yScale, destRect.width, destRect.height, element++, real, imag);
                dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, band, real);
                if (!this.function.isComplex()) continue;
                dest.setSamples(destRect.x, destRect.y, destRect.width, destRect.height, ++band, imag);
            }
        }
    }
}

